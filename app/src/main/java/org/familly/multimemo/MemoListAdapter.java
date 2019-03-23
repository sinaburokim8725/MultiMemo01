package org.familly.multimemo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoListAdapter extends BaseAdapter {
    private static final String LOG_TAG = "MultiMemo > "+MemoListAdapter.class.getSimpleName();

    //activity의 정보를 얻는다.
    private Context context;

    //리스트뷰에 연결시킬 메모아이템들을 담을 컬렉션 선언
    private List<MemoListItem> mItems = new ArrayList<MemoListItem>();


    public MemoListAdapter(Context context) {
        Log.d(LOG_TAG, "MemoListAdapter 생성");

        this.context = context;
    }



    //
    public void clear() {
        /**
         * Removes all of the elements from this list (optional operation).
         * The list will be empty after this call returns.
         * 리스트로부터 모든 요소를 삭제합니다 (옵션).
         * 이 호출이 반환되면 목록이 비어있게됩니다.
         *
         */
        Log.d(LOG_TAG, "clear Start");

        mItems.clear();

        Log.d(LOG_TAG, "clear End");

    }

    /**
     * 아이템들을 리스트에 담는다.
     * @param it
     */
    public void addIteme(MemoListItem it) {
        Log.d(LOG_TAG, "addIteme Start");

        mItems.add(it);

        Log.d(LOG_TAG, "addIteme End");

    }

    public void setListItems(List<MemoListItem> list){
        Log.d(LOG_TAG, "setListItems Start");

        mItems = list;

        Log.d(LOG_TAG, "setListItems End");

    }

    public boolean areAllItemsSelectable(){
        Log.d(LOG_TAG, "areAllItemsSelectable Start");

        Log.d(LOG_TAG, "areAllItemsSelectable false End");
        return false;
    }

    public boolean isSelectable(int index){
        try {
            Log.d(LOG_TAG, "isSelectable Start");

            Log.d(LOG_TAG, "mItems.get(index).isSelectable() > " + mItems.get(index).isSelectable());

            Log.d(LOG_TAG, "isSelectable End");

            return mItems.get(index).isSelectable();
        } catch (IndexOutOfBoundsException ex){
            return false;
        }
    }


    //구현해야할 메소드
    @Override
    public int getCount() {
        Log.d(LOG_TAG, "getCount Start");

        Log.d(LOG_TAG, "mItems.size() > "+mItems.size());

        Log.d(LOG_TAG, "getCount End");

        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        Log.d(LOG_TAG, "getItem Start");

        Log.d(LOG_TAG, "mItems.get(position) > " + mItems.get(position));

        Log.d(LOG_TAG, "getItem End");
        return mItems.get(position);
     }

    @Override
    public long getItemId(int position) {
        Log.d(LOG_TAG, "getItemId Start");

        Log.d(LOG_TAG, "getItemId End >" + position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getView Start");

        MemoListItemView itemView;

        if (convertView == null) {

            itemView = new MemoListItemView(context);
        } else {

            itemView = (MemoListItemView) convertView;
        }
        //set current item data
        //Date 세팅
        itemView.setContents(0, ((String)mItems.get(position).getData(0)));

        //Text 세팅
        itemView.setContents(1, ((String)mItems.get(position).getData(1)));

        //손글씨 세팅 index 3
        itemView.setContents(2, ((String)mItems.get(position).getData(3)));

        //사진 세팅 index 5
        itemView.setContents(3, ((String)mItems.get(position).getData(5)));

        //비데오 7 오디오 9 설정
        itemView.setMediaState(mItems.get(position).getData(7) ,
                               mItems.get(position).getData(9));


        Log.d(LOG_TAG, "getView End");

        return itemView;
    }
}
