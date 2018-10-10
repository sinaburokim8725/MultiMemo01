package org.familly.multimemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.familly.multimemo.common.TitleBitmapButton;

public class MultiMemoMainActivity extends AppCompatActivity {
    //로그켓용 로그
    public static final String TAG = "DEBUG";

    ListView mMemoListView;

    MemoListAdapter mMemoListAdapter;

    int mMemoCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_memo_main);

        //리스트 뷰 참조
        mMemoListView = (ListView) findViewById(R.id.list_memo);
        //리스트아답타에 액티비티 정보를 넘겨준다.
        mMemoListAdapter = new MemoListAdapter(this);
        //리스트 뷰에 아답타를 설정한다.
        mMemoListView.setAdapter(mMemoListAdapter);
        //리스트 아이템 클릭및터치 이벤트 처리
        mMemoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                viewMemo(position);
            }
        });
        //신규메모작성
        TitleBitmapButton nMemoButton =
                (TitleBitmapButton) findViewById(R.id.button_newmemo);
        nMemoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"새로운 멀티메모를 작성합니다.");
            }
        });
        //종료
        TitleBitmapButton cButton =
                (TitleBitmapButton) findViewById(R.id.button_close);
        cButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

        loadMemoListData();
    }
    //실제 데이터베이스에서 데이터를 가져오는 부분 구현필요.
    public void loadMemoListData() {

        MemoListItem aItem = new MemoListItem("1","2018-10-10",
                "아 가을이다 세월은 정말 빨리도 지나가는구낭",
                null,null,
                null,null,
                null,null,
                null,null);
        //리스트 컬렉션에 ArrayList 컬렉션에 아이템들을 담는다.
        mMemoListAdapter.addIteme(aItem);
        /**Notifies the attached observers that the underlying
         * data has been changed and any View reflecting the data
         * set should refresh itself.
         기본 데이터가 변경되었으며
         데이터 세트를 반영한 뷰가
         자체적으로 새로 고쳐 져야 함을 알립니다.
         *
         */
        mMemoListAdapter.notifyDataSetChanged();
    }
    //
    public void viewMemo(int index){
        Log.d(TAG,"리스트아이템이 클릭될때 수정조회 화면으로 전환할수있다.");
    }

}
