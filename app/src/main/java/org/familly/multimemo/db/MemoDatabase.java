package org.familly.multimemo.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.familly.multimemo.BasicInfo;

public class MemoDatabase {
    public static final String TAG = "DEBUG";
    public static int DATABASE_VERSION = 1;

    //싱글톤 인스턴스
    private static MemoDatabase database;
    //table name
    public static String TABLE_MEMO  = "MEMO";
    public static String TABLE_PHOTO = "PHOTO";
    public static String TABLE_VIDEO = "VIDEO";
    public static String TABLE_VOICE = "VOICE";
    public static String TABLE_HANDWRITHING = "HANDWRITING";


    //helper class defined
    private DatabaseHelper dbHelper;

    //SQLiteDatabase 인스턴스
    private SQLiteDatabase db;

    //컨텍스트 객체
    private Context context;

    public MemoDatabase(Context context) {
        this.context = context;
    }
    //인스턴스 가져오기
    public static MemoDatabase getInstance(Context context) {
        if (database == null) {
            database = new MemoDatabase(context);
        }
        return database;
    }

    //데이터베이스 열기
    public boolean open() {
        println("opening database [" + BasicInfo.DATABASE_NAME + "]");
        //헬퍼 객체얻기
        dbHelper = new DatabaseHelper(context);
        //SQLIiteDatabase 객체 얻기
        db = dbHelper.getWritableDatabase();
        return true;
    }

    //데이터베이스 닫기
    public void close() {
        println("closing database [" + BasicInfo.DATABASE_NAME + "]");
        db.close();
        database = null;
    }

    /**
     *
     * @param sql
     * @return
     */
    public Cursor rawQuery(String sql) {
        println("데이터조회 호출 rawQuery()");

        Cursor cursor = null;
        try {

            cursor = db.rawQuery(sql, null);
            println("총건수 : " + cursor.getCount());
        } catch (Exception ex) {
            Log.e(TAG, "예외발생 rawQuery() ", ex);
        }
        return cursor;
    }

    //조회 이외의 sql 실행
    public boolean execSQL(String sql) {
        println("execSQL() 호출됨");

        try {
            Log.d(TAG, "쿼리내용: " + sql);
            db.execSQL(sql);
        } catch (Exception ex) {
            Log.e(TAG, "예외발생 execSQL()", ex);
            return false;
        }
        return true;
    }
    //로그용
    private void println(String msg) {
        Log.d(TAG,msg);
    }
    /**
     * Database Helper inner class
     */
    private class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "DEBUG";

        /**
         *
         * @param context

         */
        public DatabaseHelper(Context context) {

            super(context, BasicInfo.DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            println("creating database [" + BasicInfo.DATABASE_NAME + " ] ");

            //table memo
            println("creating table [" + TABLE_MEMO + " ]");

            //drop existing table
            String DROP_SQL = "DROP TABLE if exists " + TABLE_MEMO;
            try {
                db.execSQL(DROP_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "Exception in DROP_SQL", ex);

            }
            //create table memo
            String CREATE_SQL = "CREATE TABLE " + TABLE_MEMO + "("
                    + "_id            INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "input_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "content_text   TEXT DEFAULT '',"
                    + "id_photo       INTEGER,"
                    + "id_video       INTEGER,"
                    + "id_voice       INTEGER,"
                    + "id_handwriting INTEGER,"
                    + "create_date    TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "EXCEPTION IN CREATE_SQL", ex);
            }


            //table photo
            println("creating table [" + TABLE_PHOTO + "]");

            //테이블 만들기전에 있으면 드롭하고 만들기? 신규인데 이럴가능성은 없다고 봐야지
            DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_PHOTO;

            //create photo table
            CREATE_SQL = "CREATE TABLE " + TABLE_PHOTO + "("
                    + " _id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + " uri TEXT,"
                    + " create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch (Exception ex) {
                Log.e(TAG,"Exception in create photo table",ex);
            }

            //Create index photo
            println("creating index in table photo" );
            String CREATE_INDEX_SQL = "CREATE INDEX " + TABLE_PHOTO +  "_IDX ON " + TABLE_PHOTO + "("
                                       + "uri"
                                       + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex) {
                Log.e(TAG,"Exception index in photo",ex);
            }
            //table video
            println("creating table video [" + TABLE_VIDEO + "]");

            //DROP existing table video
            DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_VIDEO;

            try {
                db.execSQL(DROP_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "exception drop in table video");
            }
            //create video table
            CREATE_SQL = "CREATE TABLE " + TABLE_VIDEO + "("
                    + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "uri TEXT,"
                    + "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try {
                db.execSQL(CREATE_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "exception create table video", ex);
            }
            //create index video
            println("creating index in table video" );

            CREATE_INDEX_SQL = "CREATE INDEX " + TABLE_VIDEO + "_IDX ON " + TABLE_VIDEO + " ("
                    + "uri"
                    + ")";

            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex) {
                Log.e(TAG,"exception create index in table video");
            }
            //end video
            //start create voice table and index
            println("creating table [" + TABLE_VOICE + "]");

            //if existing table voice
            DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_VOICE;
            try {

                db.execSQL(DROP_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예외발생 drop voice table", ex);
            }
            //create table voice
            CREATE_SQL = "CREATE TABLE " + TABLE_VOICE + " ("
                    + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , "
                    + "uri TEXT , "
                    + "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";

            try {
                db.execSQL(CREATE_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예회발생 table voice", ex);
            }
            //create index voice
            println("creating index in table voice" );

            CREATE_INDEX_SQL = "CREATE INDEX " + TABLE_VOICE + "_IDX ON " + TABLE_VOICE + "("
                    + "uri"
                    + ")";
            try {
                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예외발생 creat index in table voice");
            }
            //end voice

            //start handwrithing table
            println("creating table [" + TABLE_HANDWRITHING + "]");
            //DROP EXISTING TABLE HANDWRITING
            DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_HANDWRITHING;

            try {
                db.execSQL(DROP_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예외발생 create handwriting table", ex);
            }
            //create table handwriting
            CREATE_SQL = "CREATE TABLE " + TABLE_HANDWRITHING + " ("
                    + "_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
                    + "uri TEXT ,"
                    + "create_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            try {

                db.execSQL(CREATE_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예외발생 create table handwriting");
            }
            //create indeX handwriting
            println("creating index in table handwriting" );

            CREATE_INDEX_SQL = "CREATE INDEX " + TABLE_HANDWRITHING + "_IDX ON " + TABLE_HANDWRITHING + "("
                    + "uri"
                    + ")";
            try {

                db.execSQL(CREATE_INDEX_SQL);
            } catch (Exception ex) {
                Log.e(TAG, "예외발생 CREATE INDEX in HANDWRITING TABLE", ex);
            }
            //end hand writhig

        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            println("opened database [" + BasicInfo.DATABASE_NAME + "]");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            println("upgrading database from version " + oldVersion + " to " + newVersion);

        }
    }

}
