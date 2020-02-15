package com.example.wifitimerapp.activities;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.example.wifitimerapp.R;

public class WifiConnectionListItem {

    private static final int[] LEVEL_RES_IDS = {R.mipmap.wifi_level_1, R.mipmap.wifi_level_2, R.mipmap.wifi_level_3};
    private ScanResult mScanResult;

    private String mSsid;
    private int mLevelResId;
    private static final int LEVEL_RANGE = 3;


    WifiConnectionListItem( ScanResult scanResult ){
        mSsid = scanResult.SSID;
        int levelIndex = WifiManager.calculateSignalLevel(scanResult.level, LEVEL_RANGE);
        mLevelResId = LEVEL_RES_IDS[levelIndex];

    }

    public String getmSsid(){
        return mSsid;
    }

    public int getLevelResId(){
        return mLevelResId;
    }

}
