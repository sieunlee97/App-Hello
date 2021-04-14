package com.human.edu;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import core.AsyncResponse;
import core.JsonConverter;
import core.PostResponseAsyncTask;

/**
 * 이 액티비티에서는 리사이클러뷰에 RestAPI Json데이터를 바인딩 시키는 기능
 * List 객체(Json데이터바인딩)-어댑터클래스(데이터와 뷰객체 중간)-리사이클러뷰
 */
public class SubActivity extends AppCompatActivity {
    //리사이클러뷰를 사용할 멤버변수(필드변수) 생성
    private RecyclerAdapter mRecyclerAdapter;
    private List mItemList = new ArrayList<MemberVO>();
    //어댑터에서 선택한 값 확인 변수(선택한 회원을 삭제하기 위함)
    private String currentCursorId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        //객체 생성
        mRecyclerAdapter = new RecyclerAdapter(mItemList);
        //리사이클러뷰 xml과 어댑터클래스 바인딩
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mRecyclerAdapter); //데이터 없는 빈 어댑터를 뷰화면에 바인딩시킴
        getAllData();
        mRecyclerAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MemberVO memberVO = (MemberVO) mItemList.get(position);
                currentCursorId = memberVO.getUser_id();
                //Toast.makeText(getApplicationContext(), "현재 선택한 회원 ID는 "+ currentCursorId, Toast.LENGTH_LONG).show();
                deleteUserData(position, currentCursorId);
            }
        });
    }

    //RestAPI 서버로 UserId를 전송해서 스프링앱의 사용자를 삭제하는 메서드
    private void deleteUserData(int position, String currentCursorId) {
        //삭제 대화상자에 보여줄 메세지 생성
        String message = "해당 회원을 삭제하시겠습니까?<br />"+
                "position : "+position+"<br />"+
                "회원ID : "+currentCursorId+"<br />";
        DialogInterface.OnClickListener deleteListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String requestUrl = "http://192.168.56.1:8080/android/delete/"+currentCursorId;
                PostResponseAsyncTask deleteTask = new PostResponseAsyncTask(SubActivity.this, new AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                        if(output.equals("success")){
                            Toast.makeText(SubActivity.this, "삭제 성공", Toast.LENGTH_LONG).show();
                            getAllData();
                        }else{
                            Toast.makeText(SubActivity.this, "삭제 실패", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                deleteTask.execute(requestUrl);
            }
        };
        //삭제를 물어보는 dialog 생성
        new AlertDialog.Builder(this).setTitle("선택한 회원을 삭제")
            .setMessage(Html.fromHtml(message))
            .setPositiveButton("삭제", deleteListener)
            .setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss(); //취소버튼을 눌렀을 때 화면에서 지우기
                        }
                    }
        ).show();

    }

    //RestAPI 서버에서 전송받은 데이터를 리사이클러뷰에 바인딩시킴
    private void getAllData() {
        //RestAPI 서버와 비동기 통신 시작
        String requestUrl = "http://192.168.56.1:8080/android/list";
        HashMap postDataParams = new HashMap();
        postDataParams.put("mobile","android");
        List resultList = new ArrayList<>();//RestAPI에서 보내온 json데이터 저장공간 생성
        PostResponseAsyncTask readTask = new PostResponseAsyncTask(SubActivity.this, postDataParams, new AsyncResponse() {
            @Override
            public void processFinish(String output) {
                Log.i("RestAPI 텍스트 : ", output);
                ArrayList<MemberVO> memberList = new JsonConverter<MemberVO>().toArrayList(output, MemberVO.class);
                //위  컨버트한 memberList변수를 어댑터에 바인딩 시키기(아래)
                for(MemberVO value : memberList){
                    //resultList에 1개 레코드씩 저장 -> 어댑터에 데이터 바인딩 예정
                    String p_id = value.getUser_id();
                    String p_name = value.getUser_name();
                    String p_email = value.getEmail();
                    resultList.add(new MemberVO(p_id, p_name, p_email));
                }
                //화면출력
                mItemList.clear();
                mItemList.addAll(resultList);
                mRecyclerAdapter.notifyDataSetChanged();//어댑터 객체가 리프레시 됨

            }
        });
        readTask.execute(requestUrl); //비동기통신 시작 명령
    }
}