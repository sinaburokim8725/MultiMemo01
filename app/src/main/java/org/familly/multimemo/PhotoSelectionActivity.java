package org.familly.multimemo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;

import org.familly.multimemo.common.TitleBitmapButton;

import java.io.FileNotFoundException;

import static android.provider.MediaStore.Images;

public class PhotoSelectionActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MultiMemo > "+PhotoSelectionActivity.class.getSimpleName();


    private static int spacing = -45;

    CoverFlow gPhotoGallery;
    Uri       gAlbumPhotoUri;
    Bitmap    resultPhotoBitmap = null;
    TextView  gSelectPhotoText;
    ImageView gSelectedPhotoImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selection);
        Log.d(LOG_TAG, "onCreate Start");

        setBottomBtns();

        setSelectPhotoLayout();

        Log.d(LOG_TAG, "loadding galley data ....");

        gPhotoGallery = (CoverFlow) findViewById(R.id.loading_gallery);

        Cursor cursor = getContentResolver().query(Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, Images.Media.DATE_TAKEN + " DESC");

        PhotoCursorAdapter adapter = new PhotoCursorAdapter(this, cursor);
        gPhotoGallery.setAdapter(adapter);

        gPhotoGallery.setSpacing(spacing);
        gPhotoGallery.setSelection(2, true);
        gPhotoGallery.setAnimationDuration(3000);

        Log.d(LOG_TAG, "onCreate() Count of gallery images : " + gPhotoGallery.getCount());

        //커버플로우 클릭이벤트 처리
        gPhotoGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //개별이미지에 대한 uri 생성
                Uri uri = ContentUris.withAppendedId(Images.Media.EXTERNAL_CONTENT_URI, id);
                //앨범에서 선택한 이미지 uri
                gAlbumPhotoUri = uri;
                BitmapFactory.Options options = new BitmapFactory.Options();
                //sinaburokim 원본크기?
                options.inSampleSize = 8;
                try {
                    resultPhotoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),
                            null, options);
                    Log.d(LOG_TAG, "앨범에서 선택된 이미지 uri 정보: " + uri);

                    gSelectPhotoText.setVisibility(View.GONE);

                    gSelectedPhotoImage.setImageBitmap(resultPhotoBitmap);

                    gSelectedPhotoImage.setVisibility(View.VISIBLE);

                } catch (FileNotFoundException e) {
                    Log.e(LOG_TAG, "FILE NOT FOUND  : ", e);
                }
            }
        });
        Log.d(LOG_TAG, "onCreate End");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d(LOG_TAG, "onWindowFocusChanged Start");

        if (hasFocus) {

            MediaScannerConnection.scanFile(this,
                    new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d(LOG_TAG, "onWindowFocusChange() 호출  포커스된 이미지파일 스캔완료 ");
                        }
                    });

        } else {
            Log.d(LOG_TAG, "onWindowFocusChange() 호출 포커스된 파일 찾지못했음.");
        }
        Log.d(LOG_TAG, "onWindowFocusChanged End");

    }

    private void setBottomBtns() {
        Log.d(LOG_TAG, "setBottomBtns Start");

        TitleBitmapButton loadingOkBtn = (TitleBitmapButton) findViewById(R.id.loading_okBtn);

        loadingOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showParentActivity();
            }
        });

        TitleBitmapButton loadingCancelBtn = (TitleBitmapButton) findViewById(R.id.loading_cancelBtn);

        loadingCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "loadingCancelBtn 취소버튼 눌렸음");
                finish();
            }
        });
        Log.d(LOG_TAG, "setBottomBtns End");

    }

    public void setSelectPhotoLayout() {
        Log.d(LOG_TAG, "setSelectPhotoLayout Start");

        gSelectPhotoText = (TextView) findViewById(R.id.loading_selectPhotoText);

        gSelectedPhotoImage = (ImageView) findViewById(R.id.loading_selectedPhoto);

        gSelectedPhotoImage.setVisibility(View.VISIBLE);

        Log.d(LOG_TAG, "setSelectPhotoLayout End");
    }

    //요청 액티비티로 돌아가기
    private void showParentActivity() {
        Intent intent = getIntent();
        if (gAlbumPhotoUri != null && resultPhotoBitmap != null) {
            intent.putExtra(BasicInfo.KEY_URI_PHOTO, gAlbumPhotoUri);
            //선택된 이미지 정보와 함께 호출요청한 액티비티에 요청처리 보냄
            setResult(RESULT_OK, intent);

            Log.d(LOG_TAG, "showParentActivity() 호출 (key,value) = "
                    + " ( " + BasicInfo.KEY_URI_PHOTO + " , " + gAlbumPhotoUri + " ) ");

        }
        finish();
    }

    /**
     * 기능:
     */
    class PhotoCursorAdapter extends CursorAdapter {
        private  final String LOG_TAG = "MultiMemo > "+PhotoCursorAdapter.class.getSimpleName();

        int gGalleryItemBackground;

        public PhotoCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor);
            Log.d(LOG_TAG, "PhotoCursorAdapter Start");

            //sinaburokim 커스텀뷰의 스타일 속성 지정및 변경시 사용하는것같다 자세한것은 심도있게 봐야 한다.
            TypedArray tA = obtainStyledAttributes(R.styleable.Gallery1);

            gGalleryItemBackground = tA.getResourceId(R.styleable.Gallery1_android_galleryItemBackground,
                    0);

            tA.recycle();

            Log.d(LOG_TAG, "PhotoCursorAdapter End");

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Log.d(LOG_TAG, "bindView Start");

            ImageView img = (ImageView) view;
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(Images.Media._ID));

            //개별이미지에 대한 uri 생성
            Uri uri = ContentUris.withAppendedId(Images.Media.EXTERNAL_CONTENT_URI, id);

            Log.d(LOG_TAG, "id : " + id + ", uri : " + uri);

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                //원본크기의 1/(8*8) 크기
                options.inSampleSize = 10;
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri),
                        null,
                        options);
                img.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "bindView End");

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            Log.d(LOG_TAG, "newView() Start");

            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new Gallery.LayoutParams(220, 150));

            Log.d(LOG_TAG, "newView() End");

            return imageView;
        }
    }
}
