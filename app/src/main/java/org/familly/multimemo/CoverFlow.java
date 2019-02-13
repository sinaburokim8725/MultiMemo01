package org.familly.multimemo;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;

public class CoverFlow extends Gallery {
    private static final String TAG = "DEBUG";

    private static int maxRotationAngle = 55;
    private static int maxZoom = -60;
    private int centerPoint;
    private Camera camera = new Camera();

    public CoverFlow(Context context) {
        super(context);
        init();
    }

    public CoverFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //When this property is set to true, this ViewGroup supports static transformations on children; this causes getChildStaticTransformation(View, Transformation) to be invoked when a child is drawn. Any subclass overriding getChildStaticTransformation(View, Transformation) should set this property to true.
    //이 속성을 true로 설정하면이 ViewGroup은 자식에 대한 정적 변환을 지원합니다.
    // 이로 인해 자식을 그릴 때 getChildStaticTransformation (View, Transformation)이 호출됩니다.
    // getChildStaticTransformation (View, Transformation)을 오버라이드 (override)하는 서브 클래스에서는,
    // 이 property를 true로 설정할 필요가 있습니다.
    private void init() {
        this.setStaticTransformationsEnabled(true);
    }

    public int getMaxRotationAngle() {
        return maxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
        this.maxRotationAngle = maxRotationAngle;
    }

    public int getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(int maxZoom) {
        this.maxZoom = maxZoom;
    }

    private int getCenterOfCoverflow() {
        Log.d(TAG, "width : " + getWidth() + ", left padding : " + getPaddingLeft() + ", right padding : " + getPaddingRight());

        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(View view) {
        Log.d(TAG, "view left : " + view.getLeft() + " , " + "view width : " + view.getWidth());
        return view.getLeft() + view.getWidth()/2;
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation t) {
        Log.d(TAG, "CoverFlow class getChildStaticTransformation() 호출됨 ");
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;
        //
        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);
        if (childCenter == centerPoint) {
            transformImageBitmap((ImageView) child, t, 0);
        } else {
            rotationAngle = (int) (((float)(centerPoint -childCenter)/childWidth) * maxRotationAngle);
            if (Math.abs(rotationAngle) > maxRotationAngle) {
                rotationAngle = (rotationAngle < 0)?-maxRotationAngle : maxRotationAngle;
            }
            transformImageBitmap((ImageView) child, t, rotationAngle);
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "CenterFlow class onSizechanged() 호출 (현재너비,현재높이,이전너비,이전높이)"
                + "( " + w + " , " + h + " , " + oldw + " , " + oldh + " )");

        centerPoint = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void transformImageBitmap(ImageView child, Transformation t, int rotationAngle) {
        camera.save();

        //The 3x3 Matrix representing the trnasformation to apply to the coordinates of the object being animated
        //애니메이션되는 객체의 좌표에 적용 할 trnasformation을 나타내는 3x3 행렬입니다.
        final Matrix imageMatrix = t.getMatrix();
        final int imageHeight = child.getLayoutParams().height;
        final int imageWidth = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        camera.translate(0.0F, 0.0F, 100.0F);

        if (rotation < maxRotationAngle) {
            float zoomAmount = (float) (maxZoom + (rotation * 1.5F));
            camera.translate(0.0F, 0.0F, zoomAmount);
        }
        camera.rotateY(rotationAngle);
        camera.getMatrix(imageMatrix);

        imageMatrix.preTranslate(-(imageWidth / 2), -(imageHeight / 2));
        imageMatrix.postTranslate((imageWidth / 2), (imageHeight / 2));

        camera.restore();
    }
}
