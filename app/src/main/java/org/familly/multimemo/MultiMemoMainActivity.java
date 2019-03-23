package org.familly.multimemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.familly.multimemo.common.TitleBitmapButton;
import org.familly.multimemo.db.MemoDatabase;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class MultiMemoMainActivity extends AppCompatActivity {
    //디버거용
    public static final String LOG_TAG = "MultiMemo > "+MultiMemoMainActivity.class.getSimpleName();

    /**
     *메모정보를 리스팅 할 리스트 뷰 참조
     */
    ListView mMemoListView;

    //1.데이터 베이스에서 조회한 메모정보를 메모리스트 아이템 객체에 설정
    //2.설정된 아이템들의 정보를 리스트뷰의 각 설정부에 데이터를 설정해준다.
    MemoListAdapter mMemoListAdapter;

    //조회된 메모리 리스의 총건수
    int mMemoCount = 0;

    //메모 database 인스턴스 객체
    public static MemoDatabase mDatabase = null;
    TextView itemCountText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_memo_main);
        Log.d(LOG_TAG, "onCreate Start");

        //set current locale 설정
        Locale currentLocale = getResources().getConfiguration().locale;
        BasicInfo.LANGUAGE = currentLocale.getLanguage();
        Log.d(LOG_TAG,"현재 언어 : " + BasicInfo.LANGUAGE);

        //sdcard  연결상태 첵크 check
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD CARD가 연결되지 않을을경우
            Toast.makeText(this, "SD 카드가 없습니다.\n SD 카드를 넣은후 다시실행하십시요", Toast.LENGTH_LONG).show();
            return;
        } else {
            //SD 카드가 연결되었을 경우
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d(LOG_TAG, "Environment.getExternalStorageDirectory().getAbsolutePath() 외부저장소 절대경로는 >>> " + externalPath);
            if (!BasicInfo.ExternalChecked && externalPath != null) {
                //외부저장소연결상태양호 그리고 외부저장소 경로가 있을경우
                BasicInfo.ExternalPath = externalPath + File.separatorChar;
                Log.d(LOG_TAG, "externalPath : " + externalPath + " , File.separatorChar : " + File.separator);

                Log.d(LOG_TAG, "변경전 BasicInfo.FOLDER_PHOTO" + BasicInfo.FOLDER_PHOTO);
                BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;
                Log.d(LOG_TAG, "변경후 BasicInfo.FOLDER_PHOTO" + BasicInfo.FOLDER_PHOTO);

                BasicInfo.FOLDER_VIDEO = BasicInfo.ExternalPath + BasicInfo.FOLDER_VIDEO;
                BasicInfo.FOLDER_VOICE = BasicInfo.ExternalPath + BasicInfo.FOLDER_VOICE;
                BasicInfo.FOLDER_HANDWRITING = BasicInfo.ExternalPath + BasicInfo.FOLDER_HANDWRITING;
                BasicInfo.DATABASE_NAME = BasicInfo.ExternalPath + BasicInfo.DATABASE_NAME;

                BasicInfo.ExternalChecked = true;
            }
        }
        //end sdcard check

        /**
         * 메모리스트
         */
        //리스트 뷰 참조
        mMemoListView = (ListView) findViewById(R.id.list_memo);
        //리스트아답타에 액티비티 정보를 넘겨준다.
        mMemoListAdapter = new MemoListAdapter(this);
        //리스트 뷰에 아답타를 설정한다.
        mMemoListView.setAdapter(mMemoListAdapter);
        //리스트 아이템 클릭및터치 이벤트 처리
        mMemoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewMemo(position);
            }
        });

        //STAGE 2. 신규메모작성
        TitleBitmapButton nMemoButton =
                (TitleBitmapButton) findViewById(R.id.button_newmemo);
        nMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG,"새로운 멀티메모를 작성합니다.");

                //신규메모입력 액티비티로 이동한다.
                Intent mIntent = new Intent(getApplicationContext(), MemoInsertActivity.class);
                mIntent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_INSERT);
                startActivityForResult(mIntent,BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });
        //종료버튼
        TitleBitmapButton cButton =
                (TitleBitmapButton) findViewById(R.id.button_close);
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });
        //추가: 리스트 화면 변경으로 리스트 총건수 보여주기 추가.
        itemCountText = (TextView) findViewById(R.id.itemCount);
        //
        checkDangerousPermissions();

        Log.d(LOG_TAG, "onCreate End");
    }
    private void checkDangerousPermissions() {
        Log.d(LOG_TAG, "checkDangerousPermissions Start");

        //문자배열로  퍼미션들 초기화 시켜준다.
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        int permissionCheck = -1;
        //각 권한허가여부 첵크
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_SHORT).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
        Log.d(LOG_TAG, "checkDangerousPermissions End");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(LOG_TAG, "onRequestPermissionsResult() Start");

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " : 권한이 승인됨.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, permissions[i] + " : 권한이 승인되지 않음.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Log.d(LOG_TAG, "onRequestPermissionsResult() End");
    }

    /**
     * database 오픈
     */
    public void openDatabase() {
        Log.d(LOG_TAG, "openDatabase Start");

        //데이터베이스오픈
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = MemoDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(LOG_TAG, "메모데이터베이스 오픈됨");
        } else {
            Log.d(LOG_TAG, "메모데이터베이스 오픈실패");
        }

        Log.d(LOG_TAG, "openDatabase End");
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(LOG_TAG, "onStart Start");

        //데이터베이스 신규생성및 오픈
        if (mDatabase == null) {
             openDatabase();
        }

        //메모데이터 로딩
        loadMemoListData();

        Log.d(LOG_TAG, "onStart End");

    }

    //end

    //실제 데이터베이스에서 데이터를 가져오는 부분 구현필요.
    public int loadMemoListData() {
        Log.d(LOG_TAG, "loadMemoListData Start");

        String sql = "SELECT _id, input_date,content_text,"
                + "id_photo,id_video,id_voice,id_handwriting "
                + "FROM MEMO ORDER BY input_date DESC";

        int recordCount = -1;
        //데이터 베이스 객체가 널이 아닐경우 즉 오픈되었을경우만
        if (MultiMemoMainActivity.mDatabase != null) {

            //조회
            Cursor rCursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);

            //조회건수
            recordCount = rCursor.getCount();
            Log.d(LOG_TAG, "조회건수 : " + recordCount);

            //조회정보를 담는 아이템 객체를 담는 컬렉션 객체(List 클래스형)를 초기화한다.
            mMemoListAdapter.clear();

            //1.조회된건수를 MemoListItem 객체에 설정하고 건수만큼 컬렉션 객체에 담는다.
            for (int i = 0; i < recordCount; i++) {
                //첫번째 조회건수의 인덱스를 가리키도록 한다.
                rCursor.moveToNext();

                String memoId      = rCursor.getString(0);
                String inputDate   = rCursor.getString(1);
                //리스트를 보여주는 화면에서는 메모내용이 길경우 잘라서 보여주고
                //상세화면에서 모든것을 보여주는 것으로 한다.
                if (inputDate != null && inputDate.length() > 10) {

                    try {
                        Date inDate = BasicInfo.dateFormat.parse(inputDate);

                        if (BasicInfo.LANGUAGE.equals("ko")) {
                            inputDate = BasicInfo.dateNameformat2.format(inDate);
                        } else {
                            inputDate = BasicInfo.dateNameFormat3.format(inDate);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    inputDate = "";
                }

                String contentText = rCursor.getString(2);
                String photoId = rCursor.getString(3);
                String photoUriStr = getPhotoUriStr(photoId);

                String videoId = rCursor.getString(4);
                String videoUriStr = null;

                String voiceId = rCursor.getString(5);
                String voiceUriStr = null;

                String handwritingId = rCursor.getString(6);
                String handwritingUriStr = null;

                //Stag3 추가
                handwritingUriStr = getHandwritingUriStr(handwritingId);

                //stage4 추가
                videoUriStr = getVideoUriStr(videoId);
                voiceUriStr = getVoiceUriStr(voiceId);


                //MomoListItem 객체생성
                MemoListItem memoListItem = new MemoListItem(memoId, inputDate, contentText,
                        handwritingId, handwritingUriStr,
                        photoId, photoUriStr,
                        videoId, videoUriStr,
                        voiceId, voiceUriStr);

                //MemoListItem 객체들 리스트 컬렉션에 담기
                mMemoListAdapter.addIteme(memoListItem);
            }//end for
            //커서 닫기
            rCursor.close();

            //아답타에게 데이터가 변경되었으며 데이터셋을 반영한 뷰가 자체적으로
            //새로고쳐져야함을 알린다 즉 콜백함수 getView()호출
            mMemoListAdapter.notifyDataSetChanged();

            //메인화면 변경으로 인하 리스트 총건수 표시 추가
            itemCountText.setText(recordCount + " " + getResources().getString(R.string.itme_count));
            itemCountText.invalidate();
        }

        Log.d(LOG_TAG, "loadMemoListData End");

        return recordCount;
    }
    /**
     * 사진 데이터 URI 가져오기
     * @param photoId
     * @return
     */
    public String getPhotoUriStr(String photoId) {
        Log.d(LOG_TAG, "getPhotoUriStr Start");

        String photoUriStr = null;
        //photo id 가 유효할경우 쿼리해서 사진 id를 가져온다.
        if (photoId != null && !photoId.equals("-1")) {
            //photo id 는 문자열이고 포토테이블의 _id는 정수다 정수로 변경해서 조건절에 비교
            //해야 하지않을까?
            String sql = "SELECT uri FROM " + MemoDatabase.TABLE_PHOTO + " WHERE _id = "+ photoId + "";
            Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            if (cursor.moveToNext()) {
                photoUriStr = cursor.getString(0);
            }
            cursor.close();

        } else {
            photoUriStr = "";
        }

        Log.d(LOG_TAG, "getPhotoUriStr End");

        return photoUriStr;
    }
    //손글씨 uri정보 가져오기
    public String getHandwritingUriStr(String handwritingId) {
        Log.d(LOG_TAG, "getHandwritingUriStr Start");

        String handwritingUriStr = null;

        if (handwritingId != null && handwritingId.trim().length() > 0 && !handwritingId.equals("-1")) {

            String sql = "SELECT uri FROM " + MemoDatabase.TABLE_HANDWRITING + " WHERE _id = "+ handwritingId + "";
            Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            if (cursor.moveToNext()) {
                handwritingUriStr = cursor.getString(0);
            }
            cursor.close();

        } else {
            handwritingUriStr = "";
        }
        return handwritingUriStr;
    }
    //동영상 uri정보 가져오기
    public String getVideoUriStr(String videoId) {
        String videoUriStr = null;

        if (videoId != null && videoId.trim().length() > 0 && !videoId.equals("-1")) {

            String sql = "SELECT uri FROM " + MemoDatabase.TABLE_VIDEO + " WHERE _id = "+ videoId + "";
            Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            if (cursor.moveToNext()) {
                videoUriStr = cursor.getString(0);
            }
            cursor.close();

        } else {
            videoUriStr = "";
        }

        Log.d(LOG_TAG, "getHandwritingUriStr End");

        return videoUriStr;
    }
    //음성   uri정보 가져오기
    public String getVoiceUriStr(String voiceId) {
        Log.d(LOG_TAG, "getVoiceUriStr Start");

        String voiceUriStr = null;

        if (voiceId != null && voiceId.trim().length() > 0 && !voiceId.equals("-1")) {

            String sql = "SELECT uri FROM " + MemoDatabase.TABLE_VOICE + " WHERE _id = " + voiceId + "";
            Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            if (cursor.moveToNext()) {
                voiceUriStr = cursor.getString(0);
            }
            cursor.close();

        } else {
            voiceUriStr = "";
        }

        Log.d(LOG_TAG, "getVoiceUriStr End");

        return voiceUriStr;
    }

    //리스트 클릭시 수정화면으로
    public void viewMemo(int index) {
        Log.d(LOG_TAG, "viewMemo Start");

        //클릭된 메모리스트 아이템 어뎁더 리스트 컬렉션에서 확득
        MemoListItem item = (MemoListItem) mMemoListAdapter.getItem(index);

        //메모보기 액티비티 뜨우기
        Intent intent = new Intent(getApplicationContext(), MemoInsertActivity.class);
        //부가정보 넘기기
        /**
         * itemId =memoId;
         * objItems = new Object[10];
         * objItems[0] = memoDate;
         * objItems[1] = memoText;
         * objItems[2] = id_hand_writing;
         * objItems[3] = uri_handwriting;
         * objItems[4] = id_phto;
         * objItems[5] = uri_photo;
         * objItems[6] = id_video;
         * objItems[7] = uri_video;
         * objItems[8] = id_voice;
         * objItems[9] = uri_voice;
         */
        intent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_VIEW);
        intent.putExtra(BasicInfo.KEY_MEMO_ID,   item.getId());
        intent.putExtra(BasicInfo.KEY_MEMO_DATE, item.getData(0));
        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, item.getData(1));

        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING,  item.getData(2));
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, item.getData(3));

        intent.putExtra(BasicInfo.KEY_ID_PHOTO,  item.getData(4));
        intent.putExtra(BasicInfo.KEY_URI_PHOTO, item.getData(5));

        intent.putExtra(BasicInfo.KEY_ID_VIDEO,  item.getData(6));
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, item.getData(7));

        intent.putExtra(BasicInfo.KEY_ID_VOICE,  item.getData(8));
        intent.putExtra(BasicInfo.KEY_URI_VOICE, item.getData(9));

        startActivityForResult(intent, BasicInfo.REQ_VIEW_ACTIVITY);

        Log.d(LOG_TAG, "viewMemo End");
    }

    //다른 액티버티의 응답처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(LOG_TAG, "onActivityResult Start");

        switch (requestCode) {
            case BasicInfo.REQ_INSERT_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    loadMemoListData();
                }
                break;

            case BasicInfo.REQ_VIEW_ACTIVITY:
                loadMemoListData();
                break;
            default:
        }

        Log.d(LOG_TAG, "onActivityResult End");
    }

}
