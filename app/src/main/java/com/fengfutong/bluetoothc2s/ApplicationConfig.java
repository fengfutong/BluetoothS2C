package com.fengfutong.bluetoothc2s;

import java.util.UUID;

/**
 * ＊ Created by xiaguangcheng on 17/3/10.
 */

public class ApplicationConfig {
    public static final UUID myUUID=UUID.fromString("dfb79481-691c-497e-9f0e-36de8906759a");
    public static final String isServer="isserver";
    //设置设备可以被检查到的时间
    public static final int BLUETOOTH_DISCOVER_TIME=100;
    public static final int BLUETOOTH_REQUEST_CODE_CHECK=202;
    public static final int BLUETOOTH_REQUEST_CODE_TURNON=201;
}
