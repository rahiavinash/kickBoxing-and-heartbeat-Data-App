/****************************************************************************************
 * 文件名称：HeartRateActivity.java
 * 内容摘要：旧协议心率带
 * 版本编号：1.0.1
 * 创建日期：2019年09月27日
 * **************************************************************************************/

package com.app.kickboxing.Activitys.presentation.HeartRate;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.app.kickboxing.Activitys.domain.response.BluetoothDeviceModel;
import com.app.kickboxing.Activitys.domain.response.HeartRateValue;
import com.app.kickboxing.Activitys.presentation.NewTracker.RecordActivity;
import com.hb.dialog.myDialog.MyAlertInputDialog;
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRate;
import com.onecoder.fitblekit.API.HeartRate.FBKApiHeartRateCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.NewTracker.ModelStorage;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class HeartRateActivity extends Activity {

    // TAG值
    private static final String TAG = HeartRateActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int HEARTRATE_TO_SCAN = 12001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 设备API
    private FBKApiHeartRate m_apiHeartRate;

    // ListView
    private ListView m_heartListView;

    // ListView BaseAdapter
    private BaseAdapter m_heartAdapter;

    // List
    private static List<String> m_heartArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 心率
    private TextView m_heartText;

    private MyAlertInputDialog m_inputDialog;


    // 数据回调
    private FBKApiHeartRateCallBack m_apiHeartRateCallBack = new FBKApiHeartRateCallBack() {
        @Override
        public void realTimeHeartRate(Object data, FBKApiHeartRate apiHeartRate) {
            Map<String, Object> resultMap = (Map<String, Object>) data;
            String heartRateStr = (String) resultMap.get("heartRate");
            final int heartRate = Integer.parseInt(heartRateStr);


            HeartRateValue heartRateStat = new HeartRateValue(
                    heartRate
            );

            org.greenrobot.eventbus.EventBus.getDefault().post(heartRateStat);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartText.setText("   HeartRate: "+heartRate);
                }
            });
        }

        @Override
        public void heartRateRecord(Object data, FBKApiHeartRate apiHeartRate) {
            final Map<String, Object> resultMap = (Map<String, Object>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultMap.keySet().size() > 0) {
                        ModelStorage.getInstance().putModel(resultMap);
                        Intent intent = new Intent(HeartRateActivity.this, RecordActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void HRHrvResultData(boolean status, Object data, FBKApiHeartRate apiHeartRate) {
            Log.e(TAG,"HRHrvResult:"+status+"---"+data.toString());
        }

        @Override
        public void setHRShockStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Set Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void getHRShockStatus(Object data, FBKApiHeartRate apiHeartRate) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            final String number = resultMap.get("shockNumber");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Get Shock"+number,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void closeHRShockStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Close Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setHRMaxIntervalStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Set Max Interval succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setHRLightSwitchStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Set Light Switch succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setHRColorShockStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Set Color Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setHRColorIntervalStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Set Color Interval succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void clearHRRecordStatus(boolean status, FBKApiHeartRate apiHeartRate) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this, "Clear Record succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
//                        setHRInterval();
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
                            m_apiHeartRate.connectBluetooth( m_bluetoothDevice);
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
        public void batteryPower(int power, FBKApiBsaeMethod apiBsaeMethod) {
            final int batteryPower = power;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartArray.set(11,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_heartAdapter.notifyDataSetChanged();
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
                    m_heartArray.set(12,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_heartAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartArray.set(13,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_heartAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartArray.set(14,"Read Software Version"+"   ("+nowVersion+")");
                    m_heartAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HeartRateActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HeartRateActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HeartRateActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_heartrate);

        m_apiHeartRate = new FBKApiHeartRate(HeartRateActivity.this, m_apiHeartRateCallBack);
        m_apiHeartRate.registerBleListenerReceiver();
        m_heartArray.clear();
        m_heartArray.add("Get Record");
        m_heartArray.add("Set Shock (80)");
        m_heartArray.add("Get Shock");
        m_heartArray.add("Close Clock");
        m_heartArray.add("Set max heart rate interval (166)");
        m_heartArray.add("Set light switch (on)");
        m_heartArray.add("Set light switch (off)");
        m_heartArray.add("Set color shock (on)");
        m_heartArray.add("Set color shock (off)");
        m_heartArray.add("Set color interval (180)");
        m_heartArray.add("Clear Record");
        m_heartArray.add("Read Battery Power");
        m_heartArray.add("Read Firmware Version");
        m_heartArray.add("Read Hardware Version");
        m_heartArray.add("Read Software Version");
        m_heartArray.add("Reset Name");
        m_heartArray.add("Private get version");
        m_heartArray.add("Private get mac");
        m_heartArray.add("Private Enter OTA Mode");
        m_heartArray.add("Enter HRV Mode");
        m_heartArray.add("Exit HRV Mode");
        m_heartArray.add("Read System data");
        m_heartArray.add("Read Model String");
        m_heartArray.add("Read Serial Number");
        m_heartArray.add("Read Manufacturer Name");
        initView();
        loadDialog();
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
        m_apiHeartRate.disconnectBle();
        m_apiHeartRate.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：setHRInterval
     * 功能描述：setHRInterval
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void setHRInterval() {
        m_apiHeartRate.setMaxInterval(166);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                m_apiHeartRate.setShock(166);
            }
        }, 1000);

    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.heart_text_status);
        m_heartText = (TextView) this.findViewById(R.id.heart_text_heart);

        m_heartListView = (ListView) this.findViewById(R.id.heart_list);
        m_heartAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_heartArray.size();
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
                LayoutInflater inflater = HeartRateActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_heartArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_heartListView.setAdapter(m_heartAdapter);
        m_heartListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiHeartRate.getHeartRateRecord();
                }
                else if (position == 1) {
                    m_apiHeartRate.setShock(80);
                }
                else if (position == 2) {
                    m_apiHeartRate.getShock();
                }
                else if (position == 3) {
                    m_apiHeartRate.closeShock();
                }
                else if (position == 4) {
//                    setHRInterval();
                    m_apiHeartRate.setMaxInterval(166);
                }
                else if (position == 5) {
                    m_apiHeartRate.setLightSwitch(true);
                }
                else if (position == 6) {
                    m_apiHeartRate.setLightSwitch(false);
                }
                else if (position == 7) {
                    m_apiHeartRate.setColorShock(true);
                }
                else if (position == 8) {
                    m_apiHeartRate.setColorShock(false);
                }
                else if (position == 9) {
                    m_apiHeartRate.setColorInterval(180);
                }
                else if (position == 10) {
                    m_apiHeartRate.clearRecord();
                }
                else if (position == 11) {
                    m_apiHeartRate.readDeviceBatteryPower();
                }
                else if (position == 12) {
                    m_apiHeartRate.readFirmwareVersion();
                }
                else if (position == 13) {
                    m_apiHeartRate.readHardwareVersion();
                }
                else if (position == 14) {
                    m_apiHeartRate.readSoftwareVersion();
                }
                else if (position == 15) {
                    m_inputDialog.show();
                }
                else if (position == 16) {
                    m_apiHeartRate.getPrivateVersion();
                }
                else if (position == 17) {
                    m_apiHeartRate.getPrivateMacAddress();
                }
                else if (position == 18) {
                    showSystemDialog("Device will enter OTA Mode?",0);
                }
                else if (position == 19) {
                    m_apiHeartRate.enterHRVMode(true);
                }
                else if (position == 20) {
                    m_apiHeartRate.enterHRVMode(false);
                }
                else if (position == 21) {
                    m_apiHeartRate.readSystemId();
                }
                else if (position == 22) {
                    m_apiHeartRate.readModelString();
                }
                else if (position == 23) {
                    m_apiHeartRate.readSerialNumber();
                }
                else if (position == 24) {
                    m_apiHeartRate.readManufacturerName();
                }
            }
        });
    }


    private void showSystemDialog(String alertString, int tag) {
        AlertDialog.Builder bulder = new AlertDialog.Builder(HeartRateActivity.this);
        bulder.setCancelable(false);
        bulder.setTitle("Alert");
        bulder.setMessage(alertString);
        bulder.setNegativeButton(getString(R.string.cancel),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        bulder.setPositiveButton(getString(R.string.confirm),new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_apiHeartRate.enterOTAMode();
            }
        });

        bulder.show();
    }


    /*************************************************************************************
     * Method: loadDialog
     * Description: load EditText Dialog
     * Parameter:
     * Return Data:
     *************************************************************************************/
    private void loadDialog() {
        m_inputDialog = new MyAlertInputDialog(this).builder()
                .setTitle("New Name")
                .setEditText("请勿输入中文名称，否则乱码");
        m_inputDialog.setPositiveButton("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameString = m_inputDialog.getResult();
                if (nameString.length() > 0) {
                    m_apiHeartRate.resetDeviceName(nameString);
                }
                m_inputDialog.dismiss();
            }
        }).setNegativeButton("Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_inputDialog.dismiss();
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
        Intent intent = new Intent(HeartRateActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,HEARTRATE_TO_SCAN);
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
                if (requestCode == HEARTRATE_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
                    Log.e(TAG,"onActivityResult");
                    m_bluetoothDevice = data.getParcelableExtra("bluetooth");
                    m_apiHeartRate.connectBluetooth(m_bluetoothDevice.getAddress());       }
    }

}
