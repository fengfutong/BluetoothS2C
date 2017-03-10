package com.fengfutong.bluetoothc2s;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * ＊ Created by xiaguangcheng on 17/3/10.
 */

public class BluetoothEditActivity extends Activity implements BluetoothShowAdapter.ClickContent {
    private boolean isServer;
    private TextView tv_can_check, tv_stop, tv_find, tv_has_check,tv_server,tv_tips;
    private RecyclerView recyclerView;
    private Client clinet;
    private  Server server;
    private EditText editText;
    private BluetoothAdapter defaultAdapter;
    //存放已搜索到的和刚搜索到的蓝牙设备
    private ArrayList<BluetoothDevice> list = new ArrayList<>();
    //展示蓝牙设备适配器
    private BluetoothShowAdapter bluetoothShowAdapter;

    private StringBuilder sb=new StringBuilder();
    private Handler mHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.arg1!=-1){
                sb.append(new String((byte[]) msg.obj));
                editText.setText(sb.toString());
            }
        }
    };
    //用来处理蓝牙状态的广播
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                //是否是蓝牙状态改变的广播
                int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                int intExtra1 = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, -1);
                tipInfo( "new scan state:" + intExtra + ";previous scan state:" + intExtra1);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //是否是发现蓝牙设备的广播
                BluetoothDevice parcelableExtra = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                list.add(parcelableExtra);
                if (bluetoothShowAdapter == null) {
                    bluetoothShowAdapter = new BluetoothShowAdapter(context, list);
                    bluetoothShowAdapter.setClickContent(BluetoothEditActivity.this);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                bluetoothShowAdapter.setBondedDeviceList(list);
                bluetoothShowAdapter.notifyDataSetChanged();
            } else if (BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(action)) {
                // 扫描设备状态的更改
                int intExtra = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, -1);
                int intExtra1 = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_SCAN_MODE, -1);
                tipInfo( "new scan state:" + intExtra + ";previous scan state:" + intExtra1);

            }

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_blue_tooth_edit);
        if (getIntent() != null) {
            isServer = getIntent().getBooleanExtra(ApplicationConfig.isServer, false);
        }
        defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        initView();
        if (defaultAdapter == null) {
            tipInfo(R.string.not_support_bluetooth);
        } else {
            //查看蓝牙是否启动
            boolean enabled = defaultAdapter.isEnabled();
            if (enabled) {
                //蓝牙已启动
            } else {
                //通过系统intent启动蓝牙
                Intent blueIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(blueIntent, ApplicationConfig.BLUETOOTH_REQUEST_CODE_TURNON);
            }
        }


        IntentFilter intentFilter = new IntentFilter();
        //过滤蓝牙状态的广播
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //过滤发现蓝牙设备的广播
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        //过滤可检测模式变化的广播
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);


        // 点击查看已有的蓝牙设备
        tv_has_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBluetoothList();
                if (list.isEmpty()) {
                    tipInfo(R.string.not_found_device);
                }
                bluetoothShowAdapter = new BluetoothShowAdapter(BluetoothEditActivity.this, list);
                bluetoothShowAdapter.setClickContent(BluetoothEditActivity.this);
                recyclerView.setAdapter(bluetoothShowAdapter);
                recyclerView.setVisibility(View.VISIBLE);

            }
        });
        //开始扫描设备
        tv_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultAdapter != null) {
                    tipInfo(R.string.start_scan);
                    defaultAdapter.startDiscovery();
                }
            }
        });
        //停止扫描设备
        tv_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (defaultAdapter != null) {
                    tipInfo(R.string.scan_cancel);
                    defaultAdapter.cancelDiscovery();
                }
            }
        });
        //打开设备可检测性
        tv_can_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                //值为0，表示始终可以被检测到，任何小于0，大于3600s的都会被设置为120秒，默认120秒
                intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, ApplicationConfig.BLUETOOTH_DISCOVER_TIME);//100秒，
                //如果用户允许，则resultcode==BLUETOOTH_DISCOVER_TIME，也就是我们设置的时间
                //如果用户拒绝，就是resultcode＝＝RESULT_CANCELED
                startActivityForResult(intent, ApplicationConfig.BLUETOOTH_REQUEST_CODE_CHECK);

            }
        });

        tv_server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server=new Server(BluetoothEditActivity.this,defaultAdapter,mHandler);
                server.start();
                tipInfo(R.string.server_success);
            }
        });
    }

    private void initView() {
        tv_can_check = (TextView) findViewById(R.id.tv_can_check);
        tv_find = (TextView) findViewById(R.id.tv_find);
        tv_has_check = (TextView) findViewById(R.id.tv_check);
        tv_stop= (TextView) findViewById(R.id.tv_stop);
        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        tv_server= (TextView) findViewById(R.id.tv_server);
        editText = (EditText) findViewById(R.id.edit);
        tv_tips= (TextView) findViewById(R.id.tv_tips);
        if (isServer) {
            tv_can_check.setVisibility(View.VISIBLE);
            tv_server.setVisibility(View.VISIBLE);
            editText.setHint(getResources().getString(R.string.hint_server));
            tv_tips.setText(getResources().getString(R.string.tip_server));
        } else {
            tv_has_check.setVisibility(View.VISIBLE);
            tv_find.setVisibility(View.VISIBLE);
            tv_stop.setVisibility(View.VISIBLE);
            tv_tips.setText(getResources().getString(R.string.tip_client));

        }
    }

    private ArrayList<BluetoothDevice> getBluetoothList() {
        if (defaultAdapter != null) {
            Set<BluetoothDevice> bondedDevices = defaultAdapter.getBondedDevices();
            if (bondedDevices != null && bondedDevices.size() > 0) {
                for (BluetoothDevice bluetoothDevice : bondedDevices) {
                    list.add(bluetoothDevice);
                }
            }
        }
        return list;
    }

    //点击事件的回调。当用户点击蓝牙设备时，就是像该蓝牙设备传递数据
    @Override
    public void clickConnect(BluetoothDevice bluetoothDevice) {
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        String message = editText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            tipInfo(R.string.tip_message);
            return;
        }
        clinet = new Client(this, message, bluetoothDevice, defaultAdapter);
        clinet.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clinet != null) {
            clinet.cancle();
        }
        if(server!=null){
            server.cancle();
        }
        if(mHandler!=null){
            mHandler.removeCallbacksAndMessages(null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ApplicationConfig.BLUETOOTH_REQUEST_CODE_TURNON && resultCode == RESULT_OK) {
            //蓝牙启动成功
            tipInfo(R.string.bluetooth_success);
        } else if (requestCode == ApplicationConfig.BLUETOOTH_REQUEST_CODE_CHECK && resultCode == ApplicationConfig.BLUETOOTH_DISCOVER_TIME) {
            tipInfo(R.string.bluetooth_check);
        } else {
            //蓝牙启动失败
            tipInfo(R.string.bluetooth_fail);
        }
    }

    private void tipInfo(int resourceID){
        Toast.makeText(this,resourceID , Toast.LENGTH_SHORT).show();

    }
    private void tipInfo(String string){
        Toast.makeText(this,string , Toast.LENGTH_SHORT).show();

    }
}
