package com.fengfutong.bluetoothc2s;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ━━━━━━神兽出没━━━━━━by xiaguangcheng
 * ＊ Created by xiaguangcheng on 17/3/10.
 */

public class HandleSocket extends Thread {
    private boolean isServer;
    private Handler mHanlder;
    public HandleSocket(BluetoothSocket socket, boolean isServer, Handler mHandler) {
        this.isServer=isServer;
        mmSocket=socket;
        this.mHanlder=mHandler;
        InputStream inputStream=null;
        OutputStream outputStream=null;
        try {
            inputStream=socket.getInputStream();
            outputStream=socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmOutStream=outputStream;
        mmInStream=inputStream;
    }


    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;



    public void run(){
        byte [] buffer=new byte[1024];
        int bytes;
        while(true){
            try {
                bytes=mmInStream.read(buffer);
                if(isServer){
                    mHanlder.obtainMessage(1001,bytes,-1,buffer).sendToTarget();
                }
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public void write(byte [] bytes){
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancle(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
