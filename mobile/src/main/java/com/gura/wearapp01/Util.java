package com.gura.wearapp01;

import android.content.Context;
import android.os.PowerManager;

/**
 * Created by user on 2017-02-28.
 */

public class Util {
    /*
       기기를 깨우기 위해서는 퍼미션이 필요하다
       androidManifest.xml 에 다음 퍼미션을 추가 한다.

       android.permission.WAKE_LOCK
    */
    private static PowerManager.WakeLock wakeLock;
    //기기 깨우기
    public static void acquireCpuWakeLock(Context context){
        if(wakeLock != null)return;
        //PowerManager 객체 얻어오기
        PowerManager pm=(PowerManager)
                context.getSystemService(Context.POWER_SERVICE);
        //WakeLock 객체 얻어오기
        wakeLock=pm.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK |
                        PowerManager.ACQUIRE_CAUSES_WAKEUP |
                        PowerManager.ON_AFTER_RELEASE,
                "kimgura");
        wakeLock.acquire();
    }
    //기기를 다시 쉬게하기.
    public static void releaseCpuLock(){
        if(wakeLock !=null){
            wakeLock.release();
            wakeLock=null;
        }
    }
}
