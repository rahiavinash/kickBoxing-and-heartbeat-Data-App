/****************************************************************************************
 * 文件名称：ArmBandActivity.java
 * 内容摘要：臂带
 * 版本编号：1.0.1
 * 创建日期：2019年08月22日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.ArmBand;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onecoder.fitblekit.API.ArmBand.FBKApiArmBand;
import com.onecoder.fitblekit.API.ArmBand.FBKApiArmBandCallBack;
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDevice;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Ble.FBKBleScan.FBKBleScanCallBack;
import com.onecoder.fitblekit.Ble.FBKBleScan.FBKNewBleScan;
import com.onecoder.fitblekit.Protocol.ArmBand.FBKArmBandPrivate;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaAcceleration;
import com.onecoder.fitblekit.Tools.FBKHRRecordAnaly;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.NewTracker.ModelStorage;
import com.app.kickboxing.Activitys.presentation.NewTracker.RecordActivity;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class ArmBandActivity extends Activity {

    // TAG值
    private static final String TAG = ArmBandActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int ARMBAND_TO_SCAN = 5001;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiArmBand m_apiArmBand;

    // ListView
    private ListView m_armBandListView;

    // ListView BaseAdapter
    private BaseAdapter m_armBandAdapter;

    // List
    private static List<String> m_armBandArray = new ArrayList<>();

    // LinearLayout
    private LinearLayout m_armSetView;

    // ListView
    private ListView m_armSetList;

    // ListView BaseAdapter
    private BaseAdapter m_armSetAdapter;

    // List
    private List<FBKArmBandPrivate> m_armSetArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 步数
    private TextView m_stepText;

    // 步频
    private TextView m_stepFreText;

    private TextView m_caloriesText;
    private TextView m_temText;
    private TextView m_spo2Text;
    private TextView m_otherText;

    // 心率
    private TextView m_heartRateText;

    // 臂带回调
    private FBKApiArmBandCallBack m_apiArmBandCallBack = new FBKApiArmBandCallBack() {
        @Override
        public void realTimeHeartRate(Object data, FBKApiArmBand apiArmBand) {
            Map<String, Object> resultMap = (Map<String, Object>) data;
            String heartRateStr = (String) resultMap.get("heartRate");
            final int heartRate = Integer.parseInt(heartRateStr);
            final String hrv = (String) resultMap.get("HRV");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_heartRateText.setText("   HeartRate: "+heartRate+"->"+hrv);
                }
            });
        }

        @Override
        public void realTimeStepFrequency(Object data, FBKApiArmBand apiArmBand) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_stepText.setText("   Steps: "+resultMap.get("steps"));
                    m_stepFreText.setText("   stepFrequency: "+resultMap.get("stepFrequency"));
                    m_caloriesText.setText("   Calories: "+resultMap.get("calories"));
                }
            });
        }


        @Override
        public void armBandTemperature(Object data, FBKApiArmBand apiArmBand) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_temText.setText(resultMap.toString());
                }
            });
        }


        @Override
        public void armBandSPO2(Object data, FBKApiArmBand apiArmBand) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String spo2 = resultMap.get("spo2");
                    String moveStr = resultMap.get("isMoving");
                    String status = resultMap.get("status");
                    m_spo2Text.setText("   Spo2: "+spo2+"->M:"+moveStr+"->S:"+status);
                }
            });
        }

        @Override
        public void HRVResultData(boolean status, Object data, FBKApiArmBand apiArmBand) {
            Log.e(TAG,"HRVResultData:"+status+"---"+data.toString());
            final Map<String, String> resultMap = (Map<String, String>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, resultMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }


        @Override
        public void armBandRecord(Object data, FBKApiArmBand apiArmBand) {
            final Map<String, Object> resultMap = (Map<String, Object>) data;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultMap.keySet().size() > 0) {
                        List<Object> myList = FBKHRRecordAnaly.analyRecordList(resultMap,true,true, 20, 60, 180, 10001);
                        Log.e(TAG,"armBandRecord --- --- --- "+myList.toString());
                        ModelStorage.getInstance().putModel(resultMap);
                        Log.e(TAG,"armBandRecord - "+resultMap.keySet().size());
                        Intent intent = new Intent(ArmBandActivity.this, RecordActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public void setAgeStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Age succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setShockStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void getShockStatus(Object data, FBKApiArmBand apiArmBand) {
            final Map<String, String> resultMap = (Map<String, String>) data;
            final String number = resultMap.get("shockNumber");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Get Shock"+number,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void closeShockStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Close Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setMaxIntervalStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Max Interval succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setLightSwitchStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Light Switch succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setColorShockStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Color Shock succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void setColorIntervalStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Set Color Interval succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void clearRecordStatus(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "Clear Record succeed!",Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceMacAddress(Object data, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"deviceMacAddress is ---"+data.toString());
                    Toast.makeText(ArmBandActivity.this, data.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void totalVersion(Object data, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG,"totalVersion is ---"+data.toString());
                    Toast.makeText(ArmBandActivity.this, data.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void accelerationData(List dataList, FBKApiArmBand apiArmBand) {
            final List<FBKParaAcceleration> accList = dataList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (accList != null) {
                        int accUtc = accList.get(dataList.size()-1).getTimeStamp();
                        int xNum = accList.get(dataList.size()-1).getxAxis();
                        int yNum = accList.get(dataList.size()-1).getyAxis();
                        int zNum = accList.get(dataList.size()-1).getzAxis();
                        String accString = "  Time: "+accUtc+"ms" + "      X: "+xNum + "      Y: "+yNum + "      Z: "+zNum;
                        m_otherText.setText(accString);
                    }
                }
            });
        }

        @Override
        public void setPrivateFiveZone(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "setPrivateFiveZone:"+status,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void openSettingShow(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "openSettingShow:"+status,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void closeSettingShow(boolean status, FBKApiArmBand apiArmBand) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this, "closeSettingShow:"+status,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
//                        m_apiArmBand.disconnectBle();
//                        m_scanner.startScan();
                        m_statusText.setText("Reconnecting");
                    }
                    else if (status == FBKBleDeviceStatus.BleDisconnected) {
                        Log.e(TAG,"status --- BleDisconnected");
                        m_statusText.setText("Disconnected");
                    }
                    else if (status == FBKBleDeviceStatus.BleTurnOn) {
                        m_statusText.setText("BleTurnOn");
                        if (m_bluetoothDevice != null) {
                            m_apiArmBand.connectBluetooth( m_bluetoothDevice);
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
                    m_armBandArray.set(2,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_armBandAdapter.notifyDataSetChanged();
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
                    m_armBandArray.set(3,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_armBandAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_armBandArray.set(4,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_armBandAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_armBandArray.set(5,"Read Software Version"+"   ("+nowVersion+")");
                    m_armBandAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ArmBandActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ArmBandActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ArmBandActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_armband);

        m_apiArmBand = new FBKApiArmBand(ArmBandActivity.this, m_apiArmBandCallBack);
        m_apiArmBand.registerBleListenerReceiver();
        m_armBandArray.clear();
        m_armBandArray.add("Set Time");
        m_armBandArray.add("Get Record");
        m_armBandArray.add("Read Battery Power");
        m_armBandArray.add("Read Firmware Version");
        m_armBandArray.add("Read Hardware Version");
        m_armBandArray.add("Read Software Version");
        m_armBandArray.add("Set Shock Number");
        m_armBandArray.add("Get Shock");
        m_armBandArray.add("Close Clock");
        m_armBandArray.add("Set max heart rate interval (Simple)");
        m_armBandArray.add("Set light switch (on)");
        m_armBandArray.add("Set light switch (off)");
        m_armBandArray.add("Set color shock (on)");
        m_armBandArray.add("Set color shock (off)");
        m_armBandArray.add("Set color interval");
        m_armBandArray.add("Clear Record");
        m_armBandArray.add("Enter SPO2 Mode");
        m_armBandArray.add("Exit SPO2 Mode");
        m_armBandArray.add("Enter Temperature Mode");
        m_armBandArray.add("Exit Temperature Mode");
        m_armBandArray.add("Get Mac Address");
        m_armBandArray.add("Get Total Version");
        m_armBandArray.add("Enter OTA Mode");
        m_armBandArray.add("Get device name");
        m_armBandArray.add("Set HRV Time");
        m_armBandArray.add("Enter HRV Mode");
        m_armBandArray.add("Exit HRV Mode");
        m_armBandArray.add("Set max heart rate");
        m_armBandArray.add("Private get version");
        m_armBandArray.add("Private get mac");
        m_armBandArray.add("Private Enter OTA Mode");
        m_armBandArray.add("Set Data Frequency");
        m_armBandArray.add("Read System data");
        m_armBandArray.add("Read Model String");
        m_armBandArray.add("Read Serial Number");
        m_armBandArray.add("Read Manufacturer Name");
        m_armBandArray.add("Set Age (26)");
        m_armBandArray.add("Get Base Info");
        m_armBandArray.add("Set Private Ui Zone");
        m_armBandArray.add("Open Setting Show");
        m_armBandArray.add("Close Setting Show");
        m_armBandArray.add("Disconnect Device");


        m_armSetArray.clear();
        for (int i = 0; i < 5; i++) {
            FBKArmBandPrivate myPara = new FBKArmBandPrivate();
            myPara.setHeartZone1(70);
            myPara.setHeartZone2(120);
            myPara.setShowTime(10);
            m_armSetArray.add(myPara);
        }

        initView();
        initSetListView();
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
        m_apiArmBand.disconnectBle();
        m_apiArmBand.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.armband_text_status);
        m_stepText = (TextView) this.findViewById(R.id.armband_text_steps);
        m_stepFreText = (TextView) this.findViewById(R.id.armband_text_stepfrequency);
        m_heartRateText = (TextView) this.findViewById(R.id.armband_text_heartrate);
        m_caloriesText = (TextView) this.findViewById(R.id.armband_text_cal);
        m_temText = (TextView) this.findViewById(R.id.armband_text_tem);
        m_spo2Text = (TextView) this.findViewById(R.id.armband_text_spo);
        m_otherText = (TextView) this.findViewById(R.id.armband_text_acceleration);
        m_armSetView = (LinearLayout) this.findViewById(R.id.armband_setview);

        m_armBandListView = (ListView) this.findViewById(R.id.armband_list);
        m_armBandAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_armBandArray.size();
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
                LayoutInflater inflater = ArmBandActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_armBandArray.get(position));

            //    ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
              //  chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_armBandListView.setAdapter(m_armBandAdapter);
        m_armBandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    m_scanner.startScan();
                    m_apiArmBand.setUTC(new Date());
                }
                else if (position == 1) {
                    m_apiArmBand.getArmBandRecord();
                }
                else if (position == 2) {
                    m_apiArmBand.readDeviceBatteryPower();
                }
                else if (position == 3) {
                    m_apiArmBand.readFirmwareVersion();
                }
                else if (position == 4) {
                    m_apiArmBand.readHardwareVersion();
                }
                else if (position == 5) {
                    m_apiArmBand.readSoftwareVersion();
                }
                else if (position == 6) {
                    setCmdNumber(2);
                }
                else if (position == 7) {
                    m_apiArmBand.getShock();
                }
                else if (position == 8) {
                    m_apiArmBand.closeShock();
                }
                else if (position == 9) {
                    setCmdNumber(1);
                }
                else if (position == 10) {
                    m_apiArmBand.setLightSwitch(true);
                }
                else if (position == 11) {
                    m_apiArmBand.setLightSwitch(false);
                }
                else if (position == 12) {
                    m_apiArmBand.setColorShock(true);
                }
                else if (position == 13) {
                    m_apiArmBand.setColorShock(false);
                }
                else if (position == 14) {
                    setCmdNumber(3);
                }
                else if (position == 15) {
                    m_apiArmBand.clearRecord();
                }
                else if (position == 16) {
                    m_apiArmBand.enterSPO2Mode(true);
                    m_spo2Text.setText("   Spo2: --");
                }
                else if (position == 17) {
                    m_apiArmBand.enterSPO2Mode(false);
                }
                else if (position == 18) {
                    m_apiArmBand.enterTemperatureMode(true);
                }
                else if (position == 19) {
                    m_apiArmBand.enterTemperatureMode(false);
                }
                else if (position == 20) {
                    m_apiArmBand.getDeviceMacAddress();
                }
                else if (position == 21) {
                    m_apiArmBand.getDeviceVersion();
                }
                else if (position == 22) {
                    showSystemDialog("Device will enter OTA Mode?",0);
                }
                else if (position == 23) {
                    Log.e(TAG,"----*********-----"+m_apiArmBand.getBluetoothDevice().getName());
                }
                else if (position == 24) {
                    setCmdNumber(4);
                }
                else if (position == 25) {
                    m_apiArmBand.enterHRVMode(true);
                }
                else if (position == 26) {
                    m_apiArmBand.enterHRVMode(false);
                }
                else if (position == 27) {
                    setCmdNumber(0);
                }
                else if (position == 28) {
                    m_apiArmBand.getPrivateVersion();
                }
                else if (position == 29) {
                    m_apiArmBand.getPrivateMacAddress();
                }
                else if (position == 30) {
                    showSystemDialog("Device will enter OTA Mode?",0);
                }
                else if (position == 31) {
                    m_apiArmBand.setDataFrequency(4);
                }
                else if (position == 32) {
                    m_apiArmBand.readSystemId();
                }
                else if (position == 33) {
                    m_apiArmBand.readModelString();
                }
                else if (position == 34) {
                    m_apiArmBand.readSerialNumber();
                }
                else if (position == 35) {
                    m_apiArmBand.readManufacturerName();
                }
                else if (position == 36) {
                    m_apiArmBand.setDeviceAge(26);
                }
                else if (position == 37) {
                    m_apiArmBand.getDeviceBaseInfo();
                }
                else if (position == 38) {
                    m_armSetView.setVisibility(View.VISIBLE);
                }
                else if (position == 39) {
                    m_apiArmBand.openSettingShow();
                }
                else if (position == 40) {
                    m_apiArmBand.closeSettingShow();
                }
                else if (position == 41) {
                    m_apiArmBand.disconnectBle();
                }
            }
        });
    }


    private void initSetListView() {
        m_armSetList = (ListView) this.findViewById(R.id.armband_setting);
        m_armSetAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return 15;
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
                LayoutInflater inflater = ArmBandActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                int showRow = position / 3;
                int showIndex = position % 3;
                int showNo = 0;
                String titleString = "显示模式"+(showRow+1)+" - ";
                FBKArmBandPrivate myPara = m_armSetArray.get(showRow);
                if (showIndex == 0){
                    showNo = myPara.getHeartZone1();
                    titleString = titleString+"区间一:   "+showNo;
                }
                else if (showIndex == 1){
                    showNo = myPara.getHeartZone2();
                    titleString = titleString+"区间二:   "+showNo;
                }
                else if (showIndex == 2){
                    showNo = myPara.getShowTime();
                    titleString = titleString+"展示时间(分钟):   "+showNo;
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText(titleString);

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_armSetList.setAdapter(m_armSetAdapter);
        m_armSetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                enterSetNumber(position);
            }
        });
    }


    private void enterSetNumber(int position) {
        int showRow = position / 3;
        int showIndex = position % 3;
        String titleString = "显示模式"+(showRow+1)+" - ";
        FBKArmBandPrivate myPara = m_armSetArray.get(showRow);
        if (showIndex == 0){
            titleString = titleString+"区间一";
        }
        else if (showIndex == 1){
            titleString = titleString+"区间二 ";
        }
        else if (showIndex == 2){
            titleString = titleString+"展示时间(分钟)";
        }

        AlertDialog.Builder bulder = new AlertDialog.Builder(ArmBandActivity.this);
        bulder.setCancelable(false);
        bulder.setTitle(titleString);
        EditText enterText = new EditText(this);
        bulder.setView(enterText);
        bulder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        bulder.setPositiveButton("confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (enterText.getText().toString().length() > 0) {
                    int value = Integer.valueOf(enterText.getText().toString());
                    if (showIndex == 0){
                        myPara.setHeartZone1(value);
                    }
                    else if (showIndex == 1){
                        myPara.setHeartZone2(value);
                    }
                    else if (showIndex == 2){
                        myPara.setShowTime(value);
                    }
                    m_armSetAdapter.notifyDataSetChanged();
                }
            }
        });

        bulder.show();
    }


    private void showSystemDialog(String alertString, int tag) {
        AlertDialog.Builder bulder = new AlertDialog.Builder(ArmBandActivity.this);
        bulder.setCancelable(false);
        bulder.setTitle("Alert");
        bulder.setMessage(alertString);
        bulder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        bulder.setPositiveButton("Confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_apiArmBand.enterOTAMode();
            }
        });

        bulder.show();
    }


    private void setCmdNumber(int cmdId) {
        AlertDialog.Builder bulder = new AlertDialog.Builder(ArmBandActivity.this);
        bulder.setCancelable(false);
        if (cmdId == 0) {
            bulder.setTitle("Set Max Heart Rate");
        }
        else if (cmdId == 1) {
            bulder.setTitle("Set Max Heart Rate (Simple)");
        }
        else if (cmdId == 2) {
            bulder.setTitle("Set Shock");
        }
        else if (cmdId == 3) {
            bulder.setTitle("Set Color Interval");
        }
        else if (cmdId == 4) {
            bulder.setTitle("Set HRV Time");
        }
        EditText enterText = new EditText(this);
        bulder.setView(enterText);
        bulder.setNegativeButton("cancel",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        bulder.setPositiveButton("Confirm",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (enterText.getText().toString().length() > 0) {
                    Log.e(TAG,"setCmdNumber --- "+enterText.getText().toString());
                    if (cmdId == 0) {
                        m_apiArmBand.setMaxHr(Integer.valueOf(enterText.getText().toString()));
                    }
                    else if (cmdId == 1) {
                        m_apiArmBand.setMaxInterval(Integer.valueOf(enterText.getText().toString()));
                    }
                    else if (cmdId == 2) {
                        m_apiArmBand.setShock(Integer.valueOf(enterText.getText().toString()));
                    }
                    else if (cmdId == 3) {
                        m_apiArmBand.setColorInterval(Integer.valueOf(enterText.getText().toString()));
                    }
                    else if (cmdId == 4) {
                        m_apiArmBand.setHrvTime(Integer.valueOf(enterText.getText().toString()));
                    }
                }
            }
        });

        bulder.show();
    }


    /************************************************************************************
     * 方法名称：backAction
     * 功能描述：返回
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void backAction(View view) {
//        m_scanner.stopScan();
        finish();
    }


    /************************************************************************************
     * 方法名称：hiddenSetting
     * 功能描述：hiddenSetting
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void hiddenSetting(View view) {
        m_armSetView.setVisibility(View.INVISIBLE);
    }


    /************************************************************************************
     * 方法名称：sendAction
     * 功能描述：sendAction
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void sendAction(View view) {
        m_armSetView.setVisibility(View.INVISIBLE);
        if (m_apiArmBand.isConnected) {
            m_apiArmBand.setPrivateFiveZone(m_armSetArray);
        }
    }


    /************************************************************************************
     * 方法名称：deviceAction
     * 功能描述：选择设备
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void deviceAction(View view) {
        Intent intent = new Intent(ArmBandActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,ARMBAND_TO_SCAN);
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
        if (requestCode == ARMBAND_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiArmBand.connectBluetooth(m_bluetoothDevice.getAddress());
            Log.e(TAG,"mac is --- "+m_bluetoothDevice.getAddress()); //E5:CB:55:F4:CB:59
        }
    }


//    public static int bytes4toInt(byte[] src) {
//        if (src.length ==  2) {
//            int byte1Int = src[0]&0xFF;
//            int byte2Int = src[1]&0xFF;
//
//            // 低位在前，高位在后
//            int value = byte1Int + (byte2Int<<8);
//
//            // 高位在前，低位在后
////            int value = (byte1Int<<8) + byte2Int;
//
//            return value;
//        }
//
//        return 0;
//    }


    private FBKNewBleScan m_scanner;

    private FBKBleScanCallBack m_apiScanCallBack = new FBKBleScanCallBack() {
        @Override
        public void bleScanResult(List<FBKBleDevice> deviceArray, Object bleScanner) {
            Log.e(TAG,"bleScanResult Numbers : "+deviceArray.size());
        }

        @Override
        public void bleScanAvailable(Boolean isAvailable, Object bleScanner) {

        }
    };

    private void allocScan() {
        m_scanner = new FBKNewBleScan(ArmBandActivity.this, m_apiScanCallBack);
    }

}
