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
import android.widget.Toast;

import org.familly.multimemo.common.TitleBitmapButton;
import org.familly.multimemo.db.MemoDatabase;

import java.io.File;

public class MultiMemoMainActivity extends AppCompatActivity {
    //디버거용
    public static final String TAG = "DEBUG";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_memo_main);

        //sdcard  연결상태 첵크 check
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD CARD가 연결되지 않을을경우
            Toast.makeText(this, "SD 카드가 없습니다.\n SD 카드를 넣은후 다시실행하십시요", Toast.LENGTH_LONG).show();
            return;
        } else {
            //SD 카드가 연결되었을 경우
            String externalPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Log.d(TAG, "Environment.getExternalStorageDirectory().getAbsolutePath() 외부저장소 절대경로는 >>> " + externalPath);
            if (!BasicInfo.ExternalChecked && externalPath != null) {
                //외부저장소연결상태양호 그리고 외부저장소 경로가 있을경우
                BasicInfo.ExternalPath = externalPath + File.separatorChar;
                Log.d(TAG, "externalPath : " + externalPath + " , File.separatorChar : " + File.separator);

                Log.d(TAG, "변경전 BasicInfo.FOLDER_PHOTO" + BasicInfo.FOLDER_PHOTO);
                BasicInfo.FOLDER_PHOTO = BasicInfo.ExternalPath + BasicInfo.FOLDER_PHOTO;
                Log.d(TAG, "변경후 BasicInfo.FOLDER_PHOTO" + BasicInfo.FOLDER_PHOTO);

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
                viewMemo(position,"onTtemClick()");
            }
        });

        //STAGE 2. 신규메모작성
        TitleBitmapButton nMemoButton =
                (TitleBitmapButton) findViewById(R.id.button_newmemo);
        nMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"새로운 멀티메모를 작성합니다.");

                //신규메모입력 액티비티로 이동한다.
                Intent mIntent = new Intent(getApplicationContext(), MemoInsertActivity.class);
                mIntent.putExtra(BasicInfo.KEY_MEMO_MODE, BasicInfo.MODE_INSERT);
                startActivityForResult(mIntent,BasicInfo.REQ_INSERT_ACTIVITY);
            }
        });
        //종료
        TitleBitmapButton cButton =
                (TitleBitmapButton) findViewById(R.id.button_close);
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        //
        checkDangerousPermissions();
    }
    private void checkDangerousPermissions() {
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult() 콜백 함수 호출됨");

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " : 권한이 승인됨.", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, permissions[i] + " : 권한이 승인되지 않음.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * database 오픈
     */
    public void openDatabase() {
        //데이터베이스오픈
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = MemoDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "메모데이터베이스 오픈됨");
        } else {
            Log.d(TAG, "메모데이터베이스 오픈실패");
        }
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        //데이터베이스 신규생성및 오픈
        openDatabase();

        //메모데이터 로딩
        loadMemoListData();


        super.onStart();
    }

    //end

    //실제 데이터베이스에서 데이터를 가져오는 부분 구현필요.
    public int loadMemoListData() {
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
            Log.d(TAG, "조회건수 : " + recordCount);

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
                if (inputDate != null) {

                    if (inputDate.length() > 10) {
                        inputDate = inputDate.substring(0, 10);
                    }
                }
                String contentText = rCursor.getString(2);
                String photoId = rCursor.getString(3);
                String photoUriStr = getPhotoUriStr(photoId);

                String videoId = rCursor.getString(4);
                String videoUriStr = null;

                String voiceId = rCursor.getString(5);
                String voiceUriStr = null;

                String handWritingId = rCursor.getString(6);
                String handWrithingUriStr = null;

                //MomoListItem 객체생성
                MemoListItem memoListItem = new MemoListItem(memoId, inputDate, contentText,
                        handWritingId, handWrithingUriStr,
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
        }

        return recordCount;
    }
    /**
     * 사진 데이터 URI 가져오기
     * @param photoId
     * @return
     */
    private String getPhotoUriStr(String photoId) {
        String photoUriStr = null;
        //photo id 가 유효할경우 쿼리해서 사진 id를 가져온다.
        if (photoId != null && !photoId.equals("-1")) {
            //photo id 는 문자열이고 포토테이블의 _id는 정수다 정수로 변경해서 조건절에 비교
            //해야 하지않을까?
            String sql = "SELECT uri FROM " + MemoDatabase.TABLE_PHOTO + " WHERE _id = "+ photoId + "";
            Cursor pCursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            if (pCursor.moveToNext()) {
                photoUriStr = pCursor.getString(0);
            }
            pCursor.close();

        } else if (photoId == null || photoId.equals("-1")) {
            photoUriStr = "";
        }
        return photoUriStr;
    }

    //리스틀클릭시 입력화면으로

    public void viewMemo(int index,String callMethod) {
        Log.d(TAG, callMethod + ">" + "MultimemoMainActivity viewMemo() ");
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
    }

    //다른 액티버티의 응답처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "MultiMemoMainActivity  onActivityResult() 콜백함수 호출됨.");

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
    }

}
