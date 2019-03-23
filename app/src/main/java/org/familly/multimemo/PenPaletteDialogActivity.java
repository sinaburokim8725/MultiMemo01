package org.familly.multimemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.familly.multimemo.common.TitleBitmapButton;

public class PenPaletteDialogActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MultiMemo > "+PenPaletteDialogActivity.class.getSimpleName();


    GridView mGridView;
    TitleBitmapButton mCloseBtn;
    PenDataAdapter mAdapter;

    public static OnPenSelectedListener mSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pen_palette_dialog);
        Log.d(LOG_TAG, "onCreate Start");

        this.setTitle(R.string.pen_selection_title);
        mGridView = (GridView) findViewById(R.id.grid_pen);
        mCloseBtn = (TitleBitmapButton) findViewById(R.id.button_close);

        mGridView.setColumnWidth(12);
        mGridView.setBackgroundColor(Color.GRAY);
        mGridView.setVerticalSpacing(4);
        mGridView.setHorizontalSpacing(4);

        mAdapter = new PenDataAdapter(this);
        mGridView.setAdapter(mAdapter);
        mGridView.setNumColumns(mAdapter.getNumColumns());

        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d(LOG_TAG, "onCreate End");

    }
}

/**
 * 그리드뷰에 보여질 펜팔렛트의 데이터를 생성하고 붙이는 역할을한다.
 */
 class PenDataAdapter extends BaseAdapter {
    private static final String LOG_TAG = "MultiMemo > "+PenDataAdapter.class.getSimpleName();


    Context mContext;

    //펜정의
    public static final int[] pens = new int[]{
            1,2,3,4,5,
            6,7,8,9,10,
            11,13,15,17,20
    };
    int rowCount;
    int columnCount;

    public PenDataAdapter(Context context) {
        super();

        Log.d(LOG_TAG, "PenDataAdapter 생성자를 통한 초기화");

        mContext = context;
        rowCount = 3;
        columnCount = 5;
    }

    //사용자정의 메소드 start
    public int getNumColumns() {
        Log.d(LOG_TAG, "getNumColumns Start");

        Log.d(LOG_TAG, "getNumColumns End");

        return columnCount;
    }

    //사용자정의 메소드 end
    //오버라딩 메소드 start
    @Override
    public int getCount() {
        Log.d(LOG_TAG, "getCount Start");

        Log.d(LOG_TAG, "rowCount * columnCount >" + rowCount * columnCount);

        Log.d(LOG_TAG, "getCount End");

        return rowCount * columnCount;
    }

    @Override
    public Object getItem(int position) {
        Log.d(LOG_TAG, "getItem Start");

        Log.d(LOG_TAG, "Object position > " + position);

        Log.d(LOG_TAG, "getItem End");

        return pens[position];
    }

    @Override
    public long getItemId(int position) {
        Log.d(LOG_TAG, "getItemId Start");

        Log.d(LOG_TAG, "getItemId End");
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        Log.d(LOG_TAG, "getView(" + position + ")  Start");

        //
        int rowIndex = position / columnCount;
        int columnIdex = position % columnCount;
        Log.d(LOG_TAG, "PenDataAdapter Index( " + rowIndex + " , " + columnCount + " )");

        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT
        );
        //그림그릴펜의 굵기를 생성한다. 버튼으로
        int penWidth = 10;
        int penHeight = 20;

        Bitmap penBitmap = Bitmap.createBitmap(penWidth, penHeight, Bitmap.Config.ARGB_8888);
        Canvas penCanvas = new Canvas();
        penCanvas.setBitmap(penBitmap);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        penCanvas.drawRect(0, 0, penWidth, penHeight, mPaint);

        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth((float) pens[position]);
        penCanvas.drawLine(0, penHeight / 2, penWidth - 1, penHeight / 2, mPaint);
        BitmapDrawable penDrawable = new BitmapDrawable(mContext.getResources(), penBitmap);

        //create a button with color
        TitleBitmapButton aItem = new TitleBitmapButton(mContext);
        aItem.setText(" ");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4);
        aItem.setBackgroundDrawable(penDrawable);
        aItem.setHeight(120);
        aItem.setTag(pens[position]);

        //set listener
        aItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PenPaletteDialogActivity.mSelectedListener != null) {
                    PenPaletteDialogActivity.mSelectedListener.onPenSelected(((Integer) v.getTag()).intValue());
                }
                ((PenPaletteDialogActivity)mContext).finish();
            }
        });
        Log.d(LOG_TAG, "getView() End");

        return aItem;
    }
    //오버라이딩 메소드 end
}