package org.familly.multimemo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.FileOutputStream;

public class PhotoCaptureActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MultiMemo > "+PhotoCaptureActivity.class.getSimpleName();


    CameraSurfaceView gCameraSurfaceView;
    FrameLayout gFramLayout;
    //버튼 연속 클릭 문제해결
    boolean processing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate Start");

        //sinaburokim 상태바와 타이틀 결정
        final Window win = getWindow();
        win.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_photo_capture);

        gCameraSurfaceView = new CameraSurfaceView(getApplicationContext());
        gFramLayout = (FrameLayout) findViewById(R.id.layout_frame);
        gFramLayout.addView(gCameraSurfaceView);

        setCaptureBtn();

        Log.d(LOG_TAG, "onCreate End");
     }

    public void setCaptureBtn() {
        Log.d(LOG_TAG, "setCaptureBtn Start");

        TitleBitmapButton takePictureBtn = (TitleBitmapButton) findViewById(R.id.button_capture);

        takePictureBtn.setBackgroundBitmap(R.drawable.btn_camera_capture_normal, R.drawable.btn_camera_capture_click);

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!processing) {
                    Log.d(LOG_TAG, "사진촬영버튼눌렸음.");
                    processing = true;
                    gCameraSurfaceView.capture(new CameraPictureCallback());
                }
            }
        });
        Log.d(LOG_TAG, "setCaptureBtn End");

    }

    /**
     * 설치된 카메라어플 사용할경우?
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(LOG_TAG, "onKeyDown() 호출됨");
        if (keyCode == KeyEvent.KEYCODE_CAMERA) {
            Log.d(LOG_TAG, "onKeyDown() KEYCODE_CAMERA");

            gCameraSurfaceView.capture(new CameraPictureCallback());

            return true;

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(LOG_TAG, "onKeyDown() KEYCODE_BACK");

            finish();

            return true;
        }

        return false;
    }

    //부모액티비티로 돌아가기
    public void showParentActivity() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);

        finish();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Log.d(LOG_TAG, "PhotoCaptureActivity onCreateDialog() 호출");

        switch (id) {
            case BasicInfo.IMAGE_CANNOT_STORED:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("사진을 저장할 수 없습니다. SD카드 상태를 확인하세요.");
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                return builder.create();
        }
        return null;
    }

    /**
     * 기능:
     */
    private class CameraPictureCallback implements Camera.PictureCallback {
        private  final String LOG_TAG = "MultiMemo > "+CameraPictureCallback.class.getSimpleName();

        @Override
        public void onPictureTaken(byte[] imageByteArray, Camera camera) {
            Log.d(LOG_TAG, "onPictureTaken Start");

            int bitmapWidth = 480;
            int bitmapHeight = 360;

            Bitmap capturedBitmap = BitmapFactory.decodeByteArray(imageByteArray,
                    0,
                    imageByteArray.length);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(capturedBitmap,
                    bitmapWidth,
                    bitmapHeight,
                    false);

            Bitmap resultBitmap = null;

            //sinaburokim 여기서 회전이나 크기를 정하지 말고 캡쳐된 사진을 불러와서 수정할수 있는 부분 만들면 좋겠다.
            Matrix matrix = new Matrix();
            matrix.postRotate(0);

            resultBitmap = Bitmap.createBitmap(scaledBitmap,
                    0,
                    0,
                    bitmapWidth,
                    bitmapHeight,
                    matrix,
                    false);

            File photoFolder = new File(BasicInfo.FOLDER_PHOTO);
            //폴더없을경우 폴더생성
            if (!photoFolder.isDirectory()) {
                Log.d(LOG_TAG, "폴더생성 : " + photoFolder);
                photoFolder.mkdirs();
            }
            String photoName = "captured";

            //기존이미지 있을경우 삭제
            File file = new File(BasicInfo.FOLDER_PHOTO + photoName);
            if (file.exists()) {
                file.delete();
            }
            //캡쳐된 이미지 png 파일로 생성한다. >> captured.png
            try {
                FileOutputStream outStream = new FileOutputStream(BasicInfo.FOLDER_PHOTO + photoName);
                resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                outStream.close();
            } catch (Exception ex) {
                Log.e(LOG_TAG, "캡쳐된 이미지 파일생성시 예외발생", ex);
                showDialog(BasicInfo.IMAGE_CANNOT_STORED);

            }
            showParentActivity();

            Log.d(LOG_TAG, "onPictureTaken End");

        }
    }
}
