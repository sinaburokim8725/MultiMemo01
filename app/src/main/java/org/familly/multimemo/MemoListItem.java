package org.familly.multimemo;

import android.util.Log;

public class MemoListItem {
    private static final String LOG_TAG = "MultiMemo > "+MemoListItem.class.getSimpleName();


    private String itemId;

    private String[] objItems;

    //각아이템을 선택할수 있는지 여부
    private boolean selectableListItem = false;//true

    public MemoListItem(String itemId, String[] obj) {
        Log.d(LOG_TAG, "MemoListItem 생성");

        this.itemId = itemId;
        this.objItems = obj;
    }

    /**
     * initialize with string
     *
     * @param memoId
     * @param memoDate
     * @param memoText
     * @param id_hand_writing
     * @param uri_handwriting
     * @param id_phto
     * @param uri_photo
     * @param id_video
     * @param uri_video
     * @param id_voice
     * @param uri_voice
     */
    public MemoListItem(String memoId, String memoDate, String memoText,
                        String id_hand_writing, String uri_handwriting,
                        String id_phto, String uri_photo,
                        String id_video, String uri_video,
                        String id_voice, String uri_voice) {

        Log.d(LOG_TAG, "MemoListItem 생성");

        itemId = memoId;
        objItems = new String[10];
        objItems[0] = memoDate;
        objItems[1] = memoText;
        objItems[2] = id_hand_writing;
        objItems[3] = uri_handwriting;
        objItems[4] = id_phto;
        objItems[5] = uri_photo;
        objItems[6] = id_video;
        objItems[7] = uri_video;
        objItems[8] = id_voice;
        objItems[9] = uri_voice;

    }

    /**
     * true if this item is selectable
     */
    public boolean isSelectable() {
        Log.d(LOG_TAG, "isSelectable Start");

        Log.d(LOG_TAG, "selectableListItem > "+selectableListItem);

        Log.d(LOG_TAG, "isSelectable End");

        return selectableListItem;
    }

    /**
     * Set selectable flag
     */
    public void setSelectable(boolean selectable) {
        Log.d(LOG_TAG, "setSelectable Start");

        selectableListItem = selectable;

        Log.d(LOG_TAG, "setSelectable End");

    }

    /**
     * Get data array
     *
     * @return
     */
    public String[] getData() {
        Log.d(LOG_TAG, "getData Start");


        Log.d(LOG_TAG, "getData End");

        return objItems;
    }

    /**
     * Get data
     *
     * @param index
     * @return
     */
    public String getData(int index) {
        Log.d(LOG_TAG, "getData Start");


        if (objItems == null) {
            Log.d(LOG_TAG, "getData null End");

            return null;
        }
        Log.d(LOG_TAG, "getData End");

        return objItems[index];
    }

    /**
     * Set array
     *
     * @param obj
     */
    public void setData(String[] obj) {
        Log.d(LOG_TAG, "setData Start");

        objItems = obj;

        Log.d(LOG_TAG, "setData End");

    }


    //Compare with the input object
    public int compareTo(MemoListItem other) {
        Log.d(LOG_TAG, "compareTo Start");

        if (objItems != null) {
            String[] array = other.getData();

            //길이가 같으면 동일한 객체?
            if (objItems.length == array.length) {
                int index = 0;
                for (String s : objItems) {
                    if (!objItems[index].equals(s)) {
                        Log.d(LOG_TAG, "compareTo -1 End");

                        return -1;
                    }
                    index++;
                }

            } else {
                Log.d(LOG_TAG, "compareTo -1 End");

                return -1;
            }

        } else {
            throw new IllegalArgumentException();
        }
        Log.d(LOG_TAG, "compareTo 0 End");
        //정상코드
        return 0;
    }

    public String getId() {
        Log.d(LOG_TAG, "getId Start");


        Log.d(LOG_TAG, "getId End");

        return itemId;
    }
}
