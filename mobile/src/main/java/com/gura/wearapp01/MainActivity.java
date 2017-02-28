package com.gura.wearapp01;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener{
    //필요한 맴버필드 정의하기
    GoogleApiClient gClient;
    EditText inputText, console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // UI 의 참조값 얻어오기
        inputText=(EditText)findViewById(R.id.inputText);
        console=(EditText)findViewById(R.id.console);

        //GoogleApiClient 객체의 참조값 얻어오기
        gClient=new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }
    //버튼을 눌렀을때 호출되는 메소드
    public void send(View v){
        //입력한 문자열을 읽어온다.
        String msg=inputText.getText().toString();
        if(msg.equals(""))return;
        //Wearable 기기에 전송하기 (비동기 작업을 해야한다.)
        new SendMessageTask().execute(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //GoogleApiClient 에 연결 요청하기
        gClient.connect();
    }

    @Override
    protected void onStop() {
        gClient.disconnect();//연결 해제
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        console.append("onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        console.append("onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        console.append("onConnectionFailed()");
    }

    //연결된 Node List 를 리턴하는 메소드
    public Collection<String> getNodes(){
        HashSet<String> results=new HashSet<String>();
        //연결된 NodeList 를 읽어온다.
        NodeApi.GetConnectedNodesResult nodes=
                Wearable.NodeApi.getConnectedNodes(gClient)
                        .await();
        //반복문 돌면서 연결된 node 의 id 를 HashSet 에 담는다.
        for(Node node:nodes.getNodes()){
            results.add(node.getId());
        }
        //연결된 node의 아이디 값을 담고 있는 HashSet 객체 리턴해주기
        return results;
    }

    //비동기 작업 객체를 생성할 클래스
    public class SendMessageTask extends AsyncTask<String,Void,Void> {

        @Override
        protected Void doInBackground(String... params) {
            //전송할 문자열을 읽어온다.
            String msg = params[0];
            //연결된 node id 목록을 얻어온다.
            Collection<String> nodes=getNodes();
            //반복문 돌면서 모든 node 에 전송한다.
            for(final String node: nodes){
                //콘솔에 node id 출력해보기

                //MessageApi 를 이용해서 전송한다.
                //(GoogleApiClient, node id, msg, byte[] )
                Wearable.MessageApi
                        .sendMessage(gClient, node, msg, new byte[0])
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                //전송 성공이라면
                                if(sendMessageResult.getStatus().isSuccess()){
                                    console.setText("전송 성공!");
                                }else{//전송 실패라면
                                    console.setText("전송 실패!");
                                }
                            }
                        });
            }

            return null;
        }
    }
}








