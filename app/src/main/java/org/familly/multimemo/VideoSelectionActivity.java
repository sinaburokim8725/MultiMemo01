package org.familly.multimemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Video;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;


public class VideoSelectionActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DEBUG";
    //앨범에서 선택한 비디오의 uri
    String mAlbumVideoUri;

    //앨범 동영상 목록
    ListView mVideoList;

    //선택된 비데오 타이틀
    TextView mSelectedVideoTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate Start");

        //타이틀바없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_selection);

        setSelectVideoText();

        setBottomBtns();

        Log.d(LOG_TAG, "동영상 데이트 로딩...");

        //리스트뷰 참조
        mVideoList = (ListView) findViewById(R.id.loading_listView);
        //동영상쿼리
        Cursor c = getContentResolver().query(Video.Media.EXTERNAL_CONTENT_URI, null,
                null, null, null);
        final VideoCursorAdapter adapter = new VideoCursorAdapter(this, c);
        mVideoList.setAdapter(adapter);

        //선택된 아이템에 대한 이벤트 처리
        mVideoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                try {

                    //개별 이미지에 대한 Uri 생성
                    /**
                     * a new URI with the given ID appended to the end of the path
                     * 지정된 ID를 패스의 말미에 추가 한 새로운 URI
                     */
                    Uri uri = ContentUris.withAppendedId(Video.Media.EXTERNAL_CONTENT_URI, id);
                    String str = ((TextView) v).getText().toString();

                    mSelectedVideoTitle.setText(str);
                    mSelectedVideoTitle.setSelected(true);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, "선택된 아이템에 대한 이벤처리중 예외발생 : ", ex);
                }

            }
        });
        Log.d(LOG_TAG, "onCreate End");

    }

    //사용자정의 메소드 start
    public void setSelectVideoText() {
        Log.d(LOG_TAG, "setSelectVideoText Start");

        mSelectedVideoTitle = (TextView) findViewById(R.id.loading_selectedVideo);

        Log.d(LOG_TAG, "setSelectVideoText End");

    }

    //버튼영역 설정
    public void setBottomBtns() {
        Log.d(LOG_TAG, "setBottomBtns Start");

        TitleBitmapButton loadingOkBtn = (TitleBitmapButton) findViewById(R.id.loading_okBtn);
        TitleBitmapButton loadingCancelBtn = (TitleBitmapButton) findViewById(R.id.loading_cancelBtn);

        loadingOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showParentActivity();
            }
        });

        //취소버튼
        loadingCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d(LOG_TAG, "setBottomBtns End");

    }

    //부모 액티비티로 돌아가기
    private void showParentActivity() {
        Log.d(LOG_TAG, "showParentActivity Start");

        Intent intent = getIntent();

        if (mAlbumVideoUri != null) {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO, mAlbumVideoUri);
            setResult(RESULT_OK, intent);
        }
        Log.d(LOG_TAG, "showParentActivity End");

        finish();
    }

    //end
    //재정의(overrride) 메소드 start
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(LOG_TAG, "onWindowFocusChanged Start");

        Log.d(LOG_TAG, "포커스변경여부 : " + hasFocus);
        if (hasFocus) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath())));
        }
        Log.d(LOG_TAG, "onWindowFocusChanged End");

    }

    //end
    //inner class 정의 start
    class VideoCursorAdapter extends CursorAdapter {
        private  final String LOG_TAG = "MultiMemo > "+CursorAdapter.class.getSimpleName();


        public VideoCursorAdapter(Context context, Cursor c) {
            super(context, c);
            Log.d(LOG_TAG, "VideoCursorAdapter 생성");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(LOG_TAG, "newView() Start");

            TextView videoTitleText = new TextView(context);

            videoTitleText.setTextColor(Color.BLUE);

            videoTitleText.setPadding(10, 10, 10, 10);

            Log.d(LOG_TAG, "newView() End");

            return videoTitleText;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(LOG_TAG, "bindView Start");

            TextView videoTitleText = (TextView) view;

            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Video.Media._ID));

            String str = cursor.getString(cursor.getColumnIndexOrThrow(Video.Media.TITLE));

            Uri uri = ContentUris.withAppendedId(Video.Media.EXTERNAL_CONTENT_URI, id);

            Log.d(LOG_TAG, "id -> " + id + " , title -> " + str + " , uri -> " + uri);

            try {
                videoTitleText.setText(str);
            } catch (Exception e) {
                Log.e(LOG_TAG, "bindView() 도중 예외발생 ...", e);
            }
            Log.d(LOG_TAG, "bindView End");

        }
    }
    //end

}
