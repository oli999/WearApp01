package com.gura.wearapp01;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by user on 2017-02-28.
 */

public class DataLayerListenerService
                extends WearableListenerService{
    //MessageApi 를 이용해서 전달된 메세지를 전달받는 메소드
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //전달된 path 를 읽어온다
        String path=messageEvent.getPath();
        if(path.equals("/startMainActivity")){
             //잠자는 기기를 깨우고
            Util.acquireCpuWakeLock(this);
            //특정 액티비티(MainActivity)를 활성화 시킨다.
            Intent intent=new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}











