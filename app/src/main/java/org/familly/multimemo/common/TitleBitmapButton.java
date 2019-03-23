package org.familly.multimemo.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import org.familly.multimemo.R;

public class TitleBitmapButton extends AppCompatButton {

    public static final String LOG_TAG = "MultiMemo > "+ TitleBitmapButton.class.getSimpleName();

    //base context
    Context context;

    //paint instance
    Paint paint;

    //default color
    int defaultColor = 0xffffffff;

    //default size
    float defaultSize = 18F;

    //default scalex
    float defaultScaleX =1.0F;

    //Default typeface
    Typeface defaultTypeface = Typeface.DEFAULT;

    //title text
    String titleText = "";

    //icon status : 0 - nomal, 1 - clicked
    int iconStatus = 0;

    //icon clicked bitmap
    Bitmap iconNormalBitmap;

    //icon clicked bitmap
    Bitmap iconClickedBitmap;

    //정렬
    public static final int BITMAP_ALIGN_CENTER = 0;
    public static final int BITMAP_ALIGN_LEFT   = 1;
    public static final int BITMAP_ALIGN_RIGHT  = 2;

    int backgroundBitmapNormal = R.drawable.title_button;
    int backgroundBitmapClicked = R.drawable.title_button_clicked;

    //alignment
    int bitmapAlign = BITMAP_ALIGN_CENTER;

    //padding for left or right
    int bitmapPadding = 10;

    //flag for paint changed
    boolean paintChanged = false;

    private boolean isSelected;

    private int tabId;


    public TitleBitmapButton(Context context) {
        super(context);
        Log.d(LOG_TAG, "TitleBitmapButton 생성");


        this.context = context;
        init();
    }

    public TitleBitmapButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(LOG_TAG, "TitleBitmapButton(Context context, AttributeSet attrs) 생성");

        this.context = context;
        init();
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    @Override
    public void setSelected(boolean flag) {
        Log.d(LOG_TAG, "setSelected Start");

        this.isSelected = flag;

        if(isSelected) {
            setBackgroundResource(backgroundBitmapClicked);
            paintChanged = true;
            defaultColor = Color.BLACK;
        } else {
            setBackgroundResource(backgroundBitmapNormal);
            paintChanged = true;
            defaultColor = Color.WHITE;
        }

        Log.d(LOG_TAG, "setSelected End");

    }
    public boolean isSelected() {
        Log.d(LOG_TAG, "isSelected Start");


        Log.d(LOG_TAG, "isSelected End");

        return isSelected;
    }
    //초기화
    public void init() {
        Log.d(LOG_TAG, "init Start");

        setBackgroundResource(backgroundBitmapNormal);

        paint = new Paint();
        paint.setColor(defaultColor);
        paint.setAntiAlias(true);
        paint.setTextScaleX(defaultScaleX);
        paint.setTextSize(defaultSize);
        paint.setTypeface(defaultTypeface);

        isSelected = false;

        Log.d(LOG_TAG, "init End");

    }

    //set icon bitmap
    public void setIconBitmap(Bitmap iconNormal,Bitmap iconClicked){
        Log.d(LOG_TAG, "setIconBitmap Start");


        iconNormalBitmap = iconNormal;
        iconClickedBitmap = iconClicked;

        Log.d(LOG_TAG, "setIconBitmap End");

     }

    public void setBackgroundBitmap(int resNomal,int resClicked){
        Log.d(LOG_TAG, "setBackgroundBitmap Start");

        backgroundBitmapNormal = resNomal;
        backgroundBitmapClicked = resClicked;

        setBackgroundResource(backgroundBitmapNormal);

        Log.d(LOG_TAG, "setBackgroundBitmap End");

    }

    //Handle touch event,move to main screen

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         super.onTouchEvent(event);
        Log.d(LOG_TAG, "onTouchEvent Start");

         int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (isSelected) {

                } else {
                    setBackgroundResource(backgroundBitmapNormal);
                    iconStatus = 0;
                    paintChanged = true;
                    defaultColor = Color.WHITE;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (isSelected) {

                } else {
                    setBackgroundResource(backgroundBitmapClicked);
                    iconStatus = 1;
                    paintChanged = true;
                    defaultColor = Color.BLACK;
                }
                break;
            default:
         }
         //repaint the screen
         invalidate();

        Log.d(LOG_TAG, "onTouchEvent End");

         return true;
    }

    //Dtraw the text

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.d(LOG_TAG, "onDraw Start");


        int cWidth = getWidth();
        int cHeight = getHeight();

        //apply paint attributes
        if (paintChanged) {
            paint.setColor(defaultColor);
            paint.setTextScaleX(defaultScaleX);
            paint.setTextSize(defaultSize);
            paint.setTypeface(defaultTypeface);

            paintChanged = false;
        }

        //bitmap
        Bitmap iconBitmap = iconNormalBitmap;
        if (iconStatus == 1) {
            iconBitmap = iconClickedBitmap;
        }

        if (iconBitmap != null) {
            int iconWidth = iconBitmap.getWidth();
            int iconHeight = iconBitmap.getHeight();
            int bitmapX =0;

            switch (bitmapAlign) {
                case BITMAP_ALIGN_CENTER:
                    bitmapX = (cWidth - iconWidth)/2;
                    break;
                case BITMAP_ALIGN_LEFT:
                    bitmapX = bitmapPadding;
                    break;
                case BITMAP_ALIGN_RIGHT:
                    bitmapX = cWidth - bitmapPadding;
                    break;
                default:
            }
            canvas.drawBitmap(iconBitmap,bitmapX,(cHeight - iconHeight)/2,paint);

        }
        //text 설정
        Rect rBounds = new Rect();
        paint.getTextBounds(titleText,0, titleText.length(),rBounds);
        float textWidth = ((float)cWidth - rBounds.width())/2.0F;
        float textHeight =((float)cHeight + rBounds.height())/2.0F + 4.0F;

        //캔버스에 그린다.
        canvas.drawText(titleText,textWidth,textHeight,paint);

        Log.d(LOG_TAG, "onDraw End");

    }

    //getter setter 메소드

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

    public int getDefaultColor() {
        Log.d(LOG_TAG, "getDefaultColor Start");


        Log.d(LOG_TAG, "getDefaultColor End");

        return defaultColor;
    }

    public void setDefaultColor(int defaultColor) {
        Log.d(LOG_TAG, "setDefaultColor Start");

        this.defaultColor = defaultColor;
        paintChanged = true;

        Log.d(LOG_TAG, "setDefaultColor Edn");

    }

    public float getDefaultSize() {
        Log.d(LOG_TAG, "getDefaultSize Start");


        Log.d(LOG_TAG, "getDefaultSize End");

        return defaultSize;
    }

    public void setDefaultSize(float defaultSize) {
        Log.d(LOG_TAG, "setDefaultSize Start");

        this.defaultSize = defaultSize;

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
        paintChanged = true;

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

    public int getBitmapAlign() {
        Log.d(LOG_TAG, "getBitmapAlign Start");


        Log.d(LOG_TAG, "getBitmapAlign End");

        return bitmapAlign;
    }

    public void setBitmapAlign(int bitmapAlign) {
        Log.d(LOG_TAG, "setBitmapAlign Start");

        this.bitmapAlign = bitmapAlign;

        Log.d(LOG_TAG, "setBitmapAlign End");

    }

    public int getBitmapPadding() {
        Log.d(LOG_TAG, "getBitmapPadding Start");


        Log.d(LOG_TAG, "getBitmapPadding End");

        return bitmapPadding;
    }

    public void setBitmapPadding(int bitmapPadding) {
        Log.d(LOG_TAG, "setBitmapPadding Start");

        this.bitmapPadding = bitmapPadding;

        Log.d(LOG_TAG, "setBitmapPadding End");

    }
}
