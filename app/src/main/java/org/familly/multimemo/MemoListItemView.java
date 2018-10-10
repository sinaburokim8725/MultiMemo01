package org.familly.multimemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemoListItemView extends LinearLayout {

    private ImageView itemPhoto ,itemVideo,itemVoice,itemHandText;
    private TextView itemText,itemDate;
    Bitmap bitmap;


    /**
     *
     * @param context
     */
    public MemoListItemView(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.memo_listitem_form,this,true);
        //
        itemPhoto = (ImageView) findViewById(R.id.image_itempPoto);
        itemDate = (TextView) findViewById(R.id.text_itemDate);
        itemText = (TextView) findViewById(R.id.text_itemText);
        itemVideo = (ImageView) findViewById(R.id.image_itemVideoState);
        itemVoice = (ImageView) findViewById(R.id.image_itemVoiceState);
        itemHandText = (ImageView) findViewById(R.id.image_itemHandText);
    }

    /**
     *
     * @param index
     * @param data
     */
    public void setContents(int index,String data) {
       switch(index){

           case 0:
               itemDate.setText(data);

               break;

           case 1:
               itemText.setText(data);

               break;

           case 2:
               if(data == null || data.equals("-1")|| data.equals("")) {

                   itemHandText.setImageBitmap(null);

               } else {

                   itemHandText.setImageURI(Uri.parse(BasicInfo.FOLDER_PHOTO + data));

               }

               break;

           case 3:
               if(data == null || data.equals("-1")||data.equals("")) {

                   itemPhoto.setImageResource(R.drawable.person);

               } else {

                   if(bitmap != null) {
                       bitmap.recycle();
                   }

                   BitmapFactory.Options options =new BitmapFactory.Options();
                   options.inSampleSize = 8;
                   bitmap = BitmapFactory.decodeFile(BasicInfo.FOLDER_PHOTO + data,options);
                   itemPhoto.setImageBitmap(bitmap);
               }

               break;

           default:
               throw new IllegalArgumentException();
       }//swich

    }//end

    public void setMediaState(Object sVideo,Object sVoice) {

        if (sVideo == null) {

            itemVideo.setImageResource(R.drawable.icon_video_empty);

        } else {

            itemVideo.setImageResource(R.drawable.icon_video);
        }
        if (sVoice == null) {

            itemVoice.setImageResource(R.drawable.icon_voice_empty);

        } else {

            itemVoice.setImageResource(R.drawable.icon_voice);
        }
    }
}
