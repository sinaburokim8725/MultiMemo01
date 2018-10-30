package org.familly.multimemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoPlayActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";

    VideoView mVideoView;
    String mVideoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //화면전체 사용하기 상태바 숨기기,타이틀바 숨기기
        final Window win = getWindow();
        //상태바숨기기
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //타이틀 바 숨기기
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_video_play);

        setVideoView();

        setMediaController();
    }

    private void setVideoView() {
        mVideoView = (VideoView) findViewById(R.id.video_playing_videoView);

        Intent intent = getIntent();
        mVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);
        mVideoView.setVideoPath(mVideoUri);

    }

    private void setMediaController() {
        MediaController controller = new MediaController(VideoPlayActivity.this);
        mVideoView.setMediaController(controller);
        mVideoView.start();
    }
}
