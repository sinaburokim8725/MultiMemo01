package org.familly.multimemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.File;

public class VoiceRecordingActivity extends AppCompatActivity implements VoiceRecorder.OnStateChangedListener {
    private static final String TAG = "DEBUG";
    public static final int WARNING_INSERT_SDCARD   = 1011;
    public static final int WARNING_DISK_SPACE_FULL = 1012;

    static final String SAMPLE_INTERRUPTED_KEY = "sample_interrupted";
    static final String RECORDER_STATE_KEY = "recorder_state";
    static final String MAX_FILE_SIZE_KEY = "max_file_size";

    static final int BITRATE_AMR  = 5900;
    static final int BITRATE_3GPP = 5900;
    static final String AUDIO_AMR = "audio/amr";
    static final String AUDIO_3GP = "audio/3gpp";
    public static final int RECORDING_RUNNING = 1;
    public static final int RECORDING_IDLE = 0;
    String mRequestedType = AUDIO_AMR;

    boolean isRecording;

    TextView mRecordingTimeText;
    TitleBitmapButton mStartStopBtn;
    TitleBitmapButton mCancelBtn;

    VoiceRecorder mRecorder;
    RemainingTimeCalculator mRemainingTimeCalculator;

    PowerManager pm;
    WakeLock mWakeLock;

    long mMaxFileSize = -1;
    boolean mSampleInterrupted = false;
    String mTimeFormat;
    long mRecordingTime;
    Handler mHandler = new Handler();

    public int mRecordingState = 0;

    private Runnable mUpdateTimer = new Runnable() {
        @Override
        public void run() {
            updateTimerView(mRecordingTimeText);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀 바 숨기기.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voice_recording);

        setBottomBtns();

        init(savedInstanceState);
    }

    //사용자 정의 메소드 start
    //
    public void setBottomBtns() {
        isRecording = false;
        mRecordingTimeText = (TextView) findViewById(R.id.recording_recordingTimeText);
        mRecordingTimeText.setText(R.string.minute_second);

        mStartStopBtn = (TitleBitmapButton) findViewById(R.id.recording_startStopBtn);
        mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_voice_record, 0, 0);
        mStartStopBtn.setText(R.string.audio_recording_start_title);

        mCancelBtn = (TitleBitmapButton) findViewById(R.id.recording_cancelBtn);

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    //녹화중지
                    stopVoiceRecording();
                    isRecording = false;
                } else {
                    //녹화시작
                    startVoiceRecording();
                    isRecording = true;
                }
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //
    @SuppressLint("InvalidWakeLockTag")
    public void init(Bundle bundle) {
        mRecorder = new VoiceRecorder();
        mRecorder.setOnStateChangedListener(this);

        mRemainingTimeCalculator = new RemainingTimeCalculator();

        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MediGalaxy");

        if (bundle != null) {
            Bundle recorderState = bundle.getBundle(RECORDER_STATE_KEY);
            if (recorderState != null) {
                mRecorder.restoreState(recorderState);
                mSampleInterrupted = recorderState.getBoolean(SAMPLE_INTERRUPTED_KEY, false);
                mMaxFileSize = recorderState.getLong(MAX_FILE_SIZE_KEY, -1);
            }
        }
        mTimeFormat = "%02d:%02d";
        updateUi(mRecordingTimeText);
    }


    public void updateUi(TextView textView) {
        updateTimerView(textView);
    }

    private void updateTimerView(TextView textView) {
        int state = mRecorder.state();

        boolean ongoing = state == VoiceRecorder.RECORDING_STAE || state == VoiceRecorder.PLAYING_STATE;
        long time = ongoing ? mRecorder.progress() : mRecorder.sampleLength();
        String timeStr = String.format(mTimeFormat, time / 60, time % 60);
        textView.setText(timeStr);
        mRecordingTime = time;

        if (state == VoiceRecorder.PLAYING_STATE) {

        } else if (state == VoiceRecorder.RECORDING_STAE){
            updateTimeRemaining();
        }
        if (ongoing) {
            mHandler.postDelayed(mUpdateTimer, 1000);
        }
    }

    private void updateTimeRemaining() {
        long rt = mRemainingTimeCalculator.timeRemaining();
        if (rt <= 0) {
            mSampleInterrupted = true;
            int limit = mRemainingTimeCalculator.currentLowerLimit();
            switch (limit) {
                case RemainingTimeCalculator.DISK_SPACE_LIMIT:
                    break;
                case RemainingTimeCalculator.FILE_SIZE_LIMIT:
                    break;
                default:
                    break;
            }
            mRecorder.stop();
            return;
        }
        //여분의 녹화시간이 있을경우
        String timeStr = "";
        if (rt < 60) {
            timeStr = String.format("%d min available", rt);
        } else if (rt < 540) {
            timeStr = String.format("%d s available", rt / 60 + 1);
        }
    }

    public void startVoiceRecording() {
        voiceRecordingStart();

        mStartStopBtn.setText(R.string.audio_recording_stop_title);
        mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_voice_stop, 0, 0);
        mRecordingTimeText.setText(R.string.minute_second);
    }

    public void voiceRecordingStart() {
        mRemainingTimeCalculator.reset();
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd카드 연결되지 않았을 경우
            mSampleInterrupted = true;
            showDialog(WARNING_INSERT_SDCARD);

            stopAudioPlayback();

            if (AUDIO_AMR.equals(mRequestedType)) {
                mRemainingTimeCalculator.setBitRate(BITRATE_AMR);
                mRecorder.startRecording(MediaRecorder.OutputFormat.RAW_AMR, ".amr", this);
            } else if (AUDIO_3GP.equals(mRequestedType)) {
                mRemainingTimeCalculator.setBitRate(BITRATE_3GPP);
                mRecorder.startRecording(MediaRecorder.OutputFormat.THREE_GPP, ".3gpp", this);
            } else {
                throw new IllegalArgumentException("Invalid output file type requested");
            }

            if (mMaxFileSize != -1) {
                mRemainingTimeCalculator.setFileSizeLimit(mRecorder.sampleFile(), mMaxFileSize);
            }
        } else if (!mRemainingTimeCalculator.diskSpaceAvailable()) {
            //용량초과일경우
            mSampleInterrupted = true;
            //sinaburokim onCreateDialog 재정의 하지 않았음.
            showDialog(WARNING_DISK_SPACE_FULL);
        } else {
            stopAudioPlayback();

            if (AUDIO_AMR.equals(mRequestedType)) {
                mRemainingTimeCalculator.setBitRate(BITRATE_AMR);
                mRecorder.startRecording(MediaRecorder.OutputFormat.RAW_AMR, ".amr", this);
            } else if (AUDIO_3GP.equals(mRequestedType)) {
                mRemainingTimeCalculator.setBitRate(BITRATE_3GPP);
                mRecorder.startRecording(MediaRecorder.OutputFormat.THREE_GPP, ".3gpp", this);
            } else {
                throw new IllegalArgumentException("Invalid output file type requested");
            }

            if (mMaxFileSize != -1) {
                mRemainingTimeCalculator.setFileSizeLimit(mRecorder.sampleFile(), mMaxFileSize);

            }

        }
        mRecordingState = RECORDING_RUNNING;
    }

    public void stopVoiceRecording() {
        mRecorder.stop();

        mStartStopBtn.setText(R.string.audio_recording_title);
        mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_voice_record, 0, 0);

        File tempFile = mRecorder.sampleFile();
        saveRcording(tempFile);

        mRecordingState = RECORDING_IDLE;
    }

    private void saveRcording(File tempFile) {
        checkVoiceFolder();
        String voiceName = "recorded";

        try {
            tempFile.renameTo(new File(BasicInfo.FOLDER_VOICE + voiceName));
            setResult(RESULT_OK);
        } catch (Exception e) {
            Log.e(TAG, "Exception in storing recording.", e);
        }
    }

    public void checkVoiceFolder() {
        File voiceFolder = new File(BasicInfo.FOLDER_VOICE);
        if (!voiceFolder.isDirectory()) {
            Log.d(TAG, "생성된 음성 폴더 : " + voiceFolder);

            voiceFolder.mkdirs();
        }
    }

    private void stopAudioPlayback() {
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");

        sendBroadcast(intent);
    }
    //end

    //재정의 및 구현체 메소드 start

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "VoiceRecordingActivity onConfiqurationChanged() 호출 :");
        updateUi(mRecordingTimeText);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "VoiceRecordingActivity onSaveInstanceState() 호출 :");
        if (mRecorder.sampleLength() == 0) {
            return;
        }
        Bundle recorderState = new Bundle();
        mRecorder.saveState(recorderState);
        recorderState.putBoolean(SAMPLE_INTERRUPTED_KEY, mSampleInterrupted);
        recorderState.putLong(MAX_FILE_SIZE_KEY, mMaxFileSize);

        outState.putBundle(RECORDER_STATE_KEY, recorderState);
    }

    //recorder 가 변경될때 호출된다.

    /**
     * A wake lock is a mechanism to indicate that your application needs to have the device stay on.
     * Any application using a WakeLock must request the android.permission.
     * WAKE_LOCK permission in an <uses-permission> element of the application's manifest.
     * Obtain a wake lock by calling new WakeLock(int, String).
     * Call acquire() to acquire the wake lock and force the device to stay on at the level that was requested when the wake lock was created.
     * Call release() when you are done and don't need the lock anymore.
     * It is very important to do this as soon as possible to avoid running down the device's battery excessively.
     *
     * wakelock은 응용 프로그램이 장치를 켜 놓아야 함을 나타내는 메커니즘입니다.
     * WakeLock을 사용하는 모든 응용 프로그램은 android.permission을 요청해야합니다.
     * 응용 프로그램 매니페스트의 <uses-permission> 요소에서 WAKE_LOCK 권한.
     * 새로운 WakeLock (int, String)을 호출하여 wake lock을 얻는다.
     * acquire()를 호출하여 wake lock을 획득하고 wake lock이 생성 될 때 요청 된 수준으로 장치를 강제로 유지합니다.
     * 완료되고 더 이상 잠금이 필요하지 않을때 release()를 호출합니다.
     * 장치의 배터리가 과도하게 소모되는 것을 피하려면 가능한 한 빨리 이 작업을 수행하는 것이 매우 중요합니다.
     * @param state
     */
    @Override
    public void onStateChanged(int state) {
        Log.d(TAG, "VoiceRecordingActivity onStateChanged() 호출 :");
        if (state == VoiceRecorder.PLAYING_STATE || state == VoiceRecorder.RECORDING_STATE) {
            mSampleInterrupted = false;

        }
        if (state == VoiceRecorder.RECORDING_STAE) {
            mWakeLock.acquire();
        } else {
            if (mWakeLock.isHeld()) {
                mWakeLock.release();
            }
        }
        updateUi(mRecordingTimeText);
    }

    @Override
    public void onError(int error) {
        Log.d(TAG, "VoiceRecordingActivity onError() 호출 :");
        String message = null;

        switch (error) {
            case VoiceRecorder.SDCARD_ACCESS_ERROR:
                message = String.valueOf(R.string.sdcard_access_error);
                break;
            case VoiceRecorder.INTERNAL_ERROR:
            case VoiceRecorder.IN_CALL_RECORD_ERROR:
                message = String.valueOf(R.string.in_call_record_error);
                break;
            default:
        }
        if (message != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.audio_recording_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.confirm_btn, null)
                    .setCancelable(false)
                    .show();
        }
    }


    //end
    //inner class 기타 start
    class RemainingTimeCalculator {
        public static final int UNKNOW_LIMIT = 0;
        private int mCurrentLowerLimit = UNKNOW_LIMIT;

        private File mSDCardDirectory;
        private File mRecordingFile;

        private long mMaxBytes;
        private long mBlocksChangedTime;
        private long mFileSizeChangedTime;
        private long mLastBlocks;
        private int mBytesPerSecond;
        private static final int DISK_SPACE_LIMIT = 2;
        private long mLastFileSize;
        public static final int FILE_SIZE_LIMIT = 1;

        public RemainingTimeCalculator() {
            mSDCardDirectory = Environment.getExternalStorageDirectory();
        }

        public void setFileSizeLimit(File file, long maxBytes) {
            mRecordingFile = file;
            mMaxBytes = maxBytes;
        }

        public void reset() {
            mCurrentLowerLimit = UNKNOW_LIMIT;
            mBlocksChangedTime = -1;
            mFileSizeChangedTime = -1;
        }

        public long timeRemaining() {
            Log.d(TAG, "RemainingTimeCalculator timeRemaining()");
            /**Retrieve overall information about the space on a filesystem.
             This is a wrapper for Unix statvfs().
             *파일 시스템의 여유공간에 대한 전체 정보를 구해냅니다.
             * 이것은 유닉스 statvfs ()를위한 래퍼이다.
             */
            StatFs fs = new StatFs(mSDCardDirectory.getAbsolutePath());
            long blocks = fs.getAvailableBlocks();
            long blockSize = fs.getBlockSize();
            long now = System.currentTimeMillis();
            Log.d(TAG, "blocks : " + blocks + ", blockSize : " + blockSize + " , now : " + now);


            if (mBlocksChangedTime == -1 || blocks != mLastBlocks) {
                mBlocksChangedTime = now;
                mLastBlocks = blocks;
            }

            long result = mLastBlocks * blockSize / mBytesPerSecond;
            Log.d(TAG, "(mLastBlocks * blockSize / mBytesPerSecond) => " + result);

            result -= (now - mBlocksChangedTime)/1000;
            Log.d(TAG, "result - (now - mBlocksChangedTime)/1000 :" + result);

            if (mRecordingFile == null) {
                mCurrentLowerLimit = DISK_SPACE_LIMIT;
                return result;
            }

            mRecordingFile = new File(mRecordingFile.getAbsolutePath());
            long fileSize = mRecordingFile.length();
            Log.d(TAG, "mFileSizeChangedTime : " + mFileSizeChangedTime + ", fileSize : "
                    + fileSize + ", mLastFileSize" + mLastFileSize);

            if (mFileSizeChangedTime == -1 || fileSize != mLastFileSize) {
                mFileSizeChangedTime = now;
                mLastFileSize = fileSize;
            }

            long result2 = (mMaxBytes-fileSize)/mBytesPerSecond;
            result2 -= (now-mFileSizeChangedTime)/1000;
            result2 -= 1;

            Log.d(TAG, "(result , result2) : " + result + " , " + result2);

            mCurrentLowerLimit = result < result2 ? DISK_SPACE_LIMIT : FILE_SIZE_LIMIT;

            Log.d(TAG, "mCurrentLowerLimit : " + mCurrentLowerLimit);
            //e두수중 작은수 반환
            return Math.min(result, result2);
        }

        public int currentLowerLimit() {
            return mCurrentLowerLimit;
        }

        public boolean diskSpaceAvailable() {
            StatFs fs = new StatFs(mSDCardDirectory.getAbsolutePath());
            return fs.getAvailableBlocks() > 1;
        }

        public void setBitRate(int bitRate) {
            mBytesPerSecond = bitRate/8;
        }

    }

    //end

}
