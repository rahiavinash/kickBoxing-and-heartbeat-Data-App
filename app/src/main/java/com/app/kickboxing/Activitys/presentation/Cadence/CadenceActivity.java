/****************************************************************************************
 * 文件名称：CadenceActivity.java
 * 内容摘要：踏频
 * 版本编号：1.0.1
 * 创建日期：2019年09月26日
 * **************************************************************************************/

package com.app.kickboxing.Activitys.presentation.Cadence;

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
import com.onecoder.fitblekit.API.Cadence.FBKApiCadence;
import com.onecoder.fitblekit.API.Cadence.FBKApiCadenceCallBack;
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

public class CadenceActivity extends Activity {

    // TAG值
    private static final String TAG = CadenceActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int CADENCE_TO_SCAN = 9001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 设备API
    private FBKApiCadence m_apiCadence;

    // ListView
    private ListView m_cadenceListView;

    // ListView BaseAdapter
    private BaseAdapter m_cadenceAdapter;

    // List
    private static List<String> m_cadenceArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 速度
    private TextView m_speedText;

    // 踏频
    private TextView m_cadenceText;

    // 踏频回调
    private FBKApiCadenceCallBack m_apiCadenceCallBack = new FBKApiCadenceCallBack() {

        @Override
        public void getCadence(int cadence, FBKApiCadence apiCadence) {
            final int myCadence = cadence;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_cadenceText.setText(myCadence+" t/min");
                }
            });
        }

        @Override
        public void getSpeed(double speed, double distance, FBKApiCadence apiCadence) {
            final double mySpeed = speed;
            final double myDistance = distance;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_speedText.setText(mySpeed+"m/s"+"   "+myDistance+"m");
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiCadence.connectBluetooth( m_bluetoothDevice);
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
            if (m_apiCadence == apiBsaeMethod) {
                Log.e(TAG,"batteryPower");
            }
            final int batteryPower = power;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_cadenceArray.set(0,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_cadenceAdapter.notifyDataSetChanged();
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
                    m_cadenceArray.set(1,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_cadenceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_cadenceArray.set(2,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_cadenceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_cadenceArray.set(3,"Read Software Version"+"   ("+nowVersion+")");
                    m_cadenceAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CadenceActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CadenceActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CadenceActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_cadence);

        m_apiCadence = new FBKApiCadence(CadenceActivity.this, m_apiCadenceCallBack);
        m_apiCadence.registerBleListenerReceiver();

        // 需要速度数据时必须设置该参数，默认按2.096米为自行车的周长计算速度
        m_apiCadence.setWhellDiameter(2.096);

        m_cadenceArray.clear();
        m_cadenceArray.add("Read Battery Power");
        m_cadenceArray.add("Read Firmware Version");
        m_cadenceArray.add("Read Hardware Version");
        m_cadenceArray.add("Read Software Version");
        m_cadenceArray.add("Private get version");
        m_cadenceArray.add("Private get mac");
        m_cadenceArray.add("Private Enter OTA Mode");
        m_cadenceArray.add("Read System data");
        m_cadenceArray.add("Read Model String");
        m_cadenceArray.add("Read Serial Number");
        m_cadenceArray.add("Read Manufacturer Name");
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
        m_apiCadence.disconnectBle();
        m_apiCadence.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.cadence_text_status);
        m_speedText = (TextView) this.findViewById(R.id.cadence_text_speed);
        m_cadenceText = (TextView) this.findViewById(R.id.cadence_text_cadence);

        m_cadenceListView = (ListView) this.findViewById(R.id.cadence_list);
        m_cadenceAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_cadenceArray.size();
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
                LayoutInflater inflater = CadenceActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_cadenceArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_cadenceListView.setAdapter(m_cadenceAdapter);
        m_cadenceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiCadence.readDeviceBatteryPower();
                }
                else if (position == 1) {
                    m_apiCadence.readFirmwareVersion();
                }
                else if (position == 2) {
                    m_apiCadence.readHardwareVersion();
                }
                else if (position == 3) {
                    m_apiCadence.readSoftwareVersion();
                }
                else if (position == 4) {
                    m_apiCadence.getPrivateVersion();
                }
                else if (position == 5) {
                    m_apiCadence.getPrivateMacAddress();
                }
                else if (position == 6) {
                    m_apiCadence.enterOTAMode();
                }
                else if (position == 7) {
                    m_apiCadence.readSystemId();
                }
                else if (position == 8) {
                    m_apiCadence.readModelString();
                }
                else if (position == 9) {
                    m_apiCadence.readSerialNumber();
                }
                else if (position == 10) {
                    m_apiCadence.readManufacturerName();
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
        Intent intent = new Intent(CadenceActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,CADENCE_TO_SCAN);
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
        if (requestCode == CADENCE_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiCadence.connectBluetooth(m_bluetoothDevice);
        }
    }

}
