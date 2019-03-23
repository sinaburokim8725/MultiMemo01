package org.familly.multimemo.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.familly.multimemo.R;

public class TitleBackgroundButton extends AppCompatButton {

    public static final String LOG_TAG = "MultiMemo > "+ TitleBackgroundButton.class.getSimpleName();

    //base context
    Context context;

    //paint instance
    Paint paint;

    //default color
    int defaultColor = 0xff333333;

    //default size
    float defaultSize = 20F;

    //default scaleX
    float defaultScaleX = 1.0F;

    //default typeface
    Typeface defaultTypeface = Typeface.DEFAULT_BOLD;

    //title text
    String titleText = "";

    //flag for paint changed
    boolean paintChanged = false;

    public TitleBackgroundButton(Context context) {
        super(context);
        Log.d(LOG_TAG, "TitleBackgroundButton(Context context) 생성");

        this.context =context;
        init();
    }

    public TitleBackgroundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "TitleBackgroundButton(Context context, AttributeSet attrs) 생성");

        this.context = context;
        init();
    }
    /**초기화
     * 1.리소스폴더에서 백그라운 이미지 설정
     * 2.
     */
    public void init(){
        Log.d(LOG_TAG, "init Start");

        setBackgroundResource(R.drawable.title_background);

        paint = new Paint();
        paint.setColor(defaultColor);

        //앤티 앨리어싱은 그려지는 부분의 가장자리를 부드럽게 하는 기능:true false
        paint.setAntiAlias(true);

        //
        paint.setTextScaleX(defaultScaleX);

        //
        paint.setTextSize(defaultSize);

        //서체 설정
        paint.setTypeface(defaultTypeface);

        Log.d(LOG_TAG, "init End");

    }
    /**
     * Handle touch event,move to main screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Log.d(LOG_TAG, "onTouchEvent Start");

        //
        int action = event.getAction();

        //
        switch (action) {
            case MotionEvent.ACTION_UP:
                break;

            case MotionEvent.ACTION_DOWN:
                Toast.makeText(context,
                        titleText,
                        Toast.LENGTH_LONG).show();
                break;
            default:
        }
        //repaint the screen
        // 전체뷰가 무효화된다.
        //뷰가 표시되면 미래의 어느 시점에서
        // onDraw (android.graphics.Canvas)가 호출됩니다.
        invalidate();

        Log.d(LOG_TAG, "onTouchEvent End");

        return true;
    }
    //

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(LOG_TAG, "onDraw Start");


        int vWidth  = getWidth();
        int vHeight = getHeight();

        //apply paint attributes
        if(paintChanged) {
            paint.setColor(defaultColor);
            paint.setTextScaleX(defaultScaleX);
            paint.setTextSize(defaultSize);
            paint.setTypeface(defaultTypeface);
        }

        //사각형 둘레계산
        Rect rBounds = new Rect();
        paint.getTextBounds(titleText,0, titleText.length(),rBounds);
        float tWidth  = ((float) vWidth - rBounds.width()) / 2.0F;
        float tHeight = ((float) vHeight - rBounds.height()) / 2.0F + rBounds.height();

        //draw titile text
        canvas.drawText(titleText,tWidth,tHeight,paint);

        Log.d(LOG_TAG, "onDraw End");

    }

    public int getDefaultColor() {
        Log.d(LOG_TAG, "getDefaultColor Start");

        Log.d(LOG_TAG, "getDefaultColor End");

        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        Log.d(LOG_TAG, "setDefaultColor Start");

        this.defaultColor = defaultColor;
        paintChanged = true;

        Log.d(LOG_TAG, "setDefaultColor End");

    }

    public float getDefaultSize() {
        Log.d(LOG_TAG, "getDefaultSize Start");


        Log.d(LOG_TAG, "getDefaultSize End");

        return defaultSize;
    }

    public void setDefaultSize(float defaultSize) {
        Log.d(LOG_TAG, "setDefaultSize Start");

        this.defaultSize = defaultSize;
        paintChanged =true;

        Log.d(LOG_TAG, "setDefaultSize End");

    }

    public float getDefaultScaleX() {
        Log.d(LOG_TAG, "getDefaultScaleX Start");


        Log.d(LOG_TAG, "getDefaultScaleX End");

        return defaultScaleX;
    }

    public void setDefaultScaleX(float defaultScaleX) {
        Log.d(LOG_TAG, "setDefaultScaleX Start");

        this.defaultScaleX = defaultScaleX;

        Log.d(LOG_TAG, "setDefaultScaleX End");

    }

    public Typeface getDefaultTypeface() {
        Log.d(LOG_TAG, "getDefaultTypeface Start");


        Log.d(LOG_TAG, "getDefaultTypeface End");

        return defaultTypeface;
    }

    public void setDefaultTypeface(Typeface defaultTypeface) {
        Log.d(LOG_TAG, "setDefaultTypeface Start");

        this.defaultTypeface = defaultTypeface;
        paintChanged = true;

        Log.d(LOG_TAG, "setDefaultTypeface End");

    }

    public String getTitleText() {
        Log.d(LOG_TAG, "getTitleText Start");

        Log.d(LOG_TAG, "getTitleText End");

        return titleText;
    }

    public void setTitleText(String titleText) {
        Log.d(LOG_TAG, "setTitleText Start");

        this.titleText = titleText;

        Log.d(LOG_TAG, "setTitleText End");

    }
}
