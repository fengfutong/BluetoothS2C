package com.fengfutong.bluetoothc2s;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * ＊ Created by xiaguangcheng on 17/3/9.
 */

public class BluetoothShowAdapter extends RecyclerView.Adapter<BluetoothShowAdapter.NormalHolder> {
    private Context context;

    public void setBondedDeviceList(ArrayList<BluetoothDevice> bondedDeviceList) {
        this.bondedDeviceList = bondedDeviceList;
    }

    private ArrayList<BluetoothDevice> bondedDeviceList;
    public BluetoothShowAdapter(Context context, ArrayList<BluetoothDevice> bondedDeviceList){
        this.context=context;
        this.bondedDeviceList=bondedDeviceList;
    }
    @Override
    public BluetoothShowAdapter.NormalHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalHolder(View.inflate(context, R.layout.item_blueshow,null));
    }

    @Override
    public void onBindViewHolder(BluetoothShowAdapter.NormalHolder holder, int position) {
        holder.tv_device.setText(bondedDeviceList.get(position).getName()+":"+bondedDeviceList.get(position).getAddress());
    }

    @Override
    public int getItemCount() {
        return bondedDeviceList==null?0:bondedDeviceList.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {
        private TextView tv_device;
        public NormalHolder(View itemView) {
            super(itemView);
            tv_device= (TextView) itemView.findViewById(R.id.tv_device_name);
            tv_device.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickContent!=null){
                        clickContent.clickConnect(bondedDeviceList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface ClickContent{
        void clickConnect(BluetoothDevice bluetoothDevice);
    }


    public void setClickContent(ClickContent clickContent) {
        this.clickContent = clickContent;
    }

    private ClickContent clickContent;
}
