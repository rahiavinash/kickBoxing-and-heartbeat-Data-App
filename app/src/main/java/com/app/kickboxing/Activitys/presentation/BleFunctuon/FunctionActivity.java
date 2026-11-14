package com.app.kickboxing.Activitys.presentation.BleFunctuon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.API.BleFunction.FBKBleFunction;
import com.onecoder.fitblekit.API.BleFunction.FBKBleFunctionCallBack;
import com.onecoder.fitblekit.API.BleFunction.FBKFunction;
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScan;
import com.onecoder.fitblekit.API.ScanDevices.FBKApiScanCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDevice;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class FunctionActivity extends Activity {

    // TAG值
    private static final String TAG = DevicesScanActivity.class.getSimpleName();
    private FBKApiScan m_scanner;
    private ListView m_scanListView;
    private FBKBleFunction m_bleFunction;
    private BaseAdapter m_scanListAdapter;
    private static List<FBKBleDevice> m_deviceArray = new ArrayList<>();
    private int m_cellNumber;
    private String m_deviceName = "";


    /************************************************************************************
     * 方法名称：FBKApiScanCallBack
     * 功能描述：扫描回调
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private FBKApiScanCallBack m_apiScanCallBack = new FBKApiScanCallBack() {
        @Override
        public void bleScanResult(List<FBKBleDevice> deviceArray, FBKApiScan apiScan) {
            m_deviceArray = deviceArray;
            m_scanListAdapter.notifyDataSetChanged();
        }

        @Override
        public void bleScanAvailable(Boolean isAvailable, FBKApiScan apiScan) {

        }
    };


    private FBKBleFunctionCallBack m_bleFunctionCallBack = new FBKBleFunctionCallBack() {
        @Override
        public void deviceFunction(FBKFunction function, FBKBleFunction bleFunction) {
            String typeString = "TYPE: "+function.getFunctionType();
            String recordString = "HaveRecord: "+function.isHaveRecord();
            String hrString = "IsHR: "+function.isHR();
            String hrvString = "IsHRV: "+function.isHRV();
            String ecgString = "IsECG: "+function.isECG();
            final String alertString = typeString+"\n"+recordString+"\n"+hrString+"\n"+hrvString+"\n"+ecgString;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showDialog(alertString);
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void bleConnectStatus(FBKBleDeviceStatus connectStatus, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void bleConnectStatusLog(String infoString, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void batteryPower(int power, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void bleConnectInfo(String infoString, FBKApiBsaeMethod apiBsaeMethod) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String timeString = dateFormat.format(date);
            Log.e(TAG,timeString+" --- bleConnectInfo --- "+infoString);
        }

        @Override
        public void deviceSystemData(byte[] systemData, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FunctionActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FunctionActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FunctionActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(FunctionActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceBaseInfo(FBKBleBaseInfo baseInfo, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String showString = "";
                    showString = showString+"battery: "+baseInfo.getBattery()+"\n";
                    showString = showString+"firmVersion: "+baseInfo.getFirmVersion()+"\n";
                    showString = showString+"hardVersion: "+baseInfo.getHardVersion()+"\n";
                    showString = showString+"softVersion: "+baseInfo.getSoftVersion()+"\n";
                    showString = showString+"systemId: "+ FBKSpliceBle.bytesToHexString(baseInfo.getSystemId())+"\n";
                    showString = showString+"modelString: "+baseInfo.getModelString()+"\n";
                    showString = showString+"serialNumber: "+baseInfo.getSerialNumber()+"\n";
                    showString = showString+"manufacturer: "+baseInfo.getManufacturerName()+"\n";
                    showString = showString+"customer: "+baseInfo.getCustomerName()+"\n";
                    showString = showString+"macAddress: "+baseInfo.getDeviceMac()+"\n";
                    showString = showString+"OTAMac: "+baseInfo.getDfuMac()+"\n";
                    Toast.makeText(FunctionActivity.this,showString,Toast.LENGTH_SHORT).show();
                }
            });
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
        setContentView(R.layout.activity_devicescan);

        m_cellNumber = -1;
        initView();

        m_scanner = new FBKApiScan();
        m_scanner.setScanRssi(-70);
        m_scanner.startScan(FunctionActivity.this,m_apiScanCallBack);

        m_bleFunction = new FBKBleFunction(FunctionActivity.this, m_bleFunctionCallBack);
        m_bleFunction.registerBleListenerReceiver();
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
            m_bleFunction.disconnectBle();
            m_scanner.stopScan();
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
                LayoutInflater inflater = FunctionActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText(myBleDevice.getDeviceName()+" "+myBleDevice.getDeviceRssi());

                if (myBleDevice.isBBODevice()) {
                    Log.e(TAG,"BBODevice *****"+myBleDevice.getBBQInformation().toString());
                }

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                if (m_cellNumber == position) {
                    chooseImg.setImageResource(R.mipmap.img_choose);
                    chooseImg.setVisibility(View.VISIBLE);
                }

                return convertView;
            }
        };


        m_scanListView.setAdapter(m_scanListAdapter);
        m_scanListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_scanner.stopScan();

                m_cellNumber = position;
                m_scanListAdapter.notifyDataSetChanged();

                FBKBleDevice myBleDevice = m_deviceArray.get(position);
                m_deviceName = myBleDevice.getDeviceName();
                m_bleFunction.connectBluetooth(myBleDevice.getBleDevice());
                Log.e(TAG,"setOnItemClickListener *****"+myBleDevice.getMacAddress());
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
        m_bleFunction.disconnectBle();
        m_scanner.stopScan();
        m_deviceArray.clear();
        m_scanListAdapter.notifyDataSetChanged();
        finish();
    }


    private void showDialog(String alertString) {
        AlertDialog.Builder bulder = new AlertDialog.Builder(FunctionActivity.this);
        bulder.setCancelable(false);
        bulder.setTitle(m_deviceName);
        bulder.setMessage(alertString);
        bulder.setNegativeButton("Confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        bulder.show();
    }


}
