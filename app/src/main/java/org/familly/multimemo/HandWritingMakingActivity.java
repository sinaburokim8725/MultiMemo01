package org.familly.multimemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.File;
import java.io.FileOutputStream;

public class HandWritingMakingActivity extends AppCompatActivity {

    public static final String LOG_TAG = "MultiMemo > "+HandWritingMakingActivity.class.getSimpleName();

    LinearLayout      gAddedLayout;
    TextView          gSizeLegendTxt;
    TitleBitmapButton gColorLegendBtn;
    TitleBitmapButton gColorBtn ;
    TitleBitmapButton gPenBtn   ;
    TitleBitmapButton gEraserBtn;
    TitleBitmapButton gUndoBtn  ;
    TitleBitmapButton gHandwritingSaveBtn;

    private int gColor = 0xff000000;
    private int gSize = 8;

    int gOldColor;
    int gOldSize ;

    HandwritingView gWritingBoard;
    private boolean gEraserSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hand_writing_making);

        Log.d(LOG_TAG, "onCreate Start");


        setTopLayout();

        setBottomLayout();

        setWritingBoard();

        Log.d(LOG_TAG, "onCreate End");

    }

    private void setWritingBoard() {
        Log.d(LOG_TAG, "setWritingBoard Start");

        LinearLayout boardLayout = (LinearLayout) findViewById(R.id.layout_board);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        gWritingBoard = new HandwritingView(this);

        gWritingBoard.setLayoutParams(params);

        gWritingBoard.setPadding(2, 2, 2, 2);

        boardLayout.addView(gWritingBoard);

        Log.d(LOG_TAG, "setWritingBoard End");

    }

    private void setBottomLayout() {
        Log.d(LOG_TAG, "setBottomLayout Start");

        gHandwritingSaveBtn = (TitleBitmapButton) findViewById(R.id.button_handwriting_save);

        gHandwritingSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHandwritingMaking();
            }
        });

        Log.d(LOG_TAG, "setBottomLayout End");
    }

    private void saveHandwritingMaking() {
        Log.d(LOG_TAG, "saveHandwritingMaking Start");

        checkHandwritingFolder();
        String handWritingName = "made";
        File file = new File(BasicInfo.FOLDER_HANDWRITING + handWritingName);
        if (file.exists()) {
            file.delete();
        }
        //새로운 손글씨파일 생성
        try {
            FileOutputStream handImage = new FileOutputStream(BasicInfo.FOLDER_HANDWRITING + handWritingName);

            Bitmap imageBitmap = gWritingBoard.getImage();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, handImage);
            handImage.close();

        } catch (Exception e) {
            Log.e(LOG_TAG,"예외발생 saveHandwritingMaking() ",e);
        }
        setResult(RESULT_OK);

        Log.d(LOG_TAG, "saveHandwritingMaking End");

        finish();
    }

    private void checkHandwritingFolder() {
        Log.d(LOG_TAG, "checkHandwritingFolder Start");

        File handwritingFolder = new File(BasicInfo.FOLDER_HANDWRITING);
        if (!handwritingFolder.isDirectory()) {
            Log.d(LOG_TAG, "폴던없음 폴더생성함:");
            handwritingFolder.mkdirs();
        }

        Log.d(LOG_TAG, "checkHandwritingFolder End");

    }

    public void setTopLayout() {
        Log.d(LOG_TAG, "setTopLayout Start");


        LinearLayout toolsLayout = (LinearLayout) findViewById(R.id.layout_tools);

        gColorBtn  = (TitleBitmapButton) findViewById(R.id.button_color);
        gPenBtn    = (TitleBitmapButton) findViewById(R.id.button_pen);
        gEraserBtn = (TitleBitmapButton) findViewById(R.id.button_eraser);
        gUndoBtn   = (TitleBitmapButton) findViewById(R.id.button_undo);
        //이벤트 start
        //색상선택 클릭 이벤트
        gColorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPaletteDialogActivity.gSelectedListener = new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        gColor = color;
                        gWritingBoard.updatePaintProperty(gColor,gSize);
                        displayPaintProperty();
                    }
                };
                //show color palette dialog
                Intent intent = new Intent(getApplicationContext(), ColorPaletteDialogActivity.class);
                startActivity(intent);

            }
        });
        //그림펜 굵기선택 이벤트
        gPenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PenPaletteDialogActivity.mSelectedListener = new OnPenSelectedListener() {
                    @Override
                    public void onPenSelected(int penSize) {
                        Log.d(LOG_TAG, "HandwritingMakingActivity 펜굵기가 선택되었음 선택된 펜굵기 :" + penSize);
                        gSize = penSize;
                        gWritingBoard.updatePaintProperty(gColor, gSize);
                        displayPaintProperty();
                    }
                };
                //show pen palette dialog
                Intent intent = new Intent(getApplicationContext(), PenPaletteDialogActivity.class);
                startActivity(intent);
            }
        });

        //지우개 버튼 클릭
        gEraserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gEraserSelected = !gEraserSelected) {
                    gColorBtn.setEnabled(false);
                    gPenBtn.setEnabled(false);
                    gUndoBtn.setEnabled(false);

                    gColorBtn.invalidate();
                    gPenBtn.invalidate();
                    gUndoBtn.invalidate();

                    gOldColor = gColor;
                    gOldSize  = gSize;

                    gColor = Color.WHITE;
                    gSize  = 35;

                    gWritingBoard.updatePaintProperty(gColor, gSize);
                    displayPaintProperty();

                } else {
                    gColorBtn.setEnabled(true);
                    gPenBtn.setEnabled(true);
                    gUndoBtn.setEnabled(true);

                    gColorBtn.invalidate();
                    gPenBtn.invalidate();
                    gUndoBtn.invalidate();

                    gColor = gOldColor;
                    gSize = gOldSize;

                    gWritingBoard.updatePaintProperty(gColor, gSize);
                    displayPaintProperty();
                }

            }
        });

        //이전버튼 클릭
        gUndoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gWritingBoard.undo();
            }
        });

        //이벤트 end

        //기타도입부 start
        //add legend button
        LinearLayout.LayoutParams addedParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        gAddedLayout = new LinearLayout(this);
        gAddedLayout.setLayoutParams(addedParams);
        gAddedLayout.setOrientation(LinearLayout.VERTICAL);
        gAddedLayout.setPadding(8, 8, 8, 8);

        LinearLayout outlineLayout = new LinearLayout(this);
        outlineLayout.setLayoutParams(buttonParams);
        outlineLayout.setOrientation(LinearLayout.VERTICAL);
        outlineLayout.setBackgroundColor(Color.LTGRAY);
        outlineLayout.setPadding(1, 1, 1, 1);

        gColorLegendBtn = new TitleBitmapButton(this);
        gColorLegendBtn.setClickable(false);
        gColorLegendBtn.setLayoutParams(buttonParams);
        gColorLegendBtn.setText(" ");
        gColorLegendBtn.setBackgroundColor(gColor);
        gColorLegendBtn.setHeight(20);

        outlineLayout.addView(gColorLegendBtn);
        gAddedLayout.addView(outlineLayout);

        gSizeLegendTxt = new TextView(this);
        gSizeLegendTxt.setLayoutParams(buttonParams);
        gSizeLegendTxt.setText("size : " + gSize);
        gSizeLegendTxt.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        gSizeLegendTxt.setTextSize(16);
        gSizeLegendTxt.setTextColor(Color.BLACK);

        gAddedLayout.addView(gSizeLegendTxt);

        toolsLayout.addView(gAddedLayout);

        //end
        Log.d(LOG_TAG, "setTopLayout End");

    }


    private void displayPaintProperty() {
        Log.d(LOG_TAG, "displayPaintProperty Start");

        gColorLegendBtn.setBackgroundColor(gColor);
        gSizeLegendTxt.setText("Size : " + gSize);
        gAddedLayout.invalidate();

        Log.d(LOG_TAG, "displayPaintProperty End");

    }

}
