package org.familly.multimemo;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.IOException;

/**
 * 동영상 녹화 화면
 */
public class VideoRecordingActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";

    MediaRecorder mRecorder = null;
    TitleBitmapButton mStartStopBtn;
    boolean isStart = false;
    CameraSurfaceView mCameraSurfaceView;
    FrameLayout mRecordingAreaLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Retrieve the current Window for the activity.
         * This can be used to directly access parts of the Window API that are not available through Activity/Screen.
         * Activity 대한 현재 Window 획득한다.
         * 이것은 Activity / Screen을 통해 사용할 수없는 Window API 부분에 직접 액세스하는 데 사용할 수 있습니다.
         */
        final Window win = getWindow();

        /**
         *sinaburokim
         *상태바 숨기기
         * 참고: 상태바 보이기
         * getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
         */
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video_recording);

        //동영상 촬영 뷰를 프레임 레이아웃에 추가한다.
        mCameraSurfaceView = new CameraSurfaceView(getApplicationContext());

        mRecordingAreaLayout = (FrameLayout) findViewById(R.id.layout_recording_area);
        mRecordingAreaLayout.addView(mCameraSurfaceView);

        setRecordingBtn();
    }

    //사용자정의 start
    //녹화버튼 클릭시 동영상을 녹화한다.
    public void setRecordingBtn() {
        //녹화버튼참조
        mStartStopBtn = (TitleBitmapButton) findViewById(R.id.button_recording);
        mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                (R.drawable.btn_voice_record), null, null);
        mStartStopBtn.setText(R.string.video_recording_start_title);

        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //녹화 start
                if (isStart == false) {
                    mCameraSurfaceView.stopPreview();
                    prepareVideoRecording();

                    mRecorder.start();
                    isStart = true;
                    //녹화가 시작되었음 버튼은 녹화중지 이미지 버튼으로 변경
                    //원버튼에 이미지를 합성한다.
                    mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_voice_stop, 0, 0);
                    mStartStopBtn.setText(R.string.video_recording_stop_title);

                } else {//녹화 중지를 눌렀을때
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;
                    isStart = false;
                    mCameraSurfaceView.startPreview();

                    setResult(RESULT_OK);
                    mStartStopBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.btn_voice_record, 0, 0);
                    mStartStopBtn.setText(R.string.video_recording_start_title);
                }
            }
        });
    }

    //동영상 폴더유무 첵크, 없을경우 폴더생성
    public void checkVideoFolder() {
        File videoFolder = new File(BasicInfo.FOLDER_VIDEO);
        if (!videoFolder.isDirectory()) {
            Log.d(TAG, "비데오 폴더 생성 video folder : " + videoFolder);

            videoFolder.mkdirs();
        }
    }

    //
    public void prepareVideoRecording() {
        checkVideoFolder();

        String videoName = BasicInfo.FOLDER_VIDEO + "recorded";

        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        } else {
            mRecorder.reset();
        }

        mRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

        mRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        mRecorder.setVideoFrameRate(30);
        mRecorder.setOutputFile(videoName);
        mRecorder.setPreviewDisplay(mCameraSurfaceView.getSurface());

        try {
            /**
             * Prepares the recorder to begin capturing and encoding data.
             * This method must be called after setting up the desired audio and video sources, encoders, file format, etc., but before start().
             * 레코더가 데이터 캡처 및 인코딩을 시작할 준비를합니다.
             * 이 메서드는 원하는 오디오 및 비디오 소스, 인코더, 파일 형식 등을 설정 한 후 start () 전에 호출해야합니다.
             */
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "MemoInsertActivity prepareVideoRecording() 인코딩 준비중 예외발생 :", e);
        }
    }
    //end

    //start
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isStart) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.video_recording_message, Toast.LENGTH_LONG);
                toast.show();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    /**
     *
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }
    //end
}
