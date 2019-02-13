package org.familly.multimemo;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.IOException;

public class VoicePlayActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";

    boolean isPlaying;
    boolean isHolding;

    int mTime;
    int mCurrentTime;

    String mVoicePath;

    TitleBitmapButton mStartStopBtn;
    TextView mPlayingTimeText;
    TextView mTotalTimeText;
    TitleBitmapButton mCloseBtn;

    MediaPlayer mPlayer = null;
    ProgressBar mProgressBar;
    Handler mHandler = new Handler();

    Runnable mUpdateTimer = new Runnable() {
        @Override
        public void run() {
            if (isPlaying && mTime <= (mPlayer.getDuration() / 1000)) {
                if (mTime > 0) {
                    mProgressBar.incrementProgressBy(1);
                }
                updateTimerView();
            } else {
                mProgressBar.setProgress(mProgressBar.getMax());
                if (mProgressBar.getProgress() == mProgressBar.getMax()) {
                    mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_play, R.drawable.btn_voice_play);
                    isPlaying = false;
                    isHolding = false;

                    stop();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_play);

        setTitle(R.string.audio_play_title);

        Intent intent = getIntent();

        mVoicePath = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);

        isPlaying = false;
        isHolding = false;
        mCurrentTime = 0;

        mStartStopBtn = (TitleBitmapButton) findViewById(R.id.playing_stopBtn);
        mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_pause, R.drawable.btn_voice_pause);
        mStartStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying && !isHolding) {
                    isPlaying = false;

                    mPlayer.pause();
                    mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_play, R.drawable.btn_voice_play);
                    isHolding = true;
                    mCurrentTime = mProgressBar.getProgress();
                    mHandler.removeCallbacks(mUpdateTimer);

                } else if (isHolding) {
                    isPlaying = true;
                    mPlayer.start();
                    mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_pause, R.drawable.btn_voice_pause);
                    isHolding = false;
                    mProgressBar.setProgress(mCurrentTime);
                    mHandler.post(mUpdateTimer);
                } else {
                    isPlaying = true;
                    mProgressBar.setProgress(0);
                    startPlayback(mVoicePath);
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.playing_progressBar);
        mPlayingTimeText = (TextView) findViewById(R.id.playing_playingTimeText);
        mTotalTimeText = (TextView) findViewById(R.id.playing_totalTimeText);
        startPlayback(mVoicePath);

        mCloseBtn = (TitleBitmapButton) findViewById(R.id.playing_closeBtn);
        mCloseBtn.setText(R.string.close_btn);
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
                finish();
            }
        });

        //progress bar 터치이벤트
        mProgressBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                int action = e.getAction();

                if (action == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "Action Down in progress bar x , y : " + e.getX() + " , " + e.getY());
                    //계산
                    int progressWidth = mProgressBar.getWidth();
                    float currentX = e.getX();
                    float currentOffset = currentX / (float) progressWidth;

                    if (currentOffset > 0.0F && currentOffset < 1.0F) {
                        if (mPlayer != null) {
                            //백분율 계산
                            int offsetProgressInt = new Float(currentOffset * 100).intValue();
                            float offsetFloat = ((float)mPlayer.getDuration())*currentOffset;
                            Log.d(TAG, "(float)mPlayer.getDuration() ==> " + (float) mPlayer.getDuration());

                            if (isPlaying) {
                                stop();
                                mTime = 0;
                                mPlayer = new MediaPlayer();

                                try {
                                    mPlayer.setDataSource(mVoicePath);
                                    mPlayer.prepare();
                                    mProgressBar.setMax(mPlayer.getDuration() / 1000);
                                    mProgressBar.setProgress(new Float(offsetFloat / 1000.0F).intValue());
                                    mPlayer.seekTo(new Float(offsetFloat).intValue());
                                    mTime = new Float(offsetFloat / 1000.0F).intValue();

                                    mPlayer.start();

                                    mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_pause, R.drawable.btn_voice_pause);
                                    mHandler.post(mUpdateTimer);
                                } catch (IllegalArgumentException e1) {
                                    mPlayer = null;
                                } catch (IOException e1) {
                                    mPlayer = null;
                                }


                            }
                        }
                    }
                }

                return true;
            }
        });
    }

    //사용자 정의 메소드start
    public void startPlayback(String path) {
        Log.d(TAG, "VoicePlayActivity startPlayback() 호출");
        stop();
        mTime = 0;
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();

            mProgressBar.setMax(mPlayer.getDuration() / 1000);
            String timerFormat = String.valueOf(R.string.timer_format);
            //총 녹화시간 >> 00:00 (분:초)를 나타낸다.
            String totalRecordedTimeStr = String.format(timerFormat, (mPlayer.getDuration() / 1000) / 60, (mPlayer.getDuration() / 1000) % 60);
            mTotalTimeText.setText(totalRecordedTimeStr);

            mPlayer.start();

            mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_pause, R.drawable.btn_voice_pause);

        } catch (IllegalAccessError error) {
            mPlayer = null;
            return;
        } catch (IOException e) {
            mPlayer = null;
            return;
        }
        mHandler.post(mUpdateTimer);
    }

    public void stopPlayback() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.stop();
        mPlayer.release();
        mPlayer = null;
        mStartStopBtn.setBackgroundBitmap(R.drawable.btn_voice_play, R.drawable.btn_voice_play);

    }
    public void stop() {
        stopPlayback();
        mProgressBar.setProgress(0);
        mHandler.removeCallbacks(mUpdateTimer);
        mPlayingTimeText.setText(String.valueOf(R.string.minute_second));
    }


    private void updateTimerView() {
        String timerFormat = String.valueOf(R.string.timer_format);
        String playedTimeStr = String.format(timerFormat, mTime / 60, mTime % 60);
        mPlayingTimeText.setText(playedTimeStr);
        mTime++;
        mHandler.postDelayed(mUpdateTimer, 1000);
    }
    //end

    //재정의 및 구현 메소드 start

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "VoicePlayActivity onBackPressed() 호출");

        stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "VoicePlayActivity onPause() 호출");
        mHandler.removeCallbacks(mUpdateTimer);
    }
    //end
    //inner class 등 start
    //end

}
