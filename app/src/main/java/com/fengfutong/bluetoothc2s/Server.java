package com.fengfutong.bluetoothc2s;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import java.io.IOException;

/**
 * ━━━━━━神兽出没━━━━━━by xiaguangcheng
 * ＊ Created by xiaguangcheng on 17/3/10.
 */

public class Server extends Thread{
    private Context mContext;
    private final BluetoothServerSocket mmServerSocket;
    private Handler mHandler;


    public Server(Context context, BluetoothAdapter defaultAdapter, Handler mHandler) {
        mContext=context;
        this.mHandler=mHandler;
        BluetoothServerSocket tmp=null;
        try{
            tmp=defaultAdapter.listenUsingRfcommWithServiceRecord(context.getPackageName(),ApplicationConfig.myUUID);
        }catch (Exception e){

        }
        mmServerSocket=tmp;
    }


    public void run(){
        BluetoothSocket socket=null;
        while(true){
            try{
                //This method will block
                socket=mmServerSocket.accept();
            }catch (Exception e){
                break;
            }
            if(socket!=null){
                new HandleSocket(socket,true,mHandler).start();
                try {
                    mmServerSocket.close();
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }

            }
        }
    }

    public void cancle(){
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
