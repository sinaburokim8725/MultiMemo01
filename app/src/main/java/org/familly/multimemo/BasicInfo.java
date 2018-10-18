package org.familly.multimemo;

import java.text.SimpleDateFormat;

public  class BasicInfo {

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

    //사진클릭시 첵크상태
    public static final int CONTENT_PHOTO_EX   = 2005;
    public static final int CONTENT_PHOTO      = 2001;
    //
    public static final int CONFIRM_TEXT_INPUT = 3002;

    //사진클릭 첵크상태에따른 이동할 액티비티 응답코드
    public static final int REQ_PHOTO_CAPTURE_ACTIVITY   = 1501;
    public static final int REQ_PHOTO_SELECTION_ACTIVITY = 1502;
    public static final String KEY_ID_HANDWRITING = "ID_HANDWRITING";
    public static final String KEY_URI_HANDWRITING = "URI_HANDWRITING";
    public static final String KEY_ID_PHOTO = "ID_PHOTO";
    public static final String KEY_URI_PHOTO = "URI_PHOTO";
    public static final String KEY_ID_VIDEO = "ID_VIDEO";
    public static final String KEY_URI_VIDEO = "URI_VIDEO";
    public static final String KEY_ID_VOICE = "ID_VOICE";
    public static final String KEY_URI_VOICE = "URI_VOICE";

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
    public static SimpleDateFormat dateDayNameFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

}
