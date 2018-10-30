package org.familly.multimemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.File;

public class VoiceRecordingActivity extends AppCompatActivity implements VoiceRecorder.OnStateChangedListener {
    static final String TAG = "DEBUG";
    static final String SAMPLE_INTERRUPTED_KEY = "sample_interrupted";
    static final String RECORDER_STATE_KEY = "recorder_state";
    static final String MAX_FILE_SIZE_KEY = "max_file_size";
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

    @Override
    public void onStateChanged(int state) {

    }

    @Override
    public void onError(int error) {

    }


    //end
    //inner class 기타 start
    class RemainingTimeCalculator {
        private File mSDCardDirectory;
        private File mRecordingFile;
        private long mMaxBytes;
        public static final int UNKNOW_LIMIT = 0;
        private int mCurrentLowerLimit = UNKNOW_LIMIT;
        private long mBlocksChangedTime;
        private long mFileSizeChangedTime;

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


    }

    //end

}
