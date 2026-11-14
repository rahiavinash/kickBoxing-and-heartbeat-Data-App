package com.app.kickboxing.Activitys.presentation.ScanDevices;


import android.animation.ValueAnimator;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.compose.ui.platform.ComposeView;
import androidx.lifecycle.ViewTreeLifecycleOwner;
import androidx.lifecycle.ViewTreeViewModelStoreOwner;
import androidx.savedstate.ViewTreeSavedStateRegistryOwner;

import com.app.kickboxing.Activitys.domain.response.BluetoothDeviceModel;
import com.app.kickboxing.Activitys.presentation.Ui.home.DeviceIntegrationScreenKt;
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScan;
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScanCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDevice;
import com.app.kickboxing.R;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;


public class DevicesScanActivity extends ComponentActivity {


    // TAG值
    private static final String TAG = DevicesScanActivity.class.getSimpleName();
    public static int SCAN_ACTIVITY_BACK = 1001;
    private FBKApiScan m_scanner;
    private ListView m_scanListView;

    private BaseAdapter m_scanListAdapter;
    private static List<FBKBleDevice> m_deviceArray = new ArrayList<>();
    private int m_cellNumber;


    /************************************************************************************
     * 方法名称：FBKApiScanCallBack
     * 功能描述：扫描回调
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private FBKApiScanCallBack m_apiScanCallBack = new FBKApiScanCallBack() {
//        @Override
//        public void bleScanResult(List<FBKBleDevice> deviceArray, FBKApiScan apiScan) {
//            m_deviceArray = deviceArray;
//            m_scanListAdapter.notifyDataSetChanged();
//        }

        @Override
        public void bleScanResult(List<FBKBleDevice> deviceArray, FBKApiScan apiScan) {
            m_deviceArray.clear(); // Clear previous list

            for (FBKBleDevice device : deviceArray) {
                if (device.getDeviceName() != null && device.getDeviceName().toLowerCase().contains("kbxc")) {
                    m_deviceArray.add(device); // Only add KBXC devices
                }
            }

            m_scanListAdapter.notifyDataSetChanged(); // Refresh UI
        }



        @Override
        public void bleScanAvailable(Boolean isAvailable, FBKApiScan apiScan) {
            if (!isAvailable) {
                runOnUiThread(() ->
                        Toast.makeText(DevicesScanActivity.this, "Bluetooth and location permissions are required.Please turn on the bluetooth.", Toast.LENGTH_LONG).show()
                );
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }

            }
        }
    };


    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        window.setStatusBarColor(Color.TRANSPARENT);


        setContentView(R.layout.activity_devicescan);

        View pulseView1 = findViewById(R.id.pulse_circle1);
        View pulseView2 = findViewById(R.id.pulse_circle2);
        View pulseView3 = findViewById(R.id.pulse_circle3);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.bluetooth_scale);
        pulseView1.startAnimation(pulseAnimation);

//        FrameLayout progressBar = findViewById(R.id.custom_progress_bar);
//        View progressFill = findViewById(R.id.progress_fill);

       // float progress = 0.6f;

        FrameLayout progressBar = findViewById(R.id.custom_progress_bar);
        View progressFill = findViewById(R.id.progress_fill);

        progressBar.post(() -> {
            int totalWidth = progressBar.getWidth();


            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(30000);
            animator.addUpdateListener(animation -> {
                float progress = (float) animation.getAnimatedValue();
                int progressWidth = (int) (progress * totalWidth);

                ViewGroup.LayoutParams params = progressFill.getLayoutParams();
                params.width = progressWidth;
                progressFill.setLayoutParams(params);
            });
            animator.start();
        });




        //  ComposeView composeView = findViewById(R.id.compose_ble);


//        ViewTreeLifecycleOwner.set(composeView, this);
//        ViewTreeViewModelStoreOwner.set(composeView, this);
//        ViewTreeSavedStateRegistryOwner.set(composeView, this);

//        composeView.post(() -> {
//            composeView.setContent((composer, i) -> {
//                DeviceIntegrationScreenKt.BluetoothIconWrapper();
//                return Unit.INSTANCE;
//            });
//        });
        m_cellNumber = -1;
        initView();


        m_scanner = new FBKApiScan();
        m_scanner.setScanRssi(-120);
//        m_scanner.setScanName("HW401");
        m_scanner.setAddSystemDevice(true);
        m_scanner.startScan(DevicesScanActivity.this,m_apiScanCallBack);
    }




    /************************************************************************************
     * 方法名称：onKeyDown
     * 功能描述：重写返回方法
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            m_scanner.stopScan();
            m_scanner = null;
            m_deviceArray.clear();
            m_scanListAdapter.notifyDataSetChanged();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }




    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_scanListView = (ListView) this.findViewById(R.id.devicescan_list);
       // composeView = this.findViewById(R.id.compose_ble_scan);




        m_scanListAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_deviceArray.size();
            }


            @Override
            public Object getItem(int position) {
                return null;
            }


            @Override
            public long getItemId(int position) {
                return 0;
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = DevicesScanActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.device_list_item,null);
                }


                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText(myBleDevice.getDeviceName()+" "+myBleDevice.getDeviceRssi());


                if (myBleDevice.isBBODevice()) {
                    Log.e(TAG,"BBODevice *****"+myBleDevice.getBBQInformation().toString());
                }


//                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
//                chooseImg.setVisibility(View.INVISIBLE);


                return convertView;
            }
        };




        m_scanListView.setAdapter(m_scanListAdapter);
        m_scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_scanner.stopScan();


                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                Log.e(TAG,"setOnItemClickListener *****"+myBleDevice.getMacAddress());


                BluetoothDeviceModel deviceModel = new BluetoothDeviceModel(
                    myBleDevice.getDeviceId(),           // deviceId
                    myBleDevice.getMacAddress(), // deviceMac
                    myBleDevice.getDeviceName(), // deviceName
                    myBleDevice.getConnectStatus(),       // connectionStatus
                    myBleDevice.getDeviceType(),       // deviceType
                    myBleDevice.getBleDevice()
                );


                org.greenrobot.eventbus.EventBus.getDefault().post(deviceModel);




                Intent backIntent = new Intent();
                backIntent.putExtra("bluetooth",myBleDevice.getBleDevice());
                setResult(SCAN_ACTIVITY_BACK,backIntent);


                m_deviceArray.clear();
                m_scanListAdapter.notifyDataSetChanged();
                finish();
            }
        });
    }




    /************************************************************************************
     * 方法名称：backAction
     * 功能描述：返回
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void backAction(View view) {
        m_scanner.stopScan();
        m_scanner = null;
        m_deviceArray.clear();
        m_scanListAdapter.notifyDataSetChanged();
        finish();
    }




}
