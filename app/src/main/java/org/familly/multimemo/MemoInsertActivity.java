package org.familly.multimemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import org.familly.multimemo.common.TitleBackgroundButton;
import org.familly.multimemo.common.TitleBitmapButton;
import org.familly.multimemo.db.MemoDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class MemoInsertActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";

    //버튼참조
    TitleBitmapButton mVideoBtn;
    TitleBitmapButton mVoiceBtn;

    //
    EditText mMemoEidt;
    ImageView mPhoto;
    //
    String mMemoMode;
    String mMemoId;
    String mMemoDate;
    //
    String mMediaPhotoId;
    String mMediaPhotoUri;
    String mMediaVideoId;
    String mMediaVideoUri;
    String mMediaVoiceId;
    String mMediaVoiceUri;
    String mMediaHandWritingId;
    String mMediaHandWritingUri;

    String tempPhotoUri;
    String tempVideoUri;
    String tempVoiceUri;
    String tempHandWritingUri;

    String mDateStr;
    String mMemoStr;

    Bitmap resultPhotoBitmap;

    Bitmap resultHandWritingBitmap;

    //사진켑쳐여부와 저장여부 첵크
    boolean isPhotoCaptured;
    boolean isVideoRecorded;
    boolean isVoiceRecorded;
    boolean isHandWritingMade;

    //
    boolean isPhotoFileSaved;
    boolean isVideoFileSaved;
    boolean isVoiceFilesaved;
    boolean isHandWritingFileSaved;

    boolean isPhotoCanceled;
    boolean isVideoCanceled;
    boolean isVoiceCanceled;
    boolean isHandWritingCanceled;

    Calendar mCalendar = Calendar.getInstance();
    TitleBitmapButton insertDateButton;

    //
    int mSelectdContentArray;
    int mChoicedArrayItem;

    //
    TitleBackgroundButton titleBackgroundButton;
    TitleBitmapButton insertSaveBtn;
    TitleBitmapButton insertCancelBtn;
    TitleBitmapButton insertTextBtn;
    TitleBitmapButton insertHandWritingBtn;

    int textViewMode = 0;
    EditText insertMemoEdit;
    View insertHandWritingView;

    Animation leftAnim;
    Animation rightAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_insert);

        titleBackgroundButton = (TitleBackgroundButton) findViewById(R.id.button_insertTitle);
        mPhoto = (ImageView) findViewById(R.id.image_insertPhoto);
        mMemoEidt = (EditText) findViewById(R.id.edit_insertMemo);

        insertTextBtn = (TitleBitmapButton) findViewById(R.id.button_insertMemo);
        insertHandWritingBtn = (TitleBitmapButton) findViewById(R.id.button_insertHandwriting);
        insertMemoEdit = (EditText) findViewById(R.id.edit_insertMemo);
        insertHandWritingView = (View) findViewById(R.id.view_insertHandwriting);

        //손글씨 화면과 메모입력화면 전환시 슬라이딩 기법의 애니메이션 연출
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);

        //초기 입력화면의 선택된 버튼 결정
        insertTextBtn.setSelected(true);
        insertHandWritingBtn.setSelected(false);

        //버튼클릭 화면전화 및 전환시 애니메이션(슬라이딩)적용
        //start
        insertTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewMode == 1) {
                    insertHandWritingView.setVisibility(View.GONE);
                    insertMemoEdit.setVisibility(View.VISIBLE);
                    insertMemoEdit.startAnimation(leftAnim);

                    insertTextBtn.setSelected(true);
                    insertHandWritingBtn.setSelected(false);

                    textViewMode = 0;
                }
            }
        });

        insertHandWritingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewMode == 0) {
                    insertHandWritingView.setVisibility(View.VISIBLE);
                    insertMemoEdit.setVisibility(View.GONE);
                    insertHandWritingView.startAnimation(leftAnim);

                    insertHandWritingBtn.setSelected(true);
                    insertTextBtn.setSelected(false);

                    textViewMode = 1;
                }
            }
        });

        //end

        //사진 이미지뷰 참조
        mPhoto = (ImageView) findViewById(R.id.image_insertPhoto);
        //사진 클릭이벤트
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MemoInsertActivity iInsertPhoto onClick() 호출됨");
                if (isPhotoCaptured || isPhotoFileSaved) {
                    //갭쳐여부나 저장여부가 true 이면
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);

                } else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });

        setBottomButton();
        setMediaLayout();
        setCalendar();

        Intent intent = getIntent();
        mMemoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
        if (mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(intent);
            titleBackgroundButton.setText("메모 보기");
            insertSaveBtn.setText("수정");
        } else {
            titleBackgroundButton.setText("새 메모");
            insertSaveBtn.setText("저장");
        }
    }

    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public void processIntent(Intent intent) {
        Log.d(TAG, "MemoInsertActivity processIntent() 호출");

        mMemoId = intent.getStringExtra(BasicInfo.KEY_MEMO_ID);
        mMemoEidt.setText(intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT));

        mMediaPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
        mMediaPhotoUri = intent.getStringExtra((BasicInfo.KEY_URI_PHOTO));

        mMediaVideoId = intent.getStringExtra(BasicInfo.KEY_ID_VIDEO);
        mMediaVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);

        mMediaVoiceId = intent.getStringExtra(BasicInfo.KEY_ID_VOICE);
        mMediaVoiceUri = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);

        mMediaHandWritingId = intent.getStringExtra(BasicInfo.KEY_ID_HANDWRITING);
        mMediaHandWritingUri = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);

        setMediaImage(mMediaPhotoId, mMediaPhotoUri, mMediaVideoId, mMediaVideoUri, mMediaVoiceId, mMediaVoiceUri
                , mMediaHandWritingId, mMediaHandWritingUri);
    }

    //조회화면에서 리스트를 누르고 수정화면으로 왔을경우 기존 데이터를 표시해줘야 한다.
    private void setMediaImage(String photoId, String photoUri, String videoId, String videoUri, String voiceId, String voiceUri
            , String handwritingId, String mMediaHandWritingIdUri) {

        Log.d(TAG, "MemoInsertActivity setMediaImage() 호출됨 \n" + "photoId : " + photoId + " , " + "photoUrk :" + photoUri);
        //표시할 사진 유무에 따른 사진 세팅
        if (photoId.equals("") || photoId.equals("-1")) {
            //저장된 사진이 없을경우는 사진선택관련 이미지 보여준다.
            mPhoto.setImageResource(R.drawable.person_add);

        } else {
            //표시할 사진이 있을경우
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }
    }

    //하단 메뉴 버튼 설정
    public void setBottomButton() {
        insertSaveBtn = (TitleBitmapButton) findViewById(R.id.button_saveMemo);
        insertCancelBtn = (TitleBitmapButton) findViewById(R.id.button_cancelMemo);
        //저장버튼
        insertSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isParsed = parseValues();
                if (isParsed) {
                    if (mMemoMode.equals(BasicInfo.MODE_INSERT)) {
                        saveInput();
                    } else if (mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
                        modifyInput();
                    }
                }
            }
        });

        //닫기버튼
        insertCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    /*
    *테이터 베이스 레코드 추가
    * */
    private void saveInput() {
        Log.d(TAG, "MemoInsertActivity 내 saveInput() 호출");

        String photoFileName = insertPhoto();
        int photoId = -1;

        String sql = null;
        sql = "SELECT _id FROM " + MemoDatabase.TABLE_PHOTO + " WHERE URI = '" + photoFileName + "'";
        Log.d(TAG, "sql : " + sql);

        if (MultiMemoMainActivity.mDatabase != null) {
            Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
            //데이터 존재여부 첵크
            if (cursor.moveToNext()) {
                photoId = cursor.getInt(0);
            }
            cursor.close();
        }
        sql = "INSERT INTO " + MemoDatabase.TABLE_MEMO +
                " ( INPUT_DATE,CONTENT_TEXT,ID_PHOTO,ID_VIDEO,ID_VOICE,ID_HANDWRITING) " +
                "VALUES (" +
                "DATATIME('" + mDateStr + "'), " +
                "'" + mMemoStr + "'," +
                "'" + photoId + "'," +
                "'" + "" + "'," +
                "'" + "" + "'," +
                "'" + "" + "')";
        Log.d(TAG, "Insert sql : " + sql);
        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();

    }

    /*
     * 데이터베이스 레코드 수정
     *
     * */
    private void modifyInput() {
        Log.d(TAG, "MemoINsertActivity modifyInput() 호출");

        Intent intent = getIntent();

        String photoFileName = insertPhoto();
        int photoId = -1;

        String sql = null;

        if (photoFileName != null) {
            //파일명에 해당하는 photo id 를 photo 테이블에서 가져온다.
            sql = "SELECT _id FROM " + MemoDatabase.TABLE_PHOTO + " WHERE URI = '" + photoFileName + "'";
            Log.d(TAG, "조회쿼리 : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
                mMediaPhotoUri = photoFileName;

                sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                        " SET " +
                        " ID_PHOTO = '" + photoId + "'" +
                        " WHERE _ID = " + Integer.parseInt(mMemoId);

                MultiMemoMainActivity.mDatabase.rawQuery(sql);
                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if(isPhotoCanceled && isPhotoFileSaved) {
            //수정시 새로운 사진을 선택한경우 기존선택된 사진정보 삭제후 신규 사진정보 갱신
            sql = "DELETE FROM " + MemoDatabase.TABLE_PHOTO +
                    " WHERE _ID = " + Integer.parseInt(mMediaPhotoId);
            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
            if (photoFile.exists()) {
                photoFile.delete();
            }
            sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                    "SET " +
                    " ID_PHOTO = " + photoId +
                    " WHERE _ID = " + Integer.parseInt(mMemoId);
            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            mMediaPhotoId = String.valueOf(photoId);
        }
        //update memo info
        sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                " SET " +
                " INPUT_DATE = DATETIME('" + mDateStr + "')," +
                " CONTENT_TEXT = '" + mMemoStr + "'" +
                " WHERE _ID = " + Integer.parseInt(mMemoId);
        Log.d(TAG, "sql : " + sql);
        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }

        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, mMemoStr);
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
        intent.putExtra(BasicInfo.KEY_ID_VIDEO, mMediaVideoId);
        intent.putExtra(BasicInfo.KEY_ID_VOICE, mMediaVoiceId);
        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, mMediaHandWritingId);

        intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, mMediaVideoUri);
        intent.putExtra(BasicInfo.KEY_URI_VOICE, mMediaVoiceUri);;
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, mMediaHandWritingUri);;

        setResult(RESULT_OK, intent);
        finish();
    }

    /*
     * 1.갭쳐된 이미지나 앨범에서 선택된 사진을 멀티메모의 사진폴더에 복사한다.
     * 2.이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열을 사용한다.
     * */
    private String insertPhoto() {
        Log.d(TAG, "MemoInsertActivity insertPhoto() 호출");

        String photoName = null;
        if (isPhotoCaptured) {
            try {
                //수정일경우
                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {
                    Log.d(TAG, "이전 사진정보 삭제");
                    String sql = "DELETE FROM " + MemoDatabase.TABLE_PHOTO +
                            " WHERE _ID = " + Integer.parseInt(mMediaPhotoId);
                    Log.d(TAG, "sql : " + sql);
                    if (MultiMemoMainActivity.mDatabase != null) {
                        MultiMemoMainActivity.mDatabase.execSQL(sql);
                    }
                    File previousFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoUri);
                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }

                File photoFolder = new File(BasicInfo.FOLDER_PHOTO);
                //폴더가 없다면 폴더를 생성한다.
                if (!photoFolder.isDirectory()) {
                    Log.d(TAG, "폴더생성 : " + photoFolder);
                    photoFolder.mkdir();
                }
                photoName = createFileName();
                FileOutputStream outputStream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                if (photoName != null) {
                    Log.d(TAG, "isCaptured   : " + isPhotoCaptured);
                    //insert picture info
                    String sql = "INSERT INTO " + MemoDatabase.TABLE_PHOTO + " (uri) VALUES (" +
                            "'" + photoName + "'";
                    if (MultiMemoMainActivity.mDatabase != null) {
                        MultiMemoMainActivity.mDatabase.execSQL(sql);
                    }
                }

            } catch (IOException ex) {
                Log.e(TAG, "예외발생 : copying photo", ex);
            }
        }
        return photoName;
    }

    //파일명 생성
    private String createFileName() {
        Log.d(TAG, "MemoInsertActivity createFileName() 호출");
        Date currentDate = new Date();
        String fileName = String.valueOf(currentDate.getTime());

        Log.d(TAG, "파일이름 밀리세컨드 시간 : " + fileName);
        return fileName;
    }

    /**
     *
     */
    public void setMediaLayout() {
        isPhotoCaptured = false;
        isVideoRecorded = false;
        isVoiceRecorded = false;
        isHandWritingMade = false;

        mVideoBtn = (TitleBitmapButton) findViewById(R.id.button_insertVideo);
        mVoiceBtn = (TitleBitmapButton) findViewById(R.id.button_insertVoice);
    }

    //
    private void setCalendar() {
        Log.d(TAG, "MemoInsertActivity setCalendar() 호출");
        insertDateButton = (TitleBitmapButton) findViewById(R.id.button_insertDate);
        insertDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dateStr = insertDateButton.getText().toString();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();

                try {
                    BasicInfo.dateDayNameFormat.parse(dateStr);
                } catch (ParseException e) {
                    Log.e(TAG, "Exception in parsing date : " + date);
                }
                calendar.setTime(date);

                new DatePickerDialog(MemoInsertActivity.this,
                        dataSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        insertDateButton.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
    }

    //날짜설정 리스너
    DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(year, month, dayOfMonth);
            insertDateButton.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
        }
    };

    //일자와 메모확인
    private boolean parseValues() {
        Log.d(TAG, "MemoInsertActivity parseValues() 호출");
        String insertDateStr = insertDateButton.getText().toString();
        try {
            Date insertDate = BasicInfo.dateDayNameFormat.parse(insertDateStr);
        } catch (ParseException e) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }
        String memotxt = mMemoEidt.getText().toString();
        mMemoStr = memotxt;
        if (mMemoStr.trim().length() < 1) {
            //텍스입력없을 경우 경고창에 입력독려 메시지 띄움?
            showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
            return false;
        }
        return true;
    }

    //showDialog 호출시 불려지는 콜백함수
    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(TAG, "MemoInsertActivity onCreateDialog() 오버라이드된 콜백함수 호출됨");
        AlertDialog.Builder builder = null;

        switch (id) {
            case BasicInfo.CONFIRM_TEXT_INPUT:
                Log.d(TAG, "메모텍스트 입력이 없을경우 : " + BasicInfo.CONFIRM_TEXT_INPUT);
                builder = new AlertDialog.Builder(this);
                builder.setTitle("메모");
                builder.setMessage("텍스트를 입력하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case BasicInfo.CONTENT_PHOTO:

                builder = new AlertDialog.Builder(this);
                mSelectdContentArray = R.array.array_photo;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoicedArrayItem = which;
                    }
                });
                //선택
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //직접촬영 과 앨범에서 사진선택에 땨라 분기한다.
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else {
                            showPhotoSelectionActivity();
                        }
                    }
                });
                //취소
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "취소 버튼 누름 : " + "(" + which + " , " + dialog.toString() + ")");
                    }
                });
                break;
            case BasicInfo.CONTENT_PHOTO_EX:
                Log.d(TAG, "선택된 사진이 있을경우 선택된 사진 취소 메뉴추가 :" + BasicInfo.CONTENT_PHOTO_EX);
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_photo_ex;
                builder.setTitle("선택하세요");
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoicedArrayItem = which;
                    }
                });
                builder.setPositiveButton("선택", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showPhotoSelectionActivity();
                        } else if (mChoicedArrayItem == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            mPhoto.setImageResource(R.drawable.person_add);
                        }

                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "취소버튼 눌렀음.");
                    }
                });
                break;
            default:
                break;
        }

        return builder.create();
    }
    //사진미리보기및 촬영화면 (개선점:미리보기가 및 선택부분 새로운 기법고려필요)
    private void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }
    //사진폴던내 사진보여주기 , 그중에서 한개 선택하기 (개선점:멀티선택 기능 고려필요)
    private void showPhotoSelectionActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);

    }
    /**
     * 다른 액티비티로 부터의 응답처리
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY:
                Log.d(TAG, "사진촬영화면에서 응답처리 요청 REQ_PHOTO_CAPTURE_ACTIVITY 코드 :" + BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "사진촬영정상처리됨 처리코드 : " + resultCode);
                    boolean isPhotoExists = checkCapturedPhotoFile();
                    //캡처사진 존재여부 첵크
                    if (isPhotoExists) {
                        Log.d(TAG, "캡쳐된 이미지 존재함. 이미지파일 :" + BasicInfo.FOLDER_PHOTO + "captured");

                        resultPhotoBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + "captured");
                        tempPhotoUri = "captured";
                        mPhoto.setImageBitmap(resultPhotoBitmap);
                        isPhotoCaptured = true;
                        //다시그림
                        mPhoto.invalidate();
                    } else {
                        Log.d(TAG, "촬영된 이미지 파일이 존재하지 않음 파일형태 : " + BasicInfo.FOLDER_PHOTO + "captured");
                    }
                }
                break;

            case BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY:
                Log.d(TAG, "사진을 앨범에서 선택했음 응답 액티비티 코드 REQ_PHOTO_SELECTION_ACTIVITY : " + BasicInfo.REQ_PHOTO_SELECTION_ACTIVITY);

                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "앨범에서 사진선택 정상처리 됨 응답코드 : " + resultCode);
                    Uri getPhotoUri = intent.getParcelableExtra(BasicInfo.KEY_URI_PHOTO);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    //sinaburokim
                    options.inSampleSize = 8;

                    try {
                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri));
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "이미지 디코드중 예외발생", e);
                    }
                    mPhoto.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;
                    mPhoto.invalidate();
                }

                break;
            default:
        }
    }
    /**
     * 저장된 사진 파일확인
     */
    private boolean checkCapturedPhotoFile() {
        File file = new File(BasicInfo.FOLDER_PHOTO + "captured");
        if (file.exists()) {
            return true;
        }
        return false;
    }
}
