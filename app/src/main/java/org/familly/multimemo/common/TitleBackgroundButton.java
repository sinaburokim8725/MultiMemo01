package org.familly.multimemo.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import org.familly.multimemo.R;

public class TitleBackgroundButton extends AppCompatButton {

    private static final String TAG = "DEBUG";

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

        this.context =context;
        init();
    }

    public TitleBackgroundButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }
    /**초기화
     * 1.리소스폴더에서 백그라운 이미지 설정
     * 2.
     */
    public void init(){
        setBackgroundResource(R.drawable.title_background);

        paint = new Paint();
        paint.setColor(defaultColor);
        setText("멀티메모 초기버전");
        titleText = getText();
        //Log.d(TAG , "titleText : " + titleText);

        //앤티 앨리어싱은 그려지는 부분의 가장자리를 부드럽게 하는 기능:true false
        paint.setAntiAlias(true);

        //
        paint.setTextScaleX(defaultScaleX);

        //
        paint.setTextSize(defaultSize);

        //서체 설정
        paint.setTypeface(defaultTypeface);

    }
    /**
     * Handle touch event,move to main screen
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

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

        return true;
    }
    //

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
        float tWidth = ((float)vWidth - rBounds.width())/2.0F;
        float tHeight = ((float)vHeight -rBounds.height())/2.0F + rBounds.height();

        //draw titile text
        canvas.drawText(titleText,tWidth,tHeight,paint);


    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
        paintChanged = true;
    }

    public float getDefaultSize() {
        return defaultSize;
    }

    public void setDefaultSize(float defaultSize) {
        this.defaultSize = defaultSize;
        paintChanged =true;
    }

    public float getDefaultScaleX() {
        return defaultScaleX;
    }

    public void setDefaultScaleX(float defaultScaleX) {
        this.defaultScaleX = defaultScaleX;
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }

    public void setDefaultTypeface(Typeface defaultTypeface) {
        this.defaultTypeface = defaultTypeface;
        paintChanged = true;
    }
    public String getText(){
        return titleText;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }
}
