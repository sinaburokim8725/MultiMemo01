package org.familly.multimemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.familly.multimemo.common.TitleBitmapButton;

/**
 * 색상선택 대화상자
 */
public class ColorPaletteDialogActivity extends AppCompatActivity {
    private static final String TAG = "DEBUG";
    public static OnColorSelectedListener gSelectedListener;

    GridView          gColorGV;
    TitleBitmapButton gCloseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_palette_dialog);

        this.setTitle(R.string.color_selection_title);

        gColorGV = (GridView) findViewById(R.id.grid_color);
        gCloseBtn = (TitleBitmapButton) findViewById(R.id.button_close);

        gColorGV.setColumnWidth(12);
        gColorGV.setBackgroundColor(Color.GRAY);
        gColorGV.setVerticalSpacing(4);
        gColorGV.setHorizontalSpacing(4);

        ColorDataAdapter gAdapter = new ColorDataAdapter(this);
        gColorGV.setAdapter(gAdapter);
        gColorGV.setNumColumns(gAdapter.getNumColumns());

        gCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}

class ColorDataAdapter extends BaseAdapter {
    private static final String TAG = "DEBUG";

    Context gContext;
    //colors defined
    private static final int[] colors = new int[]{
            0xff000000, 0xff00007f, 0xff0000ff, 0xff007f00, 0xff007f7f, 0xff00ff00, 0xff00ff7f,
            0xff00ffff, 0xff7f007f, 0xff7f00ff, 0xff7f7f00, 0xff7f7f7f, 0xffff0000, 0xffff007f,
            0xffff00ff, 0xffff7f00, 0xffff7f7f, 0xffff7fff, 0xffffff00, 0xffffff7f, 0xffffffff
    };
    int rowCount;
    int columnCount;

    /**
     * 생성자
     * @param context
     */
    public ColorDataAdapter(Context context) {
        super();
        gContext = context;

        //create test data
        rowCount = 3;
        columnCount = 7;
    }
    //사용자 정의 메소드 start
    public int getNumColumns() {
        Log.d(TAG, "ColorDataAdapter getNumColumns() called  컬럼갯수 : " + columnCount);
        return columnCount;
    }
    //사용자 정의 메소드 end
    //오버라이딩 메소드 start
    //getView 메소드에서 내부적으로 생성할 뷰의 갯수를 이메소드를 통해서 얻는다. 중요함.
    @Override
    public int getCount() {
        Log.d(TAG, "ColorDataAdapter getCount() called  색상수 (컬럼*행) : " + (rowCount * columnCount));
        return rowCount * columnCount;
    }

    @Override
    public Object getItem(int position) {
        Log.d(TAG, "ColorDataAdapter getItem() called \n" +
                "인자 position 의 색상획득함. 획득색상값 : " + colors[position]);
        return colors[position];
    }


    @Override
    public long getItemId(int position) {
        Log.d(TAG, "ColorDataAdapter getIemId() called ");
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "ColorDataAdapter getView(" + position + ") called.");
        //caculate position
        int rowIndex    = position / columnCount;
        int columnIndex = position % columnCount;

        Log.d(TAG, "ColorDataAdapter \n" +
                "  position : " + position +
                "  rowCount : " + rowCount +
                "  rowIndex : " + rowIndex +
                "  columnIndex : " + columnIndex);
        GridView.LayoutParams params = new GridView.LayoutParams(
                GridView.LayoutParams.MATCH_PARENT,
                GridView.LayoutParams.MATCH_PARENT);
        //create a Button with color
        TitleBitmapButton aItem = new TitleBitmapButton(gContext);
        aItem.setText("");
        aItem.setLayoutParams(params);
        aItem.setPadding(4, 4, 4, 4 );
        aItem.setBackgroundColor(colors[position]);
        aItem.setHeight(100);
        aItem.setTag(colors[position]);

        //set Listener
        aItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ColorPaletteDialogActivity.gSelectedListener != null) {
                    Log.d(TAG, "ColorDataAdapter getView() 색상버튼 클릭이벤트 내 색상이 선택되었습니다." +
                            "선택된 색상값은 : " + ((Integer)v.getTag()).intValue());

                    ColorPaletteDialogActivity.gSelectedListener.onColorSelected(((Integer) v.getTag()).intValue());
                }
                ((ColorPaletteDialogActivity)gContext).finish();
            }
        });

        return aItem;
    }
    //getView end
}

