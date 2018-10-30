package org.familly.multimemo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.IOException;

class VoiceRecorder implements MediaPlayer.OnCompletionListener,MediaPlayer.OnErrorListener {
    static final String TAG = "DEBUG";

    static final String SAMPLE_PATH_KEY = "sample_path";
    static final String SAMPLE_LENGTH_KEY = "sample_length";
    static final int IDLE_STATE = 0;
    private static final String TEMP_STORAGE = "/data/data/org.familly.multimemo/voice/";
    private static final String SAMPLE_PREFIX = "recording";
    private static final int SDCARD_ACCESS_ERROR = 1;
    private static final int INTERNAL_ERROR = 2;
    private static final int IN_CALL_RECORD_ERROR = 3;
    private static final int RECORDING_STATE = 1;


    Bundle recorderState;
    File mSampleFile = null;
    int mSampleLength = 0;
    OnStateChangedListener mOnStateChangedListener = null;
    int mState = IDLE_STATE;
    public static final int RECORDING_STAE = 1;
    public static final int PLAYING_STATE = 2;
    long mSampleStart = 0;
    MediaRecorder mRecorder = null;
    MediaPlayer mPlayer = null;

    //인터페이스
     public interface OnStateChangedListener {
       public abstract void onStateChanged(int state);

       public abstract void onError(int error);
    }

    //생성자
    public VoiceRecorder() {
    }

    //사용자정의 메소드 START
    public void saveState() {
        recorderState.putString(SAMPLE_PATH_KEY, mSampleFile.getAbsolutePath());
        recorderState.putInt(SAMPLE_LENGTH_KEY, mSampleLength);

    }

    public void restoreState(Bundle recorderState) {
        String samplePath = recorderState.getString(SAMPLE_PATH_KEY);
        if (samplePath == null) {
            return;
        }
        int sampleLength = recorderState.getInt(SAMPLE_LENGTH_KEY, -1);
        if (sampleLength == -1) {
            return;
        }
        File file = new File(samplePath);
        if (!file.exists()) {
            return;
        }
        if (mSampleFile != null && mSampleFile.getAbsolutePath().compareTo(file.getAbsolutePath()) == 0) {
            return;
        }
        delete();
        mSampleFile = file;
        mSampleLength = sampleLength;
        signalStateChanged(IDLE_STATE);
    }

    public void setInstateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }
    public int state() {
         return mState;
    }

    public int progress() {
        if (mState == RECORDING_STAE || mState == PLAYING_STATE) {
            return (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
        }
        return 0;
    }

    public int sampleLength() {
         return mSampleLength;
    }

    public File sampleFile() {
         return mSampleFile;
    }

    //레코드 상태를 재설정 한다.기존 샘플링이 존재하면 삭제한다.
    public void delete() {
        stop();
        if (mSampleFile != null) {
            mSampleFile.delete();
        }

        mSampleFile = null;
        mSampleLength = 0;

        signalStateChanged(IDLE_STATE);
    }

    public void startRecording(int outputfileformat, String extension, Context context) {
        stop();

        if (mSampleFile == null) {
            File sampleDir = new File(BasicInfo.FOLDER_VOICE);
            if (!sampleDir.canWrite()) {
                //기록할수 없을경우
                sampleDir = new File(TEMP_STORAGE);

                if (!sampleDir.isDirectory()) {
                    Log.d(TAG, "녹음폴더생성 : " + sampleDir);
                    
                    sampleDir.mkdirs();
                }
                try {
                    File.createTempFile(SAMPLE_PREFIX, extension, sampleDir);
                } catch (IOException e) {
                    setError(SDCARD_ACCESS_ERROR);
                    return;
                }
            }
            
        }
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(outputfileformat);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mSampleFile.getAbsolutePath());

        try {
            mRecorder.prepare();
        } catch (IOException e) {

            setError(INTERNAL_ERROR);
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        //녹음 할수 없을경우 예회발생에서 컨트롤 한다.
        try {
            mRecorder.start();
        } catch (RuntimeException e) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            boolean isInCall = audioManager.getMode() == AudioManager.MODE_IN_CALL;
            if (isInCall) {
                setError(IN_CALL_RECORD_ERROR);
            } else {
                setError(INTERNAL_ERROR);
            }
            mRecorder.reset();
            mRecorder.release();
            mRecorder = null;
            return;
        }
        System.currentTimeMillis();
        setState(RECORDING_STATE);

    }

    public void stopRecording() {
        if (mRecorder == null) {
            return;
        }
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        mSampleLength = (int) ((System.currentTimeMillis() - mSampleStart) / 1000);
        setState(IDLE_STATE);
    }

    public void stopPlayBack() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.start();
        mPlayer.release();
        mPlayer = null;
        setState(IDLE_STATE);
    }

    public void stop() {
        stopRecording();
        stopPlayBack();
    }

    private void setState(int state) {
        if (state == mState) {
            return;
        }
        mState = state;
        signalStateChanged(mState);
    }

    private void signalStateChanged(int state) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged(state);
        }
    }

    private void setError(int error) {
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onError(error);
        }
    }

    //END

    //구현메소드 start
    @Override
    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(SDCARD_ACCESS_ERROR);
        return true;
    }
    //end
}
