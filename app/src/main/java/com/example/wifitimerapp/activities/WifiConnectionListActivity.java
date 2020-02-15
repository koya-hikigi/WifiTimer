package com.example.wifitimerapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wifitimerapp.R;

import java.util.ArrayList;
import java.util.List;

public class WifiConnectionListActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter mAdapter;
    private LinearLayout mWifiConnectingLayout;
    private TextView mConnectingSsidTextView;
    private ImageView mWifiConnectingImageView;
    private TextView mWifiConnectingTextView;
    private WifiManager mWifiManager;
    private List<WifiConnectionListItem> wifiConnectionListItem = new ArrayList<>();



    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED,false);
            if(success){
                scanSuccess();
            } else {
                scanFailure();
            }
        }
    };

    private void scanSuccess(){
        Log.d(WifiConnectionListActivity.class.getSimpleName(), "scanSuccess成功");
        List<ScanResult> results = mWifiManager.getScanResults();

        int numLevels = 3;
        List<WifiConnectionListItem> detaList = new ArrayList<>();

        for(ScanResult result : results){
            String ssid = result.SSID;
            int level = WifiManager.calculateSignalLevel(result.level , numLevels );
            Log.d(WifiConnectionListActivity.class.getSimpleName(), ssid + " " +level + "SSID get成功");
            detaList.add(new WifiConnectionListItem(result  ));

        }
        mAdapter.clear();
        mAdapter.addAll(detaList);
        mAdapter.notifyDataSetChanged();
        Log.d(WifiConnectionListActivity.class.getSimpleName(), "mAdapterへの通知成功");
    }

    private  void scanFailure(){
        Log.d(WifiConnectionListActivity.class.getSimpleName(), "失敗");
    }




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection_list);

        mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        mWifiConnectingLayout = findViewById(R.id.Wifi_Connection_List_item);


        mConnectingSsidTextView = findViewById(R.id.ssid_text);
        mWifiConnectingTextView = findViewById(R.id.Wifi_Connecting_text);
        mWifiConnectingImageView = findViewById(R.id.Wifi_level_imp);


        mListView = findViewById(R.id.Wifi_Connection_List);

        //TODO: アダプターのインスタンスを作成
        mAdapter = new WifiListAdapter(this, wifiConnectionListItem );


        //TODO: LIstViewにアダプターをセット
        mListView.setAdapter(mAdapter);
        Log.d(WifiConnectionListActivity.class.getSimpleName(), "setAdapter成功");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //TODO: Wi-Fiデータの読み込み処理


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(mWifiScanReceiver, intentFilter);

        boolean success = mWifiManager.startScan();
        if(!success){
            scanFailure();
        }

    }


    private void updateWifiConnectingLayout( boolean visibility ){

        if(visibility){
            mWifiConnectingLayout.setVisibility(View.VISIBLE);
        } else {
            mWifiConnectingLayout.setVisibility(View.GONE);
        }

    }


    private class WifiListAdapter extends ArrayAdapter<WifiConnectionListItem>{

        List<WifiConnectionListItem> dataList = new ArrayList<>();
        LayoutInflater layoutInflater;

        public WifiListAdapter(@NonNull Context context,  @NonNull List objects) {
                super(context, R.layout.layout_wifi_connection_list_item, objects);
                dataList = objects;
                layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Nullable
        @Override
        public WifiConnectionListItem getItem(int position) {
            return dataList.get(position);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ListItemHolder holder;

            if(convertView == null){
                convertView = layoutInflater.inflate(R.layout.layout_wifi_connection_list_item,parent,false);
                holder= new ListItemHolder();
                TextView ssidTextView = convertView.findViewById(R.id.ssid_text);
                ImageView wifiLevelImageView = convertView.findViewById(R.id.Wifi_level_imp);
                holder.setSsidTextView(ssidTextView);
                holder.setWifiLevelImageView(wifiLevelImageView);
                convertView.setTag(holder);
            } else {
                holder = (ListItemHolder)convertView.getTag();
            }

            WifiConnectionListItem item = dataList.get(position);
            holder.getSsidTextView().setText(item.getmSsid());
            holder.getWifiLevelImageView().setImageResource(item.getLevelResId());
            return convertView;
        }

        private class ListItemHolder{
            private ImageView wifiLevelImageView;
            private TextView ssidTextView;

            private ImageView getWifiLevelImageView() {
                return wifiLevelImageView;
            }

            private void setWifiLevelImageView(ImageView wifiLevelImageView) {
                this.wifiLevelImageView = wifiLevelImageView;
            }

            private TextView getSsidTextView() {
                return ssidTextView;
            }

            private void setSsidTextView(TextView ssidTextView) {
                this.ssidTextView = ssidTextView;
            }
        }

    }
}
