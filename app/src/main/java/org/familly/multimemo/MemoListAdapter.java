package org.familly.multimemo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class MemoListAdapter extends BaseAdapter {

    //activity의 정보를 얻는다.
    private Context context;

    //리스트뷰에 연결시킬 메모아이템들을 담을 컬렉션 선언
    private List<MemoListItem> mItems = new ArrayList<MemoListItem>();


    public MemoListAdapter(Context context) {
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
        mItems.clear();
    }

    /**
     * 아이템들을 리스트에 담는다.
     * @param it
     */
    public void addIteme(MemoListItem it) {
        mItems.add(it);
    }

    public void setListItems(List<MemoListItem> list){
        mItems = list;
    }

    public boolean areAllItemsSelectable(){
        return false;
    }

    public boolean isSelectable(int index){
        try {
            return mItems.get(index).isSelectable();
        } catch (IndexOutOfBoundsException ex){
            return false;
        }
    }


    //구현해야할 메소드
    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
     }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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


        return itemView;
    }
}
