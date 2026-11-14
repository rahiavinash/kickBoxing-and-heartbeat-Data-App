/****************************************************************************************
 * 文件名称：KettleBellActivity.java
 * 内容摘要：壶铃
 * 版本编号：1.0.1
 * 创建日期：2019年08月22日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.KettleBell;

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
import com.onecoder.fitblekit.API.KettleBell.FBKApiKettleBell;
import com.onecoder.fitblekit.API.KettleBell.FBKApiKettleBellCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class KettleBellActivity extends Activity {

    // TAG值
    private static final String TAG = KettleBellActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int KETTLEBELL_TO_SCAN = 6001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiKettleBell m_apiKettleBell;

    // ListView
    private ListView m_kettleBellListView;

    // ListView BaseAdapter
    private BaseAdapter m_kettleBellAdapter;

    // List
    private static List<String> m_kettleBellArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 壶铃回调
    private FBKApiKettleBellCallBack m_apiKettleBellCallBack = new FBKApiKettleBellCallBack() {
        @Override
        public void kettleBellRecord(Object data, FBKApiKettleBell apiKettleBell) {
            final Map<String, Object> resultDic = (Map<String, Object>) data;
            final List<Object> resultList = (List<Object>) resultDic.get("kettleBellRecord");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultList != null) {
                        Log.e(TAG,resultList.toString());
                        Intent intent = new Intent(KettleBellActivity.this, BellListActivity.class);
                        intent.putExtra("data",(Serializable)resultList);
                        startActivity(intent);
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
                    Toast.makeText(KettleBellActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiKettleBell.connectBluetooth( m_bluetoothDevice);
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
                    m_kettleBellArray.set(2,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_kettleBellAdapter.notifyDataSetChanged();
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
                    m_kettleBellArray.set(3,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_kettleBellAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_kettleBellArray.set(4,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_kettleBellAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_kettleBellArray.set(5,"Read Software Version"+"   ("+nowVersion+")");
                    m_kettleBellAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KettleBellActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KettleBellActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(KettleBellActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KettleBellActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KettleBellActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(KettleBellActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(KettleBellActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_kettlebell);

        m_apiKettleBell = new FBKApiKettleBell(KettleBellActivity.this, m_apiKettleBellCallBack);
        m_apiKettleBell.registerBleListenerReceiver();
        m_kettleBellArray.clear();
        m_kettleBellArray.add("Set Time");
        m_kettleBellArray.add("Get Record");
        m_kettleBellArray.add("Read Battery Power");
        m_kettleBellArray.add("Read Firmware Version");
        m_kettleBellArray.add("Read Hardware Version");
        m_kettleBellArray.add("Read Software Version");
        m_kettleBellArray.add("Private get version");
        m_kettleBellArray.add("Private get mac");
        m_kettleBellArray.add("Private Enter OTA Mode");
        m_kettleBellArray.add("Read System data");
        m_kettleBellArray.add("Read Model String");
        m_kettleBellArray.add("Read Serial Number");
        m_kettleBellArray.add("Read Manufacturer Name");
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
        m_apiKettleBell.disconnectBle();
        m_apiKettleBell.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.kettle_text_status);

        m_kettleBellListView = (ListView) this.findViewById(R.id.kettle_list);
        m_kettleBellAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_kettleBellArray.size();
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
                LayoutInflater inflater = KettleBellActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_kettleBellArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_kettleBellListView.setAdapter(m_kettleBellAdapter);
        m_kettleBellListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiKettleBell.setUTC(new Date());
                }
                else if (position == 1) {
                    m_apiKettleBell.getKettleBellRecord();
                }
                else if (position == 2) {
                    m_apiKettleBell.readDeviceBatteryPower();
                }
                else if (position == 3) {
                    m_apiKettleBell.readFirmwareVersion();
                }
                else if (position == 4) {
                    m_apiKettleBell.readHardwareVersion();
                }
                else if (position == 5) {
                    m_apiKettleBell.readSoftwareVersion();
                }
                else if (position == 6) {
                    m_apiKettleBell.getPrivateVersion();
                }
                else if (position == 7) {
                    m_apiKettleBell.getPrivateMacAddress();
                }
                else if (position == 8) {
                    m_apiKettleBell.enterOTAMode();
                }
                else if (position == 9) {
                    m_apiKettleBell.readSystemId();
                }
                else if (position == 10) {
                    m_apiKettleBell.readModelString();
                }
                else if (position == 11) {
                    m_apiKettleBell.readSerialNumber();
                }
                else if (position == 12) {
                    m_apiKettleBell.readManufacturerName();
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
        Intent intent = new Intent(KettleBellActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,KETTLEBELL_TO_SCAN);
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
        if (requestCode == KETTLEBELL_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiKettleBell.connectBluetooth(m_bluetoothDevice);
        }
    }


}
