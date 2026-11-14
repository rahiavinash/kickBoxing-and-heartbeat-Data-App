/****************************************************************************************
 * 文件名称：SkippingActivity.java
 * 内容摘要：跳绳
 * 版本编号：1.0.1
 * 创建日期：2019年09月26日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.Skipping;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.Cadence.CadenceActivity;
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Skipping.FBKApiSkipping;
import com.onecoder.fitblekit.API.Skipping.FBKApiSkippingCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.app.kickboxing.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkippingActivity extends Activity {

    // TAG值
    private static final String TAG = CadenceActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int CADENCE_TO_SCAN = 11001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiSkipping m_apiSkipping;

    // ListView
    private ListView m_skippingListView;

    // ListView BaseAdapter
    private BaseAdapter m_skippingAdapter;

    // List
    private static List<String> m_skippingArray = new ArrayList<>();

    // 跳绳
    private Map<String, String> m_skipMap = new HashMap();

    // 连接状态
    private TextView m_statusText;

    // 速度
    private TextView m_speedText;

    // 跳绳回调
    private FBKApiSkippingCallBack m_apiSkippingCallBack = new FBKApiSkippingCallBack() {
        @Override
        public void getSkipData(Object data, FBKApiSkipping apiSkipping) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getUiData(m_skipMap, resultMap);
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,errorString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectStatus(FBKBleDeviceStatus connectStatus, FBKApiBsaeMethod apiBsaeMethod) {
            final FBKBleDeviceStatus status = connectStatus;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (status == FBKBleDeviceStatus.BleConnecting) {
                        m_statusText.setText("Connecting");
                    }
                    else if (status == FBKBleDeviceStatus.BleConnected) {
                        m_statusText.setText("Connected");
                    }
                    else if (status == FBKBleDeviceStatus.Blesynchronizing) {
                        m_statusText.setText("Synchronizing");
                    }
                    else if (status == FBKBleDeviceStatus.BleSyncOver) {
                        m_statusText.setText("Syn Over");
                    }
                    else if (status == FBKBleDeviceStatus.BleReconnect) {
                        m_statusText.setText("Reconnecting");
                    }
                    else if (status == FBKBleDeviceStatus.BleDisconnected) {
                        m_statusText.setText("Disconnected");
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOn) {
                        m_statusText.setText("BleTurnOn");
                        if (m_bluetoothDevice != null) {
                            m_apiSkipping.connectBluetooth( m_bluetoothDevice);
                        }
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOff) {
                        m_statusText.setText("BleTurnOff");
                    }
                }
            });
        }

        @Override
        public void bleConnectStatusLog(String infoString, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void batteryPower(final int power, FBKApiBsaeMethod apiBsaeMethod) {
            final int batteryPower = power;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_skippingArray.set(0,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_skippingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_skippingArray.set(1,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_skippingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_skippingArray.set(2,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_skippingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_skippingArray.set(3,"Read Software Version"+"   ("+nowVersion+")");
                    m_skippingAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectInfo(String infoString, FBKApiBsaeMethod apiBsaeMethod) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
            Date date = new Date();
            String timeString = dateFormat.format(date);
            Log.e(TAG,timeString+" --- bleConnectInfo --- "+infoString);
        }

        @Override
        public void deviceSystemData(byte[] systemData, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SkippingActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SkippingActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_skipping);

        m_apiSkipping = new FBKApiSkipping(SkippingActivity.this, m_apiSkippingCallBack);
        m_apiSkipping.registerBleListenerReceiver();
        m_skippingArray.clear();
        m_skippingArray.add("Read Battery Power");
        m_skippingArray.add("Read Firmware Version");
        m_skippingArray.add("Read Hardware Version");
        m_skippingArray.add("Read Software Version");
        m_skippingArray.add("Private get version");
        m_skippingArray.add("Private get mac");
        m_skippingArray.add("Private Enter OTA Mode");
        m_skippingArray.add("Read System data");
        m_skippingArray.add("Read Model String");
        m_skippingArray.add("Read Serial Number");
        m_skippingArray.add("Read Manufacturer Name");
        initView();
    }


    /************************************************************************************
     * 方法名称：onDestroy
     * 功能描述：销毁页面
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_apiSkipping.disconnectBle();
        m_apiSkipping.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.skip_text_status);
        m_speedText = (TextView) this.findViewById(R.id.skip_text_speed);

        m_skippingListView = (ListView) this.findViewById(R.id.skip_list);
        m_skippingAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_skippingArray.size();
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
                LayoutInflater inflater = SkippingActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_skippingArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_skippingListView.setAdapter(m_skippingAdapter);
        m_skippingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiSkipping.readDeviceBatteryPower();
                }
                else if (position == 1) {
                    m_apiSkipping.readFirmwareVersion();
                }
                else if (position == 2) {
                    m_apiSkipping.readHardwareVersion();
                }
                else if (position == 3) {
                    m_apiSkipping.readSoftwareVersion();
                }
                else if (position == 4) {
                    m_apiSkipping.getPrivateVersion();
                }
                else if (position == 5) {
                    m_apiSkipping.getPrivateMacAddress();
                }
                else if (position == 6) {
                    m_apiSkipping.enterOTAMode();
                }
                else if (position == 7) {
                    m_apiSkipping.readSystemId();
                }
                else if (position == 8) {
                    m_apiSkipping.readModelString();
                }
                else if (position == 9) {
                    m_apiSkipping.readSerialNumber();
                }
                else if (position == 10) {
                    m_apiSkipping.readManufacturerName();
                }
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
        finish();
    }


    /************************************************************************************
     * 方法名称：deviceAction
     * 功能描述：选择设备
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void deviceAction(View view) {
        Intent intent = new Intent(SkippingActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,CADENCE_TO_SCAN);
    }


    /************************************************************************************
     * 方法名称：getUiData
     * 功能描述：
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void getUiData(Map<String,String> beforeMap, Map<String,String> nowMap) {
        if (beforeMap.keySet().size() > 0) {
            if (beforeMap.containsKey("skipCount")) {
                double skipCount = Double.parseDouble(beforeMap.get("skipCount"));
                double skipTime = Double.parseDouble(beforeMap.get("skipTime"));

                double skipCountNow = Double.parseDouble(nowMap.get("skipCount"));
                double skipTimeNow = Double.parseDouble(nowMap.get("skipTime"));

                double mySkipCount = doubleOverflow(skipCountNow-skipCount,4);
                double mySkipTime = doubleOverflow(skipTimeNow-skipTime,2);

                double speedNum = 0;
                if (mySkipTime != 0) {
                    speedNum = mySkipCount / (mySkipTime/1024) * 60; // 次/分钟
                }

                m_speedText.setText("   Speed: "+(int)speedNum+" times/minute");
            }
        }

        m_skipMap.clear();
        m_skipMap = nowMap;
    }


    /************************************************************************************
     * 方法名称：doubleOverflow
     * 功能描述：字节最大值溢出处理
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public double doubleOverflow(double value, int bitNumber) {
        double hiNum = 0;
        double resultNum = value;

        if (bitNumber == 4) {
            int tww = 255;
            hiNum = (tww<<24)+(tww<<16)+(tww<<8)+tww;
        }
        else if (bitNumber == 2) {
            hiNum = 65535;
        }

        if (value < 0) {
            resultNum = value+hiNum;
        }

        return resultNum;
    }


    /************************************************************************************
     * 方法名称：onActivityResult
     * 功能描述：
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CADENCE_TO_SCAN && resultCode == DevicesScanActivity.SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiSkipping.connectBluetooth(m_bluetoothDevice);
        }
    }

}
