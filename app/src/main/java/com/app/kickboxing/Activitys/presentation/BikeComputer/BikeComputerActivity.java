/****************************************************************************************
 * 文件名称：BikeComputerActivity.java
 * 内容摘要：码表
 * 版本编号：1.0.1
 * 创建日期：2019年08月22日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.BikeComputer;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
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

import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.API.BikeComputer.FBKApiBikeComputer;
import com.onecoder.fitblekit.API.BikeComputer.FBKApiBikeComputerCallBack;
import com.onecoder.fitblekit.API.BikeComputer.FBKDeleteFitType;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class BikeComputerActivity extends Activity {

    // TAG值
    private static final String TAG = BikeComputerActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int BIKECOMPUTER_TO_SCAN = 4001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiBikeComputer m_apiBikeComputer;

    // ListView
    private ListView m_bikeListView;

    // ListView BaseAdapter
    private BaseAdapter m_bikeAdapter;

    // List
    private static List<Object> m_bikeArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 选中的文件名称
    private String m_chooseFitName;

    // 选中的文件名称
    private int m_chooseRow = -1;


    // 码表回调
    private FBKApiBikeComputerCallBack m_apiBikeComputerCallBack = new FBKApiBikeComputerCallBack() {
        @Override
        public void getNameList(Object data, FBKApiBikeComputer apiBikeComputer) {
            Log.e(TAG,data.toString());
            final List<Object> dataArray = (List<Object>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<Object> cmdArray = new ArrayList<>();
                    for (int i = 0; i < 14; i++) {
                        cmdArray.add(m_bikeArray.get(i));
                    }

                    List<Object> nameArray = new ArrayList<>();
                    for (int i = 0; i < dataArray.size(); i++) {
                        nameArray.add(dataArray.get(i));
                    }

                    m_bikeArray.clear();
                    m_bikeArray.addAll(cmdArray);
                    m_bikeArray.addAll(nameArray);
                    m_bikeAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void getFitFileData(Object data, FBKApiBikeComputer apiBikeComputer) {

        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiBikeComputer.connectBluetooth( m_bluetoothDevice);
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
                    m_bikeArray.set(7,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_bikeAdapter.notifyDataSetChanged();
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
                    m_bikeArray.set(8,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_bikeAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_bikeArray.set(9,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_bikeAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_bikeArray.set(10,"Read Software Version"+"   ("+nowVersion+")");
                    m_bikeAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
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
                    Toast.makeText(BikeComputerActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BikeComputerActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BikeComputerActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_bike);

        m_apiBikeComputer = new FBKApiBikeComputer(BikeComputerActivity.this, m_apiBikeComputerCallBack);
        m_apiBikeComputer.registerBleListenerReceiver();
        m_bikeArray.clear();
        m_bikeArray.add("Set Time");
        m_bikeArray.add("Get Fit Name List");
        m_bikeArray.add("Get Fit File");
        m_bikeArray.add("Delete Fit File");
        m_bikeArray.add("Delete Fit Record");
        m_bikeArray.add("Delete Fit File And Record");
        m_bikeArray.add("Set Time Zone (28800)");
        m_bikeArray.add("Read Battery Power");
        m_bikeArray.add("Read Firmware Version");
        m_bikeArray.add("Read Hardware Version");
        m_bikeArray.add("Read Software Version");
        m_bikeArray.add("Private get version");
        m_bikeArray.add("Private get mac");
        m_bikeArray.add("Private Enter OTA Mode");
        m_bikeArray.add("Read System data");
        m_bikeArray.add("Read Model String");
        m_bikeArray.add("Read Serial Number");
        m_bikeArray.add("Read Manufacturer Name");
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
        m_apiBikeComputer.disconnectBle();
        m_apiBikeComputer.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.bike_text_status);

        m_bikeListView = (ListView) this.findViewById(R.id.bike_list);
        m_bikeAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_bikeArray.size();
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
                LayoutInflater inflater = BikeComputerActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                if (position < 14) {
                    String titleText = (String) m_bikeArray.get(position);
                    title.setText((position+1) + "、" + titleText);
                }
                else {
                    Map<String, String> resultMap = (Map<String, String>) m_bikeArray.get(position);
                    title.setText((position+1) + "、" + resultMap.get("fileName"));
                }

                if (position == m_chooseRow) {
                    title.setTextColor(Color.RED);
                }

                return convertView;
            }
        };


        m_bikeListView.setAdapter(m_bikeAdapter);
        m_bikeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiBikeComputer.setUTC(new Date());
                }
                else if (position == 1) {
                    m_apiBikeComputer.getFitNameList();
                }
                else if (position == 2) {
                    m_apiBikeComputer.getFitFile(m_chooseFitName);
                }
                else if (position == 3) {
                    m_apiBikeComputer.deleteFitFile(m_chooseFitName, FBKDeleteFitType.DeleteFitTypeFile);
                }
                else if (position == 4) {
                    m_apiBikeComputer.deleteFitFile(m_chooseFitName, FBKDeleteFitType.DeleteFitTypeRecord);
                }
                else if (position == 5) {
                    m_apiBikeComputer.deleteFitFile(m_chooseFitName, FBKDeleteFitType.DeleteFitTypeFileAndRecord);
                }
                else if (position == 6) {
                    m_apiBikeComputer.setFitTimeZone(28800);
                }
                else if (position == 7) {
                    m_apiBikeComputer.readDeviceBatteryPower();
                }
                else if (position == 8) {
                    m_apiBikeComputer.readFirmwareVersion();
                }
                else if (position == 9) {
                    m_apiBikeComputer.readHardwareVersion();
                }
                else if (position == 10) {
                    m_apiBikeComputer.readSoftwareVersion();
                }
                else if (position == 11) {
                    m_apiBikeComputer.getPrivateVersion();
                }
                else if (position == 12) {
                    m_apiBikeComputer.getPrivateMacAddress();
                }
                else if (position == 13) {
                    m_apiBikeComputer.enterOTAMode();
                }
                else if (position == 14) {
                    m_apiBikeComputer.readSystemId();
                }
                else if (position == 15) {
                    m_apiBikeComputer.readModelString();
                }
                else if (position == 16) {
                    m_apiBikeComputer.readSerialNumber();
                }
                else if (position == 17) {
                    m_apiBikeComputer.readManufacturerName();
                }
                else {
                    Map<String, String> resultMap = (Map<String, String>) m_bikeArray.get(position);
                    m_chooseFitName = resultMap.get("fileName");
                    m_chooseRow = position;
                    m_bikeAdapter.notifyDataSetChanged();
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
        Intent intent = new Intent(BikeComputerActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,BIKECOMPUTER_TO_SCAN);
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
        if (requestCode == BIKECOMPUTER_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiBikeComputer.connectBluetooth(m_bluetoothDevice);
        }
    }


}
