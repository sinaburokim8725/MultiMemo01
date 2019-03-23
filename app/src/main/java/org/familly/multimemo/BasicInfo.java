package org.familly.multimemo;

import android.util.Log;

import java.text.SimpleDateFormat;

public  class BasicInfo {

    public static final String LOG_TAG = "MultiMemo > "+BasicInfo.class.getSimpleName();


    public static String LANGUAGE = "";

    public static final String KEY_MEMO_MODE = "MEMO_MODE";
    public static final String MODE_INSERT   = "MODE_INSERT";
    public static final String MODE_MODIFY   = "MODE_MODIFY";
    public static final String MODE_VIEW     = "MODE_VIEW";
    //인텐트 부가정보중 KEY 값들
    public static final String KEY_MEMO_ID   = "MEMO_ID";
    public static final String KEY_MEMO_DATE = "MEMO_DATE";
    public static final String KEY_MEMO_TEXT = "MENO_TEXT";

    //END
    public static final int REQ_VIEW_ACTIVITY   = 1001;
    public static final int REQ_INSERT_ACTIVITY = 1002;

    //클릭시 첵크사항
    public static final int CONFIRM_DELETE = 3001;

    public static final int CONTENT_VOICE = 2003;
    public static final int CONTENT_VOICE_EX = 2007;

    public static final int CONTENT_VIDEO = 2002;
    public static final int CONTENT_VIDEO_EX = 2006;

    public static final int CONTENT_PHOTO_EX   = 2005;
    public static final int CONTENT_PHOTO      = 2001;
    //
    public static final int CONFIRM_TEXT_INPUT = 3002;

    public static final int IMAGE_CANNOT_STORED = 1002;



    //이벤트에 따른 이동할 액티비티 응답코드 및 부가정보 KEY
    public static final int REQ_PHOTO_CAPTURE_ACTIVITY      = 1501;
    public static final int REQ_PHOTO_SELECTION_ACTIVITY    = 1502;
    public static final int REQ_VIDEO_RECORDING_ACTIVITY    = 1503;
    public static final int REQ_VIDEO_LOADING_ACTIVITY      = 1504;
    public static final int REQ_VOICE_RECORDING_ACTIVITY    = 1505;
    public static final int REQ_HANDWRITING_MAKING_ACTIVITY = 1506;

    public static final String KEY_ID_HANDWRITING  = "ID_HANDWRITING";
    public static final String KEY_URI_HANDWRITING = "URI_HANDWRITING";
    public static final String KEY_ID_PHOTO        = "ID_PHOTO";
    public static final String KEY_URI_PHOTO       = "URI_PHOTO";
    public static final String KEY_ID_VIDEO        = "ID_VIDEO";
    public static final String KEY_URI_VIDEO       = "URI_VIDEO";
    public static final String KEY_ID_VOICE        = "ID_VOICE";
    public static final String KEY_URI_VOICE       = "URI_VOICE";

    //사진 저장위치
    public static String FOLDER_PHOTO =
            "MultimediaMemo/photo/";

    //비데오 저장위치
    public static String FOLDER_VIDEO =
            "MultimediaMemo/video/";

    //음성 저장위치
    public static String FOLDER_VOICE =
            "MultimediaMemo/voice/";

    //손글씨 저장위치
    public static String FOLDER_HANDWRITING =
            "MultimediaMemo/handwriting/";

    //미디어 포맷 형식
    public static String URI_MEDIA_FORMAT =
            "content://media";

    //외부 저장소위치
    public static boolean ExternalChecked = false;
    public static String  ExternalPath    = "/sdcard/";

    //Database Name
    public static String DATABASE_NAME = "MultimediaMemo/memo.db";

    //날짜시간 포맷설정
    public static SimpleDateFormat dateDayNameFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
    public static SimpleDateFormat dateDayFormat = new SimpleDateFormat("yyyy-MM-dd");


    public static SimpleDateFormat dateTimeKoFormat = new SimpleDateFormat("HH시 mm분");
    public static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm");

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static SimpleDateFormat dateNameFormat  = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분");
    public static SimpleDateFormat dateNameformat2 = new SimpleDateFormat("yyyy-MM-dd HH시 mm분");
    public static SimpleDateFormat dateNameFormat3 = new SimpleDateFormat("yyyy-MM-dd HH:mm");


    public static boolean isAbsoluteVideoPath(String tempVideoUri) {
        

        Log.d(LOG_TAG, "궁금하네 BasicInfo isAbsoluteVidePath() tempVideoUri : " + tempVideoUri);
        if (tempVideoUri.startsWith(URI_MEDIA_FORMAT)) {
            return false;
        } else {
            return true;
        }
    }
}
