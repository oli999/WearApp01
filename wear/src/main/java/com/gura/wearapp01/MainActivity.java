package com.gura.wearapp01;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

public class MainActivity extends Activity
        implements GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            MessageApi.MessageListener{

    //필요한 맴버필드 정의하기
    GoogleApiClient gClient;
    EditText console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_main);
        console=(EditText)findViewById(R.id.console);
        //GoogleApiClient 의 참조값 얻어오기
        gClient=new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gClient.connect();
    }

    @Override
    protected void onStop() {
        gClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        console.append("onConnected()\r\n");
        //GoogleApiClient 에 연결 되었다면 Message 리스너를 등록한다.
        Wearable.MessageApi.addListener(gClient, this);
        //Phone 에 있는 MainActivity 를 활성화 시킬수 있도록 메세지를 보낸다
        new SendMessageTask().execute("/startMainActivity");
    }

    @Override
    public void onConnectionSuspended(int i) {
        console.append("onConnectionSuspended()\r\n");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        console.append("onConnectionFailed()\r\n");
    }
    //Message 가 전달되었을때 호출되는 메소드
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //전달된 path (메세지) 를 읽어온다.
        String msg = messageEvent.getPath();
        console.append(msg+"\r\n");
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







