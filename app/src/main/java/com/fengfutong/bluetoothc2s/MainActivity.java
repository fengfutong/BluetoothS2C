package com.fengfutong.bluetoothc2s;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void server_click(View view){
        Intent intent=new Intent(this,BluetoothEditActivity.class);
        intent.putExtra(ApplicationConfig.isServer,true);
        startActivity(intent);
    }

    public void client_click(View view){
        Intent intent=new Intent(this,BluetoothEditActivity.class);
        intent.putExtra(ApplicationConfig.isServer,false);
        startActivity(intent);
    }
}
