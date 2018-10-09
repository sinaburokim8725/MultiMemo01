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
import android.view.MotionEvent;

import org.familly.multimemo.R;

public class TitleBitmapButton extends AppCompatButton {

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

    private boolean selected;

    private int tabId;


    public TitleBitmapButton(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public TitleBitmapButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setTabId(int tabId) {
        this.tabId = tabId;
    }

    @Override
    public void setSelected(boolean flag) {
        this.selected = flag;

        if(selected) {
            setBackgroundResource(backgroundBitmapClicked);
            paintChanged = true;
            defaultColor = Color.BLACK;
        } else {
            setBackgroundResource(backgroundBitmapNormal);
            paintChanged = true;
            defaultColor = Color.WHITE;
        }
    }
    public boolean isSelected() {
        return selected;
    }

    public void init() {

    }

    //set icon bitmap
    public void setIconBitmap(Bitmap iconNormal,Bitmap iconClicked){
        iconNormalBitmap = iconNormal;
        iconClickedBitmap = iconClicked;
     }

    public void setBackgroundBitmap(int resNomal,int resClicked){
        backgroundBitmapNormal = resNomal;
        backgroundBitmapClicked = resClicked;

        setBackgroundResource(backgroundBitmapNormal);
    }

    //Handle touch event,move to main screen

    @Override
    public boolean onTouchEvent(MotionEvent event) {
         super.onTouchEvent(event);

         int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (selected) {

                } else {
                    setBackgroundResource(backgroundBitmapNormal);
                    iconStatus = 0;
                    paintChanged = true;
                    defaultColor = Color.WHITE;
                }
                break;
            case MotionEvent.ACTION_DOWN:
                if (selected) {

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
         return true;
    }

    //Dtraw the text

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

        if (iconBitmap != null) {
            int iconWidth = iconBitmap.getWidth();
            int iconHeight = iconBitmap.getHeight();
            int bitmapX =0;

            switch (bitmapX) {
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

    }

    //getter setter 메소드

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
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
    }

    public float getDefaultScaleX() {
        return defaultScaleX;
    }

    public void setDefaultScaleX(float defaultScaleX) {
        this.defaultScaleX = defaultScaleX;
        paintChanged = true;
    }

    public Typeface getDefaultTypeface() {
        return defaultTypeface;
    }

    public void setDefaultTypeface(Typeface defaultTypeface) {
        this.defaultTypeface = defaultTypeface;
        paintChanged = true;
    }

    public int getBitmapAlign() {
        return bitmapAlign;
    }

    public void setBitmapAlign(int bitmapAlign) {
        this.bitmapAlign = bitmapAlign;
    }

    public int getBitmapPadding() {
        return bitmapPadding;
    }

    public void setBitmapPadding(int bitmapPadding) {
        this.bitmapPadding = bitmapPadding;
    }
}
