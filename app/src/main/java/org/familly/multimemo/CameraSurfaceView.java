package org.familly.multimemo;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * 카메라 미리보기를 위한 서피
 */
public class CameraSurfaceView extends SurfaceView implements Callback {

    public static final String LOG_TAG = "MultiMemo > "+CameraSurfaceView.class.getSimpleName();

    SurfaceHolder mSurfaceHolder;
    Camera mCamera;

    public CameraSurfaceView(Context context) {
        super(context);
        //SurfaceHolder 객체 획득
        mSurfaceHolder = getHolder();
        //SurfaceHolder 에 콜백 함수 등록
        mSurfaceHolder.addCallback(this);
        //타입설정 사용중지되었음 . 필요할때 자동으로 설정됨
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "CameraSurfaceView 객체생성");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceCreated Start");

        openCamera();

        Log.d(LOG_TAG, "surfaceCreated End");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(LOG_TAG, "surfaceChanged Start");

        mCamera.startPreview();

        Log.d(LOG_TAG, "surfaceChanged End");

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(LOG_TAG, "surfaceDestroyed Start");

        stopCamera();

        Log.d(LOG_TAG, "surfaceDestroyed End");

    }

    public void openCamera() {
        Log.d(LOG_TAG, "openCamera Start");

        mCamera = Camera.open();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error Camera preview display", e);
        }finally {
            Log.d(LOG_TAG, "openCamera End");
        }
    }

    public void stopCamera() {
        Log.d(LOG_TAG, "stopCamera Start");

        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        Log.d(LOG_TAG, "stopCamera End");

    }
    public Surface getSurface() {
        Log.d(LOG_TAG, "getSurface Start");

        Log.d(LOG_TAG, "getSurface End");

        return mSurfaceHolder.getSurface();
    }

    /**
     * takePicture(null, null, jpegHandler) 기능.
     *
     * 비동기 이미지 캡처를 트리거합니다.
     * 카메라 서비스는 이미지 캡처가 진행될 때 응용 프로그램에 대한 일련의 콜백을 시작합니다.
     *
     * 셔터 콜백은 이미지가 캡처 된 후에 발생합니다.
     * 이 기능을 사용하여 이미지가 캡처되었음을 사용자에게 알리는 사운드를 트리거 할 수 있습니다.
     *
     * raw 콜백은 raw 이미지 데이터를 사용할 수있을 때 발생합니다 (참고 : raw 이미지 콜백 버퍼를 사용할 수 없거나
     * raw 이미지 콜백 버퍼가 원시 이미지를 저장할 정도로 크지 않은 경우 데이터는 null입니다).
     *
     * 포스트 뷰 콜백은 크기가 조정되고 완전히 처리 된 포스트 뷰 이미지를 사용할 수있을 때 발생합니다
     * (참고 : 모든 하드웨어에서 지원하지는 않음).
     *
     * jpeg 콜백은 압축 된 이미지를 사용할 수있을 때 발생합니다.
     *
     * 응용 프로그램이 특정 콜백을 필요로하지 않으면 콜백 메소드 대신에 널 (null)을 전달할 수 있습니다.
     * 이 메서드는 미리보기가 활성화 된 경우에만 유효합니다 (startPreview () 후).
     *
     * !.미리보기는 이미지가 촬영 된 후에 중단됩니다. 미리보기를 다시 시작하거나 사진을 더 찍으려면
     * 호출자가 startPreview ()를 다시 호출해야합니다.
     *
     * 이것은 android.media.MediaRecorder.start ()와 android.media.MediaRecorder.stop () 사이에서
     * 호출하면 안됩니다.
     *
     * 이 메서드를 호출 한 후에는 JPEG 콜백이 반환 될 때까지
     * startPreview ()를 호출하거나 다른 그림을 가져 오면 안됩니다.
     *
     * @param jpegHandler
     * @return
     */
    public boolean capture(Camera.PictureCallback jpegHandler) {
        Log.d(LOG_TAG, "capture Start");


        if (mCamera != null) {
            mCamera.takePicture(null, null, jpegHandler);

            Log.d(LOG_TAG, "capture true End");

            return true;
        } else {

            Log.d(LOG_TAG, "capture false End");

            return false;
        }
    }

    public void stopPreview() {
        Log.d(LOG_TAG, "stopPreview Start");


        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;

        Log.d(LOG_TAG, "stopPreview End");

    }

    public void startPreview() {
        Log.d(LOG_TAG, "startPreview Start");

        openCamera();
        mCamera.startPreview();

        Log.d(LOG_TAG, "startPreview End");

    }
}
