package com.app.kickboxing.Activitys.presentation.Running;

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

import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.API.Running.FBKApiRun;
import com.onecoder.fitblekit.API.Running.FBKApiRunCallBack;
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

public class RunningActivity extends Activity {

    // TAG值
    private static final String TAG = RunningActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int RUNNING_TO_SCAN = 20003;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 设备API
    private FBKApiRun m_apiRunning;

    // ListView
    private ListView m_runListView;

    // ListView BaseAdapter
    private BaseAdapter m_runAdapter;

    // List
    private static List<String> m_runArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 速度
    private TextView m_speedText;

    // 踏频
    private TextView m_cadenceText;

    // 步距
    private TextView m_longText;

    // 距离
    private TextView m_diastanceText;

    // 运动状态
    private TextView m_runStatusText;

    // 踏频回调
    private FBKApiRunCallBack m_apiRunCallBack = new FBKApiRunCallBack() {

        @Override
        public void realTimeRunning(Map<String, String> runMap, FBKApiRun apiRun) {
            Log.e(TAG,"realTimeRunning --- "+runMap);
            final Map<String, String> dataMap = runMap;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_speedText.setText("   speed:"+runMap.get("speed") + " km/h");
                    m_cadenceText.setText("   cadence:"+runMap.get("cadence") + " rpm");
                    m_longText.setText("   stride:"+runMap.get("stride") + " m");
                    m_diastanceText.setText("   distance:"+runMap.get("distance") + " m");

                    String runStatus = runMap.get("runStatus");
                    m_runStatusText.setText("   status: stop");
                    if (runStatus.equals("1")) {
                        m_runStatusText.setText("   status: running");
                    }
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiRunning.connectBluetooth( m_bluetoothDevice);
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
            if (m_apiRunning == apiBsaeMethod) {
                Log.e(TAG,"batteryPower");
            }
            final int batteryPower = power;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_runArray.set(0,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_runAdapter.notifyDataSetChanged();
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
                    m_runArray.set(1,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_runAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_runArray.set(2,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_runAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_runArray.set(3,"Read Software Version"+"   ("+nowVersion+")");
                    m_runAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RunningActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RunningActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RunningActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_running);

        m_apiRunning = new FBKApiRun(RunningActivity.this, m_apiRunCallBack);
        m_apiRunning.registerBleListenerReceiver();
        m_runArray.clear();
        m_runArray.add("Read Battery Power");
        m_runArray.add("Read Firmware Version");
        m_runArray.add("Read Hardware Version");
        m_runArray.add("Read Software Version");
        m_runArray.add("Private get version");
        m_runArray.add("Private get mac");
        m_runArray.add("Private Enter OTA Mode");
        m_runArray.add("Read System data");
        m_runArray.add("Read Model String");
        m_runArray.add("Read Serial Number");
        m_runArray.add("Read Manufacturer Name");
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
        m_apiRunning.disconnectBle();
        m_apiRunning.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.run_text_status);
        m_speedText = (TextView) this.findViewById(R.id.run_text_speed);
        m_cadenceText = (TextView) this.findViewById(R.id.run_text_cadence);
        m_longText = (TextView) this.findViewById(R.id.run_text_length);
        m_diastanceText = (TextView) this.findViewById(R.id.run_text_distance);
        m_runStatusText = (TextView) this.findViewById(R.id.run_text_runstatus);

        m_runListView = (ListView) this.findViewById(R.id.run_list);
        m_runAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_runArray.size();
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
                LayoutInflater inflater = RunningActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_runArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_runListView.setAdapter(m_runAdapter);
        m_runListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiRunning.readDeviceBatteryPower();
                }
                else if (position == 1) {
                    m_apiRunning.readFirmwareVersion();
                }
                else if (position == 2) {
                    m_apiRunning.readHardwareVersion();
                }
                else if (position == 3) {
                    m_apiRunning.readSoftwareVersion();
                }
                else if (position == 4) {
                    m_apiRunning.getPrivateVersion();
                }
                else if (position == 5) {
                    m_apiRunning.getPrivateMacAddress();
                }
                else if (position == 6) {
                    m_apiRunning.enterOTAMode();
                }
                else if (position == 7) {
                    m_apiRunning.readSystemId();
                }
                else if (position == 8) {
                    m_apiRunning.readModelString();
                }
                else if (position == 9) {
                    m_apiRunning.readSerialNumber();
                }
                else if (position == 10) {
                    m_apiRunning.readManufacturerName();
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
        Intent intent = new Intent(RunningActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,RUNNING_TO_SCAN);
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
        if (requestCode == RUNNING_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiRunning.connectBluetooth(m_bluetoothDevice);
        }
    }

}
