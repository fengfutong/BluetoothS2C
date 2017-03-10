package com.fengfutong.bluetoothc2s;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;

/**
 * ━━━━━━神兽出没━━━━━━by xiaguangcheng
 * ＊ Created by xiaguangcheng on 17/3/10.
 */

public class Client extends Thread {
    private Context mContext;
    private BluetoothAdapter defaultAdapter;
    private String message;
    public Client(Context context,String message,BluetoothDevice device,BluetoothAdapter defaultAdapter){
        mContext=context;
        this.defaultAdapter=defaultAdapter;
        BluetoothSocket tmp=null;
        mmDevice=device;
        this.message=message;
        try {
            tmp=device.createRfcommSocketToServiceRecord(ApplicationConfig.myUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mmSocket=tmp;
    }

    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;


    public void run(){
        defaultAdapter.cancelDiscovery();
        try {
            //This method will block
            mmSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mmSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        HandleSocket handleSocket=new HandleSocket(mmSocket,false, null);
        handleSocket.write(message.getBytes());

    }

    public void cancle(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
