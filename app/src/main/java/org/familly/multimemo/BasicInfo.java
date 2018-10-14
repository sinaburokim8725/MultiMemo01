package org.familly.multimemo;

public  class BasicInfo {

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
}
