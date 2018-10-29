package org.familly.multimemo;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

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
    String mMediaHandwritingId;
    String mMediaHandwritingUri;

    String tempPhotoUri;
    String tempVideoUri;
    String tempVoiceUri;
    String tempHandWritingUri;

    String mDateStr;
    String mMemoStr;

    Bitmap resultPhotoBitmap;

    Bitmap resultHandwritingBitmap;

    //사진켑쳐여부와 저장여부 첵크
    boolean isPhotoCaptured;
    boolean isVideoRecorded;
    boolean isVoiceRecorded;
    boolean isHandwritingMade;

    //
    boolean isPhotoFileSaved;
    boolean isVideoFileSaved;
    boolean isVoiceFilesaved;
    boolean isHandwritingFileSaved;

    boolean isPhotoCanceled;
    boolean isVideoCanceled;
    boolean isVoiceCanceled;
    boolean isHandwritingCanceled;

    Calendar mCalendar = Calendar.getInstance();
    TitleBitmapButton insertDateButton;
    TitleBitmapButton insertTimeButton;

    //
    int mSelectdContentArray;
    int mChoicedArrayItem;

    //
    TitleBitmapButton deleteBtn;
    TitleBackgroundButton titleBackgroundButton;
    TitleBitmapButton insertSaveBtn;
    TitleBitmapButton insertCancelBtn;
    TitleBitmapButton insertTextBtn;
    TitleBitmapButton insertHandWritingBtn;

    int textViewMode = 0;

    ImageView insertHandWritingView;
    String mMomoDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_insert);

        Log.d(TAG, "MemoInsertActivity onCreate() 호출");

        //각종 참조 뷰들 start
        //촬영 이미지 앨번에서 선택한 이미지 위치
        mPhoto = (ImageView) findViewById(R.id.image_insertPhoto);

        //텍스트를 입력할 공간 손글씨 입력공간과 버튼으로 전환가능
        mMemoEidt = (EditText) findViewById(R.id.edit_insertMemo);

        //텍스트입력영역 선택버튼
        insertTextBtn = (TitleBitmapButton) findViewById(R.id.button_insertMemo);

        //손글씨표시영역 선택 버튼
        insertHandWritingBtn = (TitleBitmapButton) findViewById(R.id.button_insertHandwriting);

        //손글씨표시영역 (영역부분을 터하하면 터치이벤트로 손글씨 입력화면으로 전환된다.)
        insertHandWritingView = (ImageView) findViewById(R.id.view_insertHandwriting);
        //삭제버튼: 저장된 정보를 삭제한다.
        deleteBtn = (TitleBitmapButton) findViewById(R.id.button_delete);
        //비데오 선택버튼: 동영상 녹화 재생 불러오기 삭제등을 할수있다.
        mVideoBtn = (TitleBitmapButton) findViewById(R.id.button_insertVideo);
        //음성 선택버튼: 음성 녹화 재생 불러오기 삭제등을 할수있다.
        mVoiceBtn = (TitleBitmapButton) findViewById(R.id.button_insertVoice);
        /**
         * Sets the Drawables (if any) to appear to the left of, above, to the right of, and below the text.
         * Use null if you do not want a Drawable there.
         * The Drawables' bounds will be set to their intrinsic bounds.
         * Calling this method will
         * overwrite any Drawables previously set using setCompoundDrawablesRelative or related methods.
         *
         * Drawables (있는 경우)를 텍스트의 왼쪽, 위, 오른쪽, 아래에 표시하도록 설정합니다.
         * 텍스트의 위,아래,왼쪽,오른쪽에 Drawables를 원하지 않으면 null을 사용하십시오.
         * Drawables의 범위는 본질적인 범위로 설정됩니다.
         * 이 메소드를 호출하면
         * setCompoundDrawablesRelative 또는 관련 메서드를 사용하여 이전에 설정된 Drawable을 모두 덮어 씁니다.
         */
        mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.icon_video), null, null);

        mVoiceBtn.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.icon_voice), null, null);

        //end


        //손글씨 화면과 메모입력화면 전환시 슬라이딩 기법의 애니메이션 연출
        leftAnim = AnimationUtils.loadAnimation(this, R.anim.translate_left);
        rightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);
        SlidingPageAnimationListener animListener = new SlidingPageAnimationListener();
        leftAnim.setAnimationListener(animListener);
        rightAnim.setAnimationListener(animListener);
        //end

        //초기 입력화면의 선택된 버튼 결정
        insertTextBtn.setSelected(true);
        insertHandWritingBtn.setSelected(false);
        //end

        //이벤트 start
        insertTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewMode == 1) {
                    insertHandWritingView.setVisibility(View.GONE);
                    mMemoEidt.setVisibility(View.VISIBLE);
                    mMemoEidt.startAnimation(leftAnim);

                    insertTextBtn.setSelected(true);
                    insertHandWritingBtn.setSelected(false);

                    textViewMode = 0;
                }
            }
        });
        //버튼클릭 화면전화 및 전환시 애니메이션(슬라이딩)적용
        insertHandWritingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewMode == 0) {
                    insertHandWritingView.setVisibility(View.VISIBLE);
                    mMemoEidt.setVisibility(View.GONE);
                    insertHandWritingView.startAnimation(leftAnim);

                    insertHandWritingBtn.setSelected(true);
                    insertTextBtn.setSelected(false);

                    textViewMode = 1;
                }
            }
        });

        //사진 이미지뷰 참조
        mPhoto = (ImageView) findViewById(R.id.image_insertPhoto);
        //사진 클릭이벤트
        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "MemoInsertActivity iInsertPhoto onClick() 호출됨");
                //신규 수정 에 따라서 알림메뉴가 달라진다.
                if (isPhotoCaptured || isPhotoFileSaved) {
                    //갭쳐여부나 저장여부가 true 이면
                    showDialog(BasicInfo.CONTENT_PHOTO_EX);

                } else {
                    showDialog(BasicInfo.CONTENT_PHOTO);
                }
            }
        });

        //손글씨 이미지 뷰 클릭 이벤트
        insertHandWritingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HandWritingMakingActivity.class);
                startActivityForResult(intent, BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY);
            }
        });

        //정보삭제
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(BasicInfo.CONFIRM_DELETE);

            }
        });
        //동영상 버튼클릭
        mVideoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVideoRecorded || isVideoFileSaved) {
                    showDialog(BasicInfo.CONTENT_VIDEO_EX);

                } else {
                    showDialog(BasicInfo.CONTENT_VIDEO);
                }
            }
        });
        //음성 버튼 클릭
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isVoiceRecorded || isVoiceFilesaved) {
                    showDialog(BasicInfo.CONTENT_VOICE_EX);

                } else {

                    showDialog(BasicInfo.CONTENT_VOICE);
                }
            }
        });

        //end

        //참조부분
        setBottomButton();
        setMediaLayout();
        setCalendar();

        //리스트 및 새메모버튼 클릭여부 체크 뷰들의 상태 변경
        Intent intent = getIntent();
        mMemoMode = intent.getStringExtra(BasicInfo.KEY_MEMO_MODE);
        if (mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)) {
            processIntent(intent);
            //titleBackgroundButton.setText("메모 보기");
            setTitle(R.string.view_title);
            insertSaveBtn.setText(R.string.modify_btn);
            deleteBtn.setVisibility(View.VISIBLE);
        } else {
            //titleBackgroundButton.setText("새 메모");
            setTitle(R.string.new_title);
            insertSaveBtn.setText(R.string.save_btn);
            deleteBtn.setVisibility(View.GONE);

        }
        //이벤트 추가 start

        //이벤트 추가 end
    }
    Animation leftAnim;

    Animation rightAnim;


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
        //메모 id
        mMemoId = intent.getStringExtra(BasicInfo.KEY_MEMO_ID);
        //텍스트
        String curMemoText = intent.getStringExtra(BasicInfo.KEY_MEMO_TEXT);
        mMemoEidt.setText(curMemoText);
        //사진 이미지 id 및 이미지명
        mMediaPhotoId = intent.getStringExtra(BasicInfo.KEY_ID_PHOTO);
        mMediaPhotoUri = intent.getStringExtra((BasicInfo.KEY_URI_PHOTO));
        //동영상 id 및 동영상명
        mMediaVideoId = intent.getStringExtra(BasicInfo.KEY_ID_VIDEO);
        mMediaVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);
        //음성 id및 음성파일명
        mMediaVoiceId = intent.getStringExtra(BasicInfo.KEY_ID_VOICE);
        mMediaVoiceUri = intent.getStringExtra(BasicInfo.KEY_URI_VOICE);
        //손글씨 id및 손글씨이미지명
        mMediaHandwritingId = intent.getStringExtra(BasicInfo.KEY_ID_HANDWRITING);
        mMediaHandwritingUri = intent.getStringExtra(BasicInfo.KEY_URI_HANDWRITING);
        //입력 날짜
        mMomoDate = intent.getStringExtra(BasicInfo.KEY_MEMO_DATE);

        setMediaImage(mMediaPhotoId, mMediaPhotoUri, mMediaVideoId, mMediaVoiceId, mMediaHandwritingId);

        setMemoDate(mMemoDate);

        if (curMemoText != null && !curMemoText.equals("")) {
            textViewMode = 0;
            insertHandWritingView.setVisibility(View.GONE);
            mMemoEidt.setVisibility(View.VISIBLE);

            insertTextBtn.setSelected(true);
            insertHandWritingBtn.setSelected(false);

        } else {
            textViewMode = 1;
            insertHandWritingView.setVisibility(View.VISIBLE);
            mMemoEidt.setVisibility(View.GONE);

            insertTextBtn.setSelected(false);
            insertHandWritingBtn.setSelected(true);

        }
    }

    private void setMemoDate(String dateStr) {
        Log.d(TAG, "MemoInsertActivity setMemoDate() 호출 " + dateStr);
        Date date = new Date();
        try {
            if (BasicInfo.LANGUAGE.equals("ko")) {
                date = BasicInfo.dateNameformat2.parse(dateStr);
            } else {
                date = BasicInfo.dateNameFormat3.parse(dateStr);
            }

        } catch (Exception e) {
            Log.d(TAG, "날짜 파싱도중 예외발생", e);
        }

        mCalendar.setTime(date);
        int year = mCalendar.get(Calendar.YEAR);
        int monthOfyear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfyear + 1);

        if (monthOfyear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        //년월일버튼 설정
        if (BasicInfo.LANGUAGE.equals("ko")) {
            insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");
        } else {
            insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);
        }

        //시간버튼 설정
        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        //
        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }
        //로케일에 따라서 보여주는 패턴을 달리한다.
        if (BasicInfo.LANGUAGE.equals("ko")) {
            insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");

        } else {
            insertTimeButton.setText(hourStr + ":" + minuteStr);

        }
    }

    //동영상 녹화화면
    public void showVideoRecordingActivity() {
        Intent intent = new Intent(getApplicationContext(), VideoRecordingActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY);
    }

    //기존에 녹화된 동영상 파일로딩
    public void showVideoLoadingActivity() {
        Intent intent = new Intent(getApplicationContext(), VideoSelectionActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VIDEO_LOADING_ACTIVITY);
    }

    //동영상 재생화면
    public void showVideoPlayingActivity() {
        Intent intent = new Intent(getApplicationContext(), VideoPlayActivity.class);
        if (BasicInfo.isAbsoluteVideoPath(tempVideoUri)) {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO, BasicInfo.FOLDER_VIDEO + tempVideoUri);
        } else {
            intent.putExtra(BasicInfo.KEY_URI_VIDEO, tempVideoUri);
        }
        startActivity(intent);
    }

    //음성녹화화면
    public void showVoiceRecordingActivity() {
        Intent intent = new Intent(getApplicationContext(), VoiceREcordingActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_VOICE_RECORDING_ACTIVITY);
    }

    //음성재생화면
    public void showVoicePlayingActivity() {
        Intent intent = new Intent(getApplicationContext(), VoicePlayActivity.class);
        intent.putExtra(BasicInfo.KEY_URI_VOICE, BasicInfo.FOLDER_VOICE + tempVoiceUri);
        startActivity(intent);

    }

    //조회화면에서 리스트를 누르고 수정화면으로 왔을경우 기존 데이터를 표시해줘야 한다.
    private void setMediaImage(String photoId, String photoUri, String videoId, String voiceId, String handwritingId) {
        Log.d(TAG, "MemoInsertActivity setMediaImage() 호출됨 \n" + "photoId : " + photoId + " , " + "photoUrk :" + photoUri
                + " , videoId : " + videoId + " , voiceId : " + voiceId + " , handwritingId : " + handwritingId);

        //표시할 사진 유무에 따른 사진 세팅
        if (photoId.equals("") || photoId.equals("-1")) {
            if (BasicInfo.LANGUAGE.equals("ko")) {
                //저장된 사진이 없을경우는 사진선택관련 이미지 보여준다.
                mPhoto.setImageResource(R.drawable.person_add);

            } else {
                mPhoto.setImageResource(R.drawable.person_add_en);
            }

        } else {
            //표시할 사진이 있을경우
            isPhotoFileSaved = true;
            mPhoto.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + photoUri));
        }
        //손글씨 이미지 세팅
        if (handwritingId.equals("") || handwritingId.equals("-1")) {
            //저장된 이미지 없음
        } else {
            //저장된 이미지 있을경우
            isHandwritingFileSaved = true;
            tempHandWritingUri = tempHandWritingUri;

            Bitmap resultBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_HANDWRITING + tempHandWritingUri);
            insertHandWritingView.setImageBitmap(resultBitmap);
        }
        //동영상 유무에 따른 버튼 아이콘으로 표시
        if (videoId.equals("") || videoId.equals("-1")) {
            //없을경우
            mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                    (R.drawable.icon_video_empty), null, null);
        } else {
            //있을경우
            isVideoFileSaved = true;
            tempVideoUri = mMediaVideoUri;

            mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                    (R.drawable.icon_video), null, null);
        }
        //음성녹화 유무에 따른 버튼 아아콘 표시
        if (voiceId.equals("") || voiceId.equals("-1")) {
            //없을경우 비어있은 음성이미지 보여줌
            mVoiceBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                    (R.drawable.icon_voice_empty), null, null);

        } else {
            //있을경우 플래그 설정한다,저정된 음성이 있음을 나타내는 이미지를 버튼에 합성함.
            isVoiceFilesaved = true;
            mVoiceBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                    (R.drawable.icon_voice), null, null);

        }

    }

    //하단 메뉴 버튼 설정
    public void setBottomButton() {
        insertSaveBtn = (TitleBitmapButton) findViewById(R.id.button_saveMemo);
        insertCancelBtn = (TitleBitmapButton) findViewById(R.id.button_cancelMemo);
        //deleteBtn = (TitleBitmapButton) findViewById(R.id.button_delete);
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
        String sql = null;

        //photo 테이블에 입력후 입력한 사진명을 얻는다.
        String photoFileName = insertPhoto();
        int photoId = -1;

        if (photoFileName != null) {
            sql = "SELECT _id FROM " + MemoDatabase.TABLE_PHOTO + " WHERE URI = '" + photoFileName + "'";
            Log.d(TAG, "saveInput()  포토id획득 \n sql : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                //데이터 존재여부 첵크
                if (cursor.moveToNext()) {
                    photoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        //photo end
        //손글씨 start
        String handwritingFileName = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFileName != null) {
            sql = "SELECT _ID FROM " + MemoDatabase.TABLE_HANDWRITING + " WHERE URI = '" + handwritingFileName + "'";
            Log.d(TAG, "saveInput() 손글씨id 획득 \n sql : " + sql);
            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        //손글씨 end
        //video start
        String videoFileName = insertVideo();
        int videoId = -1;

        if (videoFileName != null) {
            sql = "SELECT _ID FROM " + MemoDatabase.TABLE_VIDEO + " WHERE URI = '" + videoFileName + "'";
            Log.d(TAG, "saveInput() 비데오 id 확득 \n sql : ");
            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    videoId = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        //video end
        //voice start
        String voiceFileName = insertVoice();
        int voiceId = -1;
        if (isVoiceRecorded && voiceFileName != null) {
            sql = "SELECT _ID FROM " + MemoDatabase.TABLE_VOICE + " WHERE URI = '" + voiceFileName + "'";
            Log.d(TAG,"saveInput() 보이스 id 획득 \n sql : " + sql);
            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    voiceId = cursor.getInt(0);
                }
                cursor.close();
            }
        }
        //voice end
        //photo hanwrithing video voice uri 들록후 각가의 id를 힉득했어면 memo table 각각의 id를 저장하낟.
        sql = "INSERT INTO " + MemoDatabase.TABLE_MEMO +
                " ( INPUT_DATE,CONTENT_TEXT,ID_PHOTO,ID_VIDEO,ID_VOICE,ID_HANDWRITING) " +
                "VALUES (" +
                "DATATIME('" + mDateStr + "'), " +
                "'" + mMemoStr + "'," +
                "'" + photoId + "'," +
                "'" + videoId + "'," +
                "'" + voiceId + "'," +
                "'" + handwritingId + "')";
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
        String sql = null;

        //선택된 사진이미지 업데이트 start
        String photoFileName = insertPhoto();
        int photoId = -1;

        if (photoFileName != null) {
            //파일명에 해당하는 photo id 를 photo 테이블에서 가져온다.
            sql = "SELECT _id FROM " + MemoDatabase.TABLE_PHOTO + " WHERE URI = '" + photoFileName + "'";
            Log.d(TAG, "조회쿼리 : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    //데이트가 존재한다면
                    photoId = cursor.getInt(0);
                }
                cursor.close();
                mMediaPhotoUri = photoFileName;

                sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                        " SET " +
                        " ID_PHOTO = '" + photoId + "'" +
                        " WHERE _ID = '" + mMemoId + "'";

                if (MultiMemoMainActivity.mDatabase != null) {

                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }
                mMediaPhotoId = String.valueOf(photoId);
            }
        } else if(isPhotoCanceled && isPhotoFileSaved) {
            //수정시 새로운 사진을 선택한경우 기존선택된 사진정보 삭제후 신규 사진정보 갱신
            sql = "DELETE FROM " + MemoDatabase.TABLE_PHOTO +
                    " WHERE _ID = '" + mMediaPhotoId + "'";

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
                    " WHERE _ID = '" + mMemoId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            //신규 photo id
            mMediaPhotoId = String.valueOf(photoId);
        }
        //사진이미지 업데이트 end
        //손글씨수정 start
        String handwritingFileName = insertHandwriting();
        int handwritingId = -1;

        if (handwritingFileName != null) {

            sql = "SELECT _id FROM " + MemoDatabase.TABLE_HANDWRITING + " WHERE URI = '" + handwritingFileName + "'";
            Log.d(TAG, "조회쿼리 : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    //데이트가 존재한다면
                    handwritingId = cursor.getInt(0);
                }
                cursor.close();

                mMediaHandwritingUri = handwritingFileName;

                sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                        " SET " +
                        " ID_HANDWRITING = '" + handwritingId + "'" +
                        " WHERE _ID = '" + mMemoId + "'";

                if (MultiMemoMainActivity.mDatabase != null) {

                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }

                mMediaHandwritingId = String.valueOf(handwritingId);
            }
        } else if(isHandwritingCanceled && isHandwritingFileSaved) {
            sql = "DELETE FROM " + MemoDatabase.TABLE_HANDWRITING +
                    " WHERE _ID = '" + mMediaHandwritingId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            File handwritingFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingUri);
            if (handwritingFile.exists()) {
                handwritingFile.delete();
            }

            sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                    "SET " +
                    " ID_HANDWRITING = " + handwritingId +
                    " WHERE _ID = '" + mMemoId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }

            mMediaHandwritingId = String.valueOf(handwritingId);
        }
        //손글씨수정 end

        //동영상 수정 start
        String videFileName = insertVideo();
        int videoId = -1;

        if (videFileName != null) {

            sql = "SELECT _id FROM " + MemoDatabase.TABLE_VIDEO + " WHERE URI = '" + videFileName + "'";
            Log.d(TAG, "조회쿼리 : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    //데이트가 존재한다면
                    videoId = cursor.getInt(0);
                }
                cursor.close();
                mMediaVideoUri = videFileName;

                sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                        " SET " +
                        " ID_VIDEO = '" + videoId + "'" +
                        " WHERE _ID = '" + mMemoId + "'";

                if (MultiMemoMainActivity.mDatabase != null) {

                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }

                mMediaVideoId = String.valueOf(videoId);
            }
        } else if(isVideoCanceled && isVideoFileSaved) {
            sql = "DELETE FROM " + MemoDatabase.TABLE_VIDEO +
                    " WHERE _ID = '" + mMediaVideoId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            File videoFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaVideoUri);
            if (videoFile.exists()) {
                videoFile.delete();
            }

            sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                    "SET " +
                    " ID_VIDEO = " + videoId +
                    " WHERE _ID = '" + mMemoId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }

            mMediaVideoId = String.valueOf(videoId);
        }
        //동영상 수정 end

        //음성 수정 start
        String voiceFileName = insertVoice();
        int voiceId = -1;

        if (voiceFileName != null) {

            sql = "SELECT _id FROM " + MemoDatabase.TABLE_VOICE + " WHERE URI = '" + voiceFileName + "'";
            Log.d(TAG, "조회쿼리 : " + sql);

            if (MultiMemoMainActivity.mDatabase != null) {
                Cursor cursor = MultiMemoMainActivity.mDatabase.rawQuery(sql);
                if (cursor.moveToNext()) {
                    //데이트가 존재한다면
                    voiceId = cursor.getInt(0);
                }
                cursor.close();
                mMediaVoiceUri = voiceFileName;

                sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                        " SET " +
                        " ID_VOICE = '" + voiceId + "'" +
                        " WHERE _ID = '" + mMemoId + "'";

                if (MultiMemoMainActivity.mDatabase != null) {

                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }

                mMediaVoiceId = String.valueOf(voiceId);
            }
        } else if(isVoiceCanceled && isVoiceFilesaved) {
            sql = "DELETE FROM " + MemoDatabase.TABLE_VOICE +
                    " WHERE _ID = '" + mMediaVoiceId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }
            File voiceFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaVoiceUri);
            if (voiceFile.exists()) {
                voiceFile.delete();
            }

            sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                    "SET " +
                    " ID_VOICE = " + voiceId +
                    " WHERE _ID = '" + mMemoId + "'";

            if (MultiMemoMainActivity.mDatabase != null) {
                MultiMemoMainActivity.mDatabase.execSQL(sql);
            }

            mMediaVoiceId = String.valueOf(voiceId);
        }
        //음성 수정 end

        //update memo info
        sql = "UPDATE " + MemoDatabase.TABLE_MEMO +
                " SET " +
                " INPUT_DATE = DATETIME('" + mDateStr + "')," +
                " CONTENT_TEXT = '" + mMemoStr + "'" +
                " WHERE _ID = '" + mMemoId + "'";

        Log.d(TAG, "sql : " + sql);

        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }

        intent.putExtra(BasicInfo.KEY_MEMO_TEXT, mMemoStr);
        intent.putExtra(BasicInfo.KEY_ID_PHOTO, mMediaPhotoId);
        intent.putExtra(BasicInfo.KEY_ID_VIDEO, mMediaVideoId);
        intent.putExtra(BasicInfo.KEY_ID_VOICE, mMediaVoiceId);
        intent.putExtra(BasicInfo.KEY_ID_HANDWRITING, mMediaHandwritingId);

        intent.putExtra(BasicInfo.KEY_URI_PHOTO, mMediaPhotoUri);
        intent.putExtra(BasicInfo.KEY_URI_VIDEO, mMediaVideoUri);
        intent.putExtra(BasicInfo.KEY_URI_VOICE, mMediaVoiceUri);;
        intent.putExtra(BasicInfo.KEY_URI_HANDWRITING, mMediaHandwritingUri);;

        setResult(RESULT_OK, intent);
        finish();
    }

    //start
    /**
     * 1.갭쳐된 이미지나 앨범에서 선택된 사진을 멀티메모의 사진폴더에 복사한다.
     * 2.이미지의 이름은 현재 시간을 기준으로 한 getTime() 값의 문자열을 사용한다.
     *
     * @return
     */
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
                    photoFolder.mkdirs();
                }

                photoName = createFileName();

                FileOutputStream outputStream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultPhotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                if (photoName != null) {
                    Log.d(TAG, "isPhotoCaptured   : " + isPhotoCaptured);
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
    //손글씨
    private String insertHandwriting() {
        Log.d(TAG, "MemoInsertActivity insertHandwriting() 호출");

        String handwritingName = null;

        if (isHandwritingMade) {
            try {
                //수정일경우
                if (mMemoMode != null && mMemoMode.equals(BasicInfo.MODE_MODIFY)) {
                    Log.d(TAG, "이전 손글씨정보 삭제");

                    String sql = "DELETE FROM " + MemoDatabase.TABLE_HANDWRITING +
                            " WHERE _ID = '" + mMediaHandwritingId + "'";

                    Log.d(TAG, "sql : " + sql);

                    if (MultiMemoMainActivity.mDatabase != null) {
                        MultiMemoMainActivity.mDatabase.execSQL(sql);
                    }

                    File previousFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingUri);

                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }

                File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);

                //폴더가 없다면 폴더를 생성한다.
                if (!handwritingFolder.isDirectory()) {
                    Log.d(TAG, "폴더생성 : " + handwritingFolder);
                    handwritingFolder.mkdirs();
                }

                handwritingName = createFileName();

                FileOutputStream outputStream = new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handwritingName);

                resultHandwritingBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();

                if (handwritingName != null) {
                    Log.d(TAG, "isHandwritingMade   : " + isHandwritingMade);

                    //insert picture info
                    String sql = "INSERT INTO " + MemoDatabase.TABLE_HANDWRITING + " (uri) VALUES (" +
                            "'" + handwritingName + "'";

                    if (MultiMemoMainActivity.mDatabase != null) {
                        MultiMemoMainActivity.mDatabase.execSQL(sql);
                    }
                }

            } catch (IOException ex) {
                Log.e(TAG, "예외발생 : copying handwriting", ex);
            }
        }
        return handwritingName;
    }
    //동영상
    private String insertVideo() {
        Log.d(TAG, "MemoInsertActivity insertVideo() 호출");

        String videoName = null;

        if (isVideoRecorded) {
            //수정일경우
            if (mMemoMode != null && ( mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW ))) {
                Log.d(TAG, "이전 동영상정보 삭제");

                String sql = "DELETE FROM " + MemoDatabase.TABLE_VIDEO +
                        " WHERE _ID = '" + mMediaVideoId + "'";

                Log.d(TAG, "sql : " + sql);

                if (MultiMemoMainActivity.mDatabase != null) {
                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }

                if(BasicInfo.isAbsoluteVideoPath(mMediaVideoUri)) {

                    File previousFile = new File(BasicInfo.FOLDER_VIDEO + mMediaVideoUri);

                    if (previousFile.exists()) {
                        previousFile.delete();
                    }
                }
            }

            if(BasicInfo.isAbsoluteVideoPath(tempVideoUri)) {

                File videoFolder = new File(BasicInfo.FOLDER_VIDEO);

                //폴더가 없다면 폴더를 생성한다.
                if (!videoFolder.isDirectory()) {
                    Log.d(TAG, "폴더생성 : " + videoFolder);
                    videoFolder.mkdirs();
                }

                videoName = createFileName();

                File tempFile = new File(BasicInfo.FOLDER_VIDEO + "recorded");
                tempFile.renameTo(new File(BasicInfo.FOLDER_VIDEO + videoName));

            } else {
                videoName = tempVideoUri;
            }

            if (videoName != null) {
                Log.d(TAG, "isVideoRecorded   : " + isVideoRecorded);

                //insert picture info
                String sql = "INSERT INTO " + MemoDatabase.TABLE_VIDEO + " (uri) VALUES (" +
                        "'" + videoName + "'";

                if (MultiMemoMainActivity.mDatabase != null) {
                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }
            }

        }
        return videoName;
    }
    //보이스
    private String insertVoice() {
        Log.d(TAG, "MemoInsertActivity insertVoice() 호출");

        String voiceName = null;

        if (isVoiceRecorded) {

            //수정일경우
            if (mMemoMode != null && ( mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW ))) {
                Log.d(TAG, "이전 동영상정보 삭제");

                String sql = "DELETE FROM " + MemoDatabase.TABLE_VOICE +
                        " WHERE _ID = '" + mMediaVoiceId + "'";

                Log.d(TAG, "sql : " + sql);

                if (MultiMemoMainActivity.mDatabase != null) {
                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }


                File previousFile = new File(BasicInfo.FOLDER_VOICE + mMediaVoiceUri);

                if (previousFile.exists()) {
                    previousFile.delete();
                }

            }

            File voiceFolder = new File(BasicInfo.FOLDER_VOICE);

            //폴더가 없다면 폴더를 생성한다.
            if (!voiceFolder.isDirectory()) {
                Log.d(TAG, "폴더생성 : " + voiceFolder);
                voiceFolder.mkdirs();
            }

            voiceName = createFileName();

            File tempFile = new File(BasicInfo.FOLDER_VOICE + "recorded");
            tempFile.renameTo(new File(BasicInfo.FOLDER_VOICE + voiceName));



            if (voiceName != null) {
                Log.d(TAG, "isVoiceRecorded   : " + isVoiceRecorded);

                //insert voice info
                String sql = "INSERT INTO " + MemoDatabase.TABLE_VOICE + " (uri) VALUES (" +
                        "'" + voiceName + "'";

                if (MultiMemoMainActivity.mDatabase != null) {
                    MultiMemoMainActivity.mDatabase.execSQL(sql);
                }
            }
        }
        return voiceName;
    }

    //end

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
        isHandwritingMade = false;

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
                String dateStr = insertDateButton.getText().toString().trim();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();

                try {
                    if (BasicInfo.LANGUAGE.equals("ko")) {
                        date = BasicInfo.dateDayNameFormat.parse(dateStr);
                    } else {
                        date = BasicInfo.dateDayFormat.parse(dateStr);
                    }

                } catch (ParseException e) {
                    Log.e(TAG, "Exception in parsing date : " + date,e);
                }
                calendar.setTime(date);

                new DatePickerDialog(MemoInsertActivity.this,
                        dataSetListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        insertTimeButton =  (TitleBitmapButton) findViewById(R.id.button_insertTime);
        insertTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStr = insertTimeButton.getText().toString().trim();
                Calendar calendar = Calendar.getInstance();
                Date date = new Date();
                try {
                    if (BasicInfo.LANGUAGE.equals("ko")) {
                        date = BasicInfo.dateTimeKoFormat.parse(timeStr);
                    } else {
                        date = BasicInfo.dateTimeFormat.parse(timeStr);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception in parsing date : " + date);
                }
                calendar.setTime(date);
                new TimePickerDialog(
                        MemoInsertActivity.this,
                        timeSetListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                ).show();

            }
        });

        Date curDate = new Date();
        mCalendar.setTime(curDate);

        int year = mCalendar.get(Calendar.YEAR);
        int monthOfYear = mCalendar.get(Calendar.MONTH);
        int dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(monthOfYear + 1);
        if (monthOfYear < 9) {
            monthStr = "0" + monthStr;
        }

        String dayStr = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            dayStr = "0" + dayStr;
        }

        if (BasicInfo.LANGUAGE.equals("ko")) {
            insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");

        } else {
            insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);
        }

        //시간설정
        int hourOfDay = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);

        String hourStr = String.valueOf(hourOfDay);
        if (hourOfDay < 10) {
            hourStr = "0" + hourStr;
        }

        String minuteStr = String.valueOf(minute);
        if (minute < 10) {
            minuteStr = "0" + minuteStr;
        }

        if (BasicInfo.LANGUAGE.equals("ko")) {
            insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");

        } else {
            insertTimeButton.setText(hourStr + ":" + minuteStr);

        }
    }

    //날짜설정 리스너
    DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Log.d(TAG, "MemoInsertActivity DatePickerDialog.OnDateSetListener onDateSet() 호출");
            mCalendar.set(year, month, dayOfMonth);

            String monthStr = String.valueOf(month + 1);
            if (month < 9) {
                monthStr = "0" + monthStr;
            }

            String dayStr = String.valueOf(dayOfMonth);
            if (dayOfMonth < 10) {
                dayStr = "0" + dayStr;
            }

            if (BasicInfo.LANGUAGE.equals("ko")) {
                insertDateButton.setText(year + "년 " + monthStr + "월 " + dayStr + "일");

            } else {
                insertDateButton.setText(year + "-" + monthStr + "-" + dayStr);

            }
        }
    };

    //시간설정 리스너
    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE,minute);

            String hourStr = String.valueOf(hourOfDay);
            if (hourOfDay < 10) {
                hourStr = "0" + hourStr;
            }

            String minuteStr = String.valueOf(minute);
            if (minute < 10) {
                minuteStr = "0" + minuteStr;
            }

            if (BasicInfo.LANGUAGE.equals("ko")) {
                insertTimeButton.setText(hourStr + "시 " + minuteStr + "분");

            } else {
                insertTimeButton.setText(hourStr + "-" + minuteStr);
            }
        }
    };

    //일자와 메모확인
    private boolean parseValues() {
        Log.d(TAG, "MemoInsertActivity parseValues() 호출");

        String insertDateStr = insertDateButton.getText().toString();
        String insertTimeStr = insertTimeButton.getText().toString();

        String srcDateStr = insertDateStr + " " + insertTimeStr;
        Log.d(TAG, "srcDateStr : " + srcDateStr);

        try {
            if (BasicInfo.LANGUAGE.equals("ko")) {
                Date insertDate = BasicInfo.dateNameFormat.parse(srcDateStr);
                mDateStr = BasicInfo.dateFormat.format(insertDate);
            } else {
                Date insertDate = BasicInfo.dateNameFormat3.parse(srcDateStr);
                mDateStr = BasicInfo.dateFormat.format(insertDate);
            }

        } catch (ParseException ex) {
            Log.e(TAG, "Exception in parsing date : " + insertDateStr);
        }

        mMemoStr = mMemoEidt.getText().toString();
        //if handwriting is avaiable
        if (isHandwritingMade || (mMemoMode != null && (mMemoMode.equals(BasicInfo.MODE_MODIFY) || mMemoMode.equals(BasicInfo.MODE_VIEW)))) {

        } else {
            //check text memo
            if (mMemoStr.trim().length() < 1) {
                //텍스입력없을 경우 경고창에 입력독려 메시지 띄움?
                showDialog(BasicInfo.CONFIRM_TEXT_INPUT);
                return false;
            }
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
                builder.setTitle(R.string.memo_title);
                builder.setMessage("텍스트를 입력하세요.");
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case BasicInfo.CONTENT_PHOTO: //새메모일경우

                builder = new AlertDialog.Builder(this);
                mSelectdContentArray = R.array.array_photo;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoicedArrayItem = which;
                    }
                });
                //선택
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //직접촬영 과 앨범에서 사진선택에 땨라 분기한다.
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else {
                            showPhotoLoadingActivity();
                        }
                    }
                });
                //취소
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
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
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChoicedArrayItem = which;
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mChoicedArrayItem == 0) {
                            showPhotoCaptureActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showPhotoLoadingActivity();
                        } else if (mChoicedArrayItem == 2) {
                            isPhotoCanceled = true;
                            isPhotoCaptured = false;

                            if (BasicInfo.LANGUAGE.equals("ko")) {
                                mPhoto.setImageResource(R.drawable.person_add);

                            } else {
                                mPhoto.setImageResource(R.drawable.person_add_en);

                            }
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "취소버튼 눌렀음.");
                    }
                });
                break;
            case BasicInfo.CONFIRM_DELETE:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.memo_title);
                builder.setMessage(R.string.memo_delete_question);
                builder.setPositiveButton(R.string.yes_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMemo();
                    }
                });
                builder.setNegativeButton(R.string.no_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismissDialog(BasicInfo.CONFIRM_DELETE);
                    }
                });
                break;

            case BasicInfo.CONTENT_VIDEO:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_video;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "video 알림창 선택된 idex : " + which);
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "확인버튼 선택 선택된 index : " + which);
                    }
                });
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case BasicInfo.CONTENT_VIDEO_EX:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_video_ex;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "학장 video 메뉴중 선택된 Index : " + which);
                        mChoicedArrayItem = which;
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "확인 버튼 클릭됨 선택된 Index : " + which);
                        if (mChoicedArrayItem == 0) {
                            showVideoPlayingActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showVideoRecordingActivity();
                        } else if (mChoicedArrayItem == 2) {
                            showVideoLoadingActivity();
                        } else if (mChoicedArrayItem == 3) {
                            isVideoCanceled = true;
                            isVideoRecorded = false;

                            mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                                    (R.drawable.icon_video_empty), null, null);
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case BasicInfo.CONTENT_VOICE:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_voice;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Log.d(TAG, "음성메뉴중 선택한 메뉴 index : " + whichButton);
                        mChoicedArrayItem = whichButton;
                    }
                });
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "음성확인버튼 눌렀음 선ㄷ택된 메뉴 Index : " + which);
                        if (mChoicedArrayItem == 0) {
                            showVoiceRecordingActivity();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                break;
            case BasicInfo.CONTENT_VOICE_EX:
                builder = new AlertDialog.Builder(this);

                mSelectdContentArray = R.array.array_voice_ex;
                builder.setTitle(R.string.selection_title);
                builder.setSingleChoiceItems(mSelectdContentArray, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "선택된 메뉴 index : " + which);
                        mChoicedArrayItem = which;
                    }
                });
                //학인버튼생성
                builder.setPositiveButton(R.string.selection_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "음성확장메뉴 확인버튼 클릭 선택된 메뉴 index : " + which);
                        if (mChoicedArrayItem == 0) {
                            showVoicePlayingActivity();
                        } else if (mChoicedArrayItem == 1) {
                            showVoiceRecordingActivity();
                        } else if (mChoicedArrayItem == 2) {
                            isVoiceCanceled = true;
                            isVoiceRecorded = false;
                            mVoiceBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                                    (R.drawable.icon_voice_empty), null, null);
                        }
                    }
                });
                //취소버튼생성
                builder.setNegativeButton(R.string.cancel_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                break;
            default:
                break;
        }

        return builder.create();
    }

    //메모삭제
    private void deleteMemo() {
        //delete photo record and file start
        Log.d(TAG, "deleting previous photo record and file : " + mMediaPhotoId);
        String sql = "DELETE FROM " + MemoDatabase.TABLE_PHOTO +
                " WHERE _ID = '" + mMediaPhotoId + "'";
        Log.d(TAG, "이전 포토이미지 삭제 sql : " + sql);

        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }
        //파일삭제
        File photoFile = new File(BasicInfo.FOLDER_PHOTO + mMediaPhotoId);
        if (photoFile.exists()) {
            photoFile.delete();
        }
        //end
        //delete handwriting record and file start
        Log.d(TAG, "deleting previous handwriting record and file : " + mMediaHandwritingId);
        sql = "DELETE FROM " + MemoDatabase.TABLE_HANDWRITING +
                " WHERE _ID = '" + mMediaHandwritingId + "'";
        Log.d(TAG, "이전 손글씨이미지 삭제 sql : " + sql);

        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }
        //파일삭제
        File handFile = new File(BasicInfo.FOLDER_HANDWRITING + mMediaHandwritingId);
        if (photoFile.exists()) {
            photoFile.delete();
        }
        //end
        //동영상 보이스 테이블 정보는?

        //메모테이블 정보 삭제
        Log.d(TAG, "DELETING PREVIOUS MEMO RECORD :  " + mMemoId);
        sql = "DELETE FROM " + MemoDatabase.TABLE_MEMO + " WHERE _ID = '" + mMemoId + "'";
        Log.d(TAG, "메모정보 삭제 : " + sql);
        if (MultiMemoMainActivity.mDatabase != null) {
            MultiMemoMainActivity.mDatabase.execSQL(sql);
        }
        setResult(RESULT_OK);
        finish();
    }

    //사진미리보기및 촬영화면 (개선점:미리보기가 및 선택부분 새로운 기법고려필요)
    private void showPhotoCaptureActivity() {
        Intent intent = new Intent(getApplicationContext(), PhotoCaptureActivity.class);
        startActivityForResult(intent, BasicInfo.REQ_PHOTO_CAPTURE_ACTIVITY);
    }
    //사진폴던내 사진보여주기 , 그중에서 한개 선택하기 (개선점:멀티선택 기능 고려필요)
    private void showPhotoLoadingActivity() {
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

                    //BitmapFactory.Options options = new BitmapFactory.Options();
                    //sinaburokim
                    //options.inSampleSize = 8;

                    try {
                        resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(getPhotoUri));
                        Log.d(TAG, "REQ_PHOTO_SELECTION_ACTIVITY BitmapFactory.decodeStream : " + getPhotoUri);
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "이미지 디코드중 예외발생", e);
                    }
                    mPhoto.setImageBitmap(resultPhotoBitmap);
                    isPhotoCaptured = true;
                    mPhoto.invalidate();
                }

                break;
            case BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY:
                Log.d(TAG, "손글씨 저장 요청 응답 액티비티 코드 REQ_HANDWRITING_MAKING_ACTIVITY : " + BasicInfo.REQ_HANDWRITING_MAKING_ACTIVITY);
                if (resultCode == RESULT_OK) {
                    boolean isHandwritingFile = checkMadeHandwritingFile();
                    if (isHandwritingFile) {
                        resultHandwritingBitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_HANDWRITING + "made");
                        tempHandWritingUri = "made";
                        isHandwritingMade = true;
                        insertHandWritingView.setImageBitmap(resultHandwritingBitmap);
                    }
                }
                break;
            case BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY:
                Log.d(TAG, "동영상 녹화 응답코드: " + BasicInfo.REQ_VIDEO_RECORDING_ACTIVITY);

                if (resultCode == RESULT_OK) {
                    boolean isVideoExists = checkRecordedVideoFile();
                    if (isVideoExists) {
                        tempVideoUri = "recorded";
                        isVideoRecorded = true;
                        mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                                (R.drawable.icon_video), null, null);
                    }
                }
                break;
            case BasicInfo.REQ_VIDEO_LOADING_ACTIVITY:
                Log.d(TAG, "동영상 불러오기 응답코드 : " + BasicInfo.REQ_VIDEO_LOADING_ACTIVITY);
                if (resultCode == RESULT_OK) {
                    String getVideoUri = intent.getStringExtra(BasicInfo.KEY_URI_VIDEO);
                    tempVideoUri = BasicInfo.URI_MEDIA_FORMAT + getVideoUri;
                    isVideoRecorded = true;
                    mVideoBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                            (R.drawable.icon_video), null, null);
                }
                break;
            case BasicInfo.REQ_VOICE_RECORDING_ACTIVITY:
                Log.d(TAG, "음성녹화 응답코드 : " + BasicInfo.REQ_VOICE_RECORDING_ACTIVITY);
                if (resultCode == RESULT_OK) {
                    boolean isVoiceExists = checkRecordedVoiceFile();
                    if (isVoiceRecorded) {
                        tempVoiceUri = "recorded";
                        isVoiceRecorded = true;
                        mVoiceBtn.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable
                                (R.drawable.icon_voice), null, null);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     *   저장된 손글씨 파일 존재여부 첵크
     * @return
     */
    private boolean checkMadeHandwritingFile() {
        File file = new File(BasicInfo.FOLDER_HANDWRITING + "made");
        if (file.exists()) {
            return true;
        }
        return false;
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

    //동영상파일확인
    private boolean checkRecordedVideoFile() {
        File file = new File(BasicInfo.FOLDER_VIDEO + "recorded");
        if (file.exists()) {
            return true;
        }
        return false;
    }

    //음성파일확인
    private boolean checkRecordedVoiceFile() {
        File file = new File(BasicInfo.FOLDER_VOICE + "recorded");
        if (file.exists()) {
            return true;
        }
        return false;
    }
}
