package org.familly.multimemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.OutputStream;
import java.util.Stack;

public class HandwritingView extends View {
    private static final String LOG_TAG = "MultiMemo > "+MultiMemoMainActivity.class.getSimpleName();

    Bitmap bitmap;
    Canvas canvas;
    Stack<Bitmap> undos = new Stack<Bitmap>();
    final Paint paint;

    private static final boolean RENDERING_ANTIALIAS = true;
    private final boolean DITHER_FLAG = true;
    private float lastY;
    private float lastX;

    private float strokeWidth = 8.0f;
    private int color = 0xff000000;

    public static int maxUndo = 10;
    public boolean changed = false;
    private final Path path = new Path();

    static final float TOUCH_TOLERANCE = 1;
    private int invalidateExtraBorder = 10;
    private float curveEndX;
    private float curveEndY;


    public HandwritingView(Context context) {
        super(context);
        Log.d(LOG_TAG, "HandwritingView 생성");

        //create a new paint object
        paint = new Paint();
        paint.setAntiAlias(RENDERING_ANTIALIAS);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setDither(DITHER_FLAG);

        lastX = -1;
        lastY = -1;

    }

    public HandwritingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "HandwritingView(Context context, AttributeSet attrs) 생성");

        //create a new paint object
        paint = new Paint();
        paint.setAntiAlias(RENDERING_ANTIALIAS);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setDither(DITHER_FLAG);

        lastX = -1;
        lastY = -1;

     }

    /**
     *
     */
    public void clearUndo() {
        Log.d(LOG_TAG, "clearUndo Start");

        while (true) {
            Bitmap prev = undos.pop();
            if (prev == null) {
                Log.d(LOG_TAG, "clearUndo End");
                return;
            }
            //비트맵을 더이상 사용하지 않을경우 호출해야 한다.
            prev.recycle();
        }

    }

    /**
     *
     */
    public void saveUndo() {
        Log.d(LOG_TAG, "saveUndo Start");


        if (bitmap == null) {
            Log.d(LOG_TAG, "saveUndo End");

            return;
        }
        while (undos.size() >= maxUndo) {
            Bitmap i = (Bitmap) undos.get(undos.size() - 1);
            i.recycle();
            undos.remove(i);
        }
        Bitmap img = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(img);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        undos.push(img);

        Log.d(LOG_TAG, "saveUndo End");

    }

    /**
     *
     */
    public void undo() {
        Log.d(LOG_TAG, "undo Start");

        Bitmap prev = null;
        if (!undos.isEmpty()) {
            prev = undos.pop();
        }
        if (prev != null) {
            drawBackground(canvas);
            canvas.drawBitmap(prev, 0, 0, paint);
            invalidate();
            prev.recycle();
        }
        Log.d(LOG_TAG, "undo End");
    }

    private void drawBackground(Canvas canvas) {
        Log.d(LOG_TAG, "drawBackground Start");

        if (canvas != null) {
            canvas.drawColor(Color.argb(255, 255, 255, 255));
        }

        Log.d(LOG_TAG, "drawBackground End");

    }

    /**
     * 변경한 색상 선굵기 업데이트
     */
    public void updatePaintProperty(int color, int strokeSize) {
        Log.d(LOG_TAG, "updatePaintProperty Start");

        paint.setColor(color);
        paint.setStrokeWidth(strokeSize);

        Log.d(LOG_TAG, "updatePaintProperty End");

    }

    /**
     * Create a new image
     */
    public void newImage(int width, int height) {
        Log.d(LOG_TAG, "newImage Start");

        Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas lCanvas = new Canvas();
        lCanvas.setBitmap(img);
        bitmap = img;
        canvas = lCanvas;

        drawBackground(canvas);
        changed = false;
        invalidate();

        Log.d(LOG_TAG, "newImage End");

    }

    /**
     *
     * @return
     */
    public Bitmap getImage() {
        Log.d(LOG_TAG, "getImage Start");

        Log.d(LOG_TAG, "getImage End");

        return bitmap;
    }

    /**
     *
     */
    public void setImage(Bitmap newImage) {
        Log.d(LOG_TAG, "setImage Start");

        changed = false;

        setImageSize(newImage.getWidth(), newImage.getHeight(), newImage);
        invalidate();

        Log.d(LOG_TAG, "setImage End");

    }

    private void setImageSize(int width, int height, Bitmap newImage) {

        Log.d(LOG_TAG, "setImageSize Start");


        if (bitmap != null) {
            if (width < bitmap.getWidth()) {
                width = bitmap.getWidth();
            }
            if (height < bitmap.getHeight()) {
                height = bitmap.getHeight();
            }
            if (width < 1 || height < 1) {
                Log.d(LOG_TAG, "setImageSize 폭,높이 1보다 작음. End");

                return;
            }
            Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas lCanvas = new Canvas();
            drawBackground(lCanvas);

            if (newImage != null) {
                canvas.setBitmap(newImage);
            }

            if (bitmap != null) {
                bitmap.recycle();
                canvas.restore();
            }
            bitmap = img;
            canvas = lCanvas;

            clearUndo();
        }
        Log.d(LOG_TAG, "setImageSize End");

    }

    /**
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(LOG_TAG, "onSizeChanged Start");

        if (w > 0 && h > 0) {
            newImage(w, h);
        }

        Log.d(LOG_TAG, "onSizeChanged End");

    }

    /**
     * Draw the Bitmap
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(LOG_TAG, "onDraw Start");

        if (bitmap != null) {
            canvas.drawBitmap(bitmap,0,0,null);
        }

        Log.d(LOG_TAG, "onDraw End");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, "onTouchEvent Start");

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                //KIM
                Log.d(LOG_TAG, "ACTION UP 호출");
                changed = true;

                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate();
                }
                path.rewind();

                return true;

            case MotionEvent.ACTION_DOWN:
                Log.d(LOG_TAG, "ACTION_DOWN 호출");
                saveUndo();

                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                Log.d(LOG_TAG, "ACTION_MOVE");
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;

        }

        return false;
    }

    private Rect touchMove(MotionEvent event) {
        Log.d(LOG_TAG, "touchMove Start");

        Rect rect = processMove(event);

        Log.d(LOG_TAG, "touchMove End");

        return rect;
    }

    private Rect touchDown(MotionEvent event) {
        Log.d(LOG_TAG, "touchDown Start");

        float x = event.getX();
        float y = event.getY();

        lastX = x;
        lastY = y;

        Rect invalidRect = new Rect();
        path.moveTo(x, y);

        final int border = invalidateExtraBorder;
        invalidRect.set((int) x - border, (int) y - border,
                (int) x + border, (int) y + border);

        curveEndX = x;
        curveEndY = y;
        canvas.drawPath(path, paint);

        Log.d(LOG_TAG, "touchDown End");

        return invalidRect;
    }

    private Rect touchUp(MotionEvent event, boolean cancel) {
        Log.d(LOG_TAG, "touchUp Start");


        Rect rect = processMove(event);

        Log.d(LOG_TAG, "touchUp End");

        return rect;
    }

    private Rect processMove(MotionEvent event) {
        Log.d(LOG_TAG, "processMove Start");

        final float x = event.getX();
        final float y = event.getY();

        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);

        Rect invalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = invalidateExtraBorder;
            invalidRect.set((int) curveEndX - border, (int) curveEndY - border,
                    (int) curveEndX + border, (int) curveEndY + border);

            float cX = curveEndX = (x + lastX) / 2;
            float cY = curveEndY = (y + lastY) / 2;
            /**
             * Add a quadratic bezier from the last point, approaching control point (x1,y1), and ending at (x2,y2).
             * If no moveTo() call has been made for this contour, the first point is automatically set to (0,0).
             * 마지막 점에서 제어점 (x1, y1)에 접근하고 (x2, y2)로 끝나는 2 차 베 지어를 추가합니다.
             * 이 윤곽에 대해 moveTo () 호출이 없으면 첫 번째 점이 자동으로 (0,0)으로 설정됩니다.
             */
            path.quadTo(lastX, lastY, cX, cY);

            /**
             * Update this Rect to enclose itself and the specified rectangle.
             * If the specified rectangle is empty, nothing is done.
             * If this rectangle is empty it is set to the specified rectangle.
             * 이 사각형을 업데이트하여 자신과 지정된 사각형을 묶습니다.
             * 지정된 사각형이 비어 있으면 아무 것도 수행되지 않습니다.
             * 이 사각형이 비어 있으면 지정된 사각형으로 설정됩니다.
             */
            invalidRect.union((int) lastX - border, (int) lastY - border,
                    (int) cX + border, (int) cY + border);

            invalidRect.union((int) cX - border, (int) cY - border,
                    (int) cX + border, (int) cY + border);

            lastX = x;
            lastY = y;

            canvas.drawPath(path, paint);
        }
        Log.d(LOG_TAG, "processMove End");

        return invalidRect;
    }

    /**
     *
     */
    public boolean save(OutputStream outputStream) {
        Log.d(LOG_TAG, "save Start");

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        invalidate();

        Log.d(LOG_TAG, "save End");

        return true;
    }
}
