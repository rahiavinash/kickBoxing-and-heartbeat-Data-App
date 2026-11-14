/****************************************************************************************
 * 文件名称：HubConfigActivity.java
 * 内容摘要：HUB
 * 版本编号：1.0.1
 * 创建日期：2019年08月22日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.HubConfig;

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
import com.onecoder.fitblekit.API.HubConfig.FBKApiHubConfig;
import com.onecoder.fitblekit.API.HubConfig.FBKApiHubConfigCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaHubChannel;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaHubScan;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaHubSocket;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaIPV4Info;
import com.onecoder.fitblekit.Protocol.Common.Parameter.FBKParaWiFiSTA;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class HubConfigActivity extends Activity {

    // TAG值
    private static final String TAG = HubConfigActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int HUBCONFIG_TO_SCAN = 7001;

    // WiFi 选择 TAG
    public static int HUBCONFIG_TO_WIFI = 7002;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 新协议手环
    private FBKApiHubConfig m_apiHubConfig;

    // ListView
    private ListView m_hubListView;

    // ListView BaseAdapter
    private BaseAdapter m_hubAdapter;

    // List
    private static List<String> m_hubArray = new ArrayList<>();

    private Map<String,String> m_wifiInfo = new HashMap<>();

    // 连接状态
    private TextView m_statusText;

    // 信息展示
    private TextView m_infoText;

    // HUB回调
    private FBKApiHubConfigCallBack m_apiHubConfigCallBack = new FBKApiHubConfigCallBack() {
        @Override
        public void hubLoginStatus(boolean status, FBKApiHubConfig apiHubConfig) {
            final boolean isSucceed = status;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isSucceed) {
                        m_infoText.setText("Login succeed");
                    }
                    else {
                        m_infoText.setText("Login failed");
                    }
                }
            });
        }

        @Override
        public void hubLoginPassword(Object passwordInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> pwMap = (Map<String,String>) passwordInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String password = pwMap.get("hubPassword");
                    if (password.equals("0") || password.length() == 0) {
                        m_infoText.setText("Password is nil");
                    }
                    else {
                        m_infoText.setText("Password is "+password);
                    }
                }
            });
        }

        @Override
        public void hubWifiWorkMode(String workMode, FBKApiHubConfig apiHubConfig) {
            final String hubWorkMode = workMode;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hubWorkMode.equals("0")) {
                        m_infoText.setText("Mode: AP");
                    }
                    else if (hubWorkMode.equals("1")) {
                        m_infoText.setText("Mode: STA");
                    }
                    else {
                        m_infoText.setText("Mode: AP+STA");
                    }
                }
            });
        }

        @Override
        public void hubWifiSTAInfo(Object staInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> staMap = (Map<String,String>) staInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String hubSsid = staMap.get("hubSsid");
                    String hubPassword = staMap.get("hubPassword");
                    String hubEncryption = staMap.get("hubEncryption");
                    String hubAlgorithm = staMap.get("hubAlgorithm");
                    hubEncryption = getEncryptionName(hubEncryption);
                    hubAlgorithm = getAlgorithmName(hubAlgorithm);
                    m_infoText.setText("hubSsid: "+hubSsid+"\n"+"hubPassword: "+hubPassword+"\n"+"hubEncryption: "+hubEncryption+"\n"+"hubAlgorithm: "+hubAlgorithm);
                }
            });
        }

        @Override
        public void hubWifiSocketInfo(Object socketInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> socketMap = (Map<String,String>) socketInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String hubSocketNo = socketMap.get("hubSocketNo");
                    String hubSocketProtocol = socketMap.get("hubSocketProtocol");
                    String hubSocketCs = socketMap.get("hubSocketCs");
                    String hubSocketIp = socketMap.get("hubSocketIp");
                    String hubSocketPort = socketMap.get("hubSocketPort");
                    if (hubSocketNo.equals("0")) {
                        hubSocketNo = "Socket No: A";
                    }
                    else {
                        hubSocketNo = "Socket No: B";
                    }

                    if (hubSocketProtocol.equals("2")) {
                        hubSocketProtocol = "SocketProtocol:: UDP";
                    }
                    else {
                        hubSocketProtocol = "SocketProtocol:: TCP";
                    }

                    if (hubSocketCs.equals("2")) {
                        hubSocketCs = "hubSocketCs:: Client";
                    }
                    else {
                        hubSocketCs = "hubSocketCs:: Server";
                    }

                    m_infoText.setText(hubSocketNo+"\n"+hubSocketProtocol+"\n"+hubSocketCs+"\n"+"SocketIp: "+hubSocketIp+"\n"+"SocketPort: "+hubSocketPort);
                }
            });
        }

        @Override
        public void hubNetWorkMode(String netWorkMode, FBKApiHubConfig apiHubConfig) {
            final String mode = netWorkMode;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mode.equals("0")) {
                        m_infoText.setText("Mode: External network mode");
                    }
                    else if (mode.equals("1")) {
                        m_infoText.setText("Mode: Intranet mode");
                    }
                    else {
                        m_infoText.setText("Mode: 4G");
                    }
                }
            });
        }

        @Override
        public void hubRemarkInfo(String remark, FBKApiHubConfig apiHubConfig) {
            final String markString = remark;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("Remark: "+markString);
                }
            });
        }

        @Override
        public void hubIpKeyInfo(Object ipKeyInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> ipKeyMap = (Map<String,String>) ipKeyInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String hubIp = ipKeyMap.get("hubIp");
                    String hubMask = ipKeyMap.get("hubMask");
                    String hubGateWay = ipKeyMap.get("hubGateWay");
                    m_infoText.setText("IP: "+hubIp+"\n"+"Hub Mask: "+hubMask+"\n"+"Hub Gate Way: "+hubGateWay);
                }
            });
        }

        @Override
        public void hubWifiList(Object wifiList, FBKApiHubConfig apiHubConfig) {
            final List<Object> resultList = (List<Object>) wifiList;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (resultList != null) {
                        Log.e(TAG,resultList.toString());
                        Intent intent = new Intent(HubConfigActivity.this, WiFiListActivity.class);
                        intent.putExtra("data",(Serializable)resultList);
                        startActivityForResult(intent,HUBCONFIG_TO_WIFI);
                    }
                }
            });
        }

        @Override
        public void hubWifiStatus(Object statusInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> statusMap = (Map<String,String>) statusInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String hubWifiCfgStatus = statusMap.get("hubWifiCfgStatus");
                    String hubWifiConnectStatus = statusMap.get("hubWifiConnectStatus");
                    if (hubWifiCfgStatus.equals("0")) {
                        hubWifiCfgStatus = "Idle";
                    }
                    else if (hubWifiCfgStatus.equals("1")) {
                        hubWifiCfgStatus = "Busy";
                    }
                    else if (hubWifiCfgStatus.equals("2")) {
                        hubWifiCfgStatus = "Set Failed";
                    }
                    else if (hubWifiCfgStatus.equals("3")) {
                        hubWifiCfgStatus = "Set Succeed";
                    }

                    if (hubWifiConnectStatus.equals("0")) {
                        hubWifiConnectStatus = "Disconnect";
                    }
                    else if (hubWifiConnectStatus.equals("1")) {
                        hubWifiConnectStatus = "Connected";
                    }

                    m_infoText.setText("Status: "+hubWifiCfgStatus+"&"+hubWifiConnectStatus);
                }
            });
        }

        @Override
        public void hub4GAPN(Object statusInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> rsultMap = (Map<String,String>) statusInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("hub4GAPN: "+rsultMap.toString());
                }
            });
        }

        @Override
        public void hubSystemStatus(Object statusInfo, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> rsultMap = (Map<String,String>) statusInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("hubSystemStatus: "+rsultMap.toString());
                }
            });
        }

        @Override
        public void hubIPV4Info(Object ipv4Info, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> rsultMap = (Map<String,String>) ipv4Info;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("hubIPV4Info: "+rsultMap.toString());
                }
            });
        }

        @Override
        public void hubSetLoraResult(Object ipv4Info, FBKApiHubConfig apiHubConfig) {
            final Map<String,Object> rsultMap = (Map<String,Object>) ipv4Info;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("hubSetLoraResult: "+rsultMap.toString());
                }
            });
        }

        @Override
        public void hubDiagnosisLoraResult(Object ipv4Info, FBKApiHubConfig apiHubConfig) {
            final Map<String,String> rsultMap = (Map<String,String>) ipv4Info;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("hubDiagnosisLoraResult: "+rsultMap.toString());
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiHubConfig.connectBluetooth( m_bluetoothDevice);
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
                    m_hubArray.set(14,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_hubAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_infoText.setText("protocolVersion: "+version);
                }
            });
        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_hubArray.set(15,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_hubAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_hubArray.set(16,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_hubAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_hubArray.set(17,"Read Software Version"+"   ("+nowVersion+")");
                    m_hubAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HubConfigActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HubConfigActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(HubConfigActivity.this,showString,Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_hubconfig);

        m_apiHubConfig = new FBKApiHubConfig(HubConfigActivity.this, m_apiHubConfigCallBack);
        m_apiHubConfig.registerBleListenerReceiver();
        m_hubArray.clear();
        m_hubArray.add("Hub Login");
        m_hubArray.add("Get Login Password");
        m_hubArray.add("Set Login Password");
        m_hubArray.add("Scan WiFi");
        m_hubArray.add("Get STA Information");
        m_hubArray.add("Set STA Information");
        m_hubArray.add("Get Socket Information");
        m_hubArray.add("Set Socket Information");
        m_hubArray.add("Get WiFi Work Type");
        m_hubArray.add("Set WiFi Work Type");
        m_hubArray.add("Get Hub IP");
        m_hubArray.add("Restart Hub");
        m_hubArray.add("Reset Hub");
        m_hubArray.add("Get WiFi Status");
        m_hubArray.add("Read Battery Power");
        m_hubArray.add("Read Firmware Version");
        m_hubArray.add("Read Hardware Version");
        m_hubArray.add("Read Software Version");
        m_hubArray.add("Get 4G APN");
        m_hubArray.add("Set 4G APN");
        m_hubArray.add("Set Data Type");
        m_hubArray.add("Set Scan Switch");
        m_hubArray.add("Set Scan Info");
        m_hubArray.add("Get Systen Status");
        m_hubArray.add("Get Net mode");
        m_hubArray.add("Private get version");
        m_hubArray.add("Private get mac");
        m_hubArray.add("Private Enter OTA Mode");
        m_hubArray.add("Read System data");
        m_hubArray.add("Read Model String");
        m_hubArray.add("Read Serial Number");
        m_hubArray.add("Read Manufacturer Name");
        m_hubArray.add("Get IPV4 info");
        m_hubArray.add("Set IPV4 info");
        m_hubArray.add("Set Lora channel");
        m_hubArray.add("Diagnosis Lora channel");
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
        m_apiHubConfig.disconnectBle();
        m_apiHubConfig.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.hub_text_status);
        m_infoText = (TextView) this.findViewById(R.id.hub_text_info);

        m_hubListView = (ListView) this.findViewById(R.id.hub_list);
        m_hubAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_hubArray.size();
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
                LayoutInflater inflater = HubConfigActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_hubArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_hubListView.setAdapter(m_hubAdapter);
        m_hubListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                m_infoText.setText("");
                if (position == 0) {
                    m_apiHubConfig.hubLogin("123456");
                }
                else if (position == 1) {
                    m_apiHubConfig.getHubPassword();
                }
                else if (position == 2) {
                    m_apiHubConfig.setHubPassword("123456",1);
                }
                else if (position == 3) {
                    m_infoText.setText("Waiting...");
                    m_apiHubConfig.scanHubWifi();
                }
                else if (position == 4) {
                    m_apiHubConfig.getHubWifiSTA();
                }
                else if (position == 5) {
                    if (m_wifiInfo.keySet().size() > 0) {
                        Log.e(TAG,m_wifiInfo.toString());
                        FBKParaWiFiSTA wiFiSTAInfo = new FBKParaWiFiSTA();
                        wiFiSTAInfo.setSsid(m_wifiInfo.get("hubSsid"));
                        wiFiSTAInfo.setPassword(m_wifiInfo.get("password"));
                        wiFiSTAInfo.setEncryption(Integer.parseInt(m_wifiInfo.get("hubEncryption")));
                        wiFiSTAInfo.setAlgorithm(Integer.parseInt(m_wifiInfo.get("hubAlgorithm")));
                        Log.e(TAG,wiFiSTAInfo.toString());
                        m_apiHubConfig.setHubWifiSTA(wiFiSTAInfo);
                    }
                    else {
                        Toast.makeText(HubConfigActivity.this, "Please Choose WiFi",Toast.LENGTH_SHORT).show();
                    }
                }
                else if (position == 6) {
                    m_apiHubConfig.getHubSocketInfo(true);
                }
                else if (position == 7) {
                    FBKParaHubSocket hubSocketInfo = new FBKParaHubSocket();
                    hubSocketInfo.setSocketNo(0);
                    hubSocketInfo.setSocketProtocol(2);
                    hubSocketInfo.setSocketCs(2);
                    hubSocketInfo.setSocketIp("service.spotech.cn");
                    hubSocketInfo.setSocketPort("9125");
                    m_apiHubConfig.setHubSocketInfo(hubSocketInfo);
                }
                else if (position == 8) {
                    m_apiHubConfig.getHubWifiMode();
                }
                else if (position == 9) {
                    m_apiHubConfig.setHubNetWorkMode(1);
                }
                else if (position == 10) {
                    m_apiHubConfig.getHubIpKey();
                }
                else if (position == 11) {
                    m_apiHubConfig.restartHub();
                }
                else if (position == 12) {
                    m_apiHubConfig.resetHub();
                }
                else if (position == 13) {
                    m_apiHubConfig.getHubWifiStatus();
                }
                else if (position == 14) {
                    m_apiHubConfig.readDeviceBatteryPower();
                }
                else if (position == 15) {
                    m_apiHubConfig.readFirmwareVersion();
                }
                else if (position == 16) {
                    m_apiHubConfig.readHardwareVersion();
                }
                else if (position == 17) {
                    m_apiHubConfig.readSoftwareVersion();
                }
                else if (position == 18) {
                    m_apiHubConfig.getHub4GAPN();
                }
                else if (position == 19) {
                    m_apiHubConfig.setHub4GAPN("HELLO");
                }
                else if (position == 20) {
                    m_apiHubConfig.setHubDataType(1);
                }
                else if (position == 21) {
                    m_apiHubConfig.setHubScanSwitch(2);
                }
                else if (position == 22) {
                    FBKParaHubScan hubScanInfo = new FBKParaHubScan();
                    hubScanInfo.setScanName("HW7");
                    hubScanInfo.setScanUuid("2a37");
                    hubScanInfo.setScanRssi(-80);
                    m_apiHubConfig.hubScanInfo(hubScanInfo);
                }
                else if (position == 23) {
                    m_apiHubConfig.hubSystenStatus();
                }
                else if (position == 24) {
                    m_apiHubConfig.getHubNetWorkMode();
                }
                else if (position == 25) {
                    m_apiHubConfig.getPrivateVersion();
                }
                else if (position == 26) {
                    m_apiHubConfig.getPrivateMacAddress();
                }
                else if (position == 27) {
                    m_apiHubConfig.enterOTAMode();
                }
                else if (position == 28) {
                    m_apiHubConfig.readSystemId();
                }
                else if (position == 29) {
                    m_apiHubConfig.readModelString();
                }
                else if (position == 30) {
                    m_apiHubConfig.readSerialNumber();
                }
                else if (position == 31) {
                    m_apiHubConfig.readManufacturerName();
                }
                else if (position == 32) {
                    m_apiHubConfig.getIPV4Info();
                }
                else if (position == 33) {
                    FBKParaIPV4Info myIPV4Info = new FBKParaIPV4Info();
                    myIPV4Info.setIpType(1);
                    myIPV4Info.setDnsType(1);
                    myIPV4Info.setIpString("192.168.0.2");
                    myIPV4Info.setMask("255.255.255.2");
                    myIPV4Info.setGateway("192.168.0.2");
                    myIPV4Info.setDnsString("192.168.0.2");
                    myIPV4Info.setSpareDns("8.8.8.8");
                    m_apiHubConfig.setIPV4Info(myIPV4Info);
                }
                else if (position == 34) {
                    FBKParaHubChannel myChannel = new FBKParaHubChannel();
                    myChannel.setTotalChannel(20);
                    myChannel.setNowChannel(1);
                    m_apiHubConfig.setLoraChannel(myChannel);
                }
                else if (position == 35) {
                    FBKParaHubChannel myChannel = new FBKParaHubChannel();
                    myChannel.setNowChannel(1);
                    myChannel.setSeconds(30);
                    m_apiHubConfig.diagnosisLoraChannel(myChannel);
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
        Intent intent = new Intent(HubConfigActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,HUBCONFIG_TO_SCAN);
    }


    /************************************************************************************
     * 方法名称：getEncryptionName
     * 功能描述：获取加密名称
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private String getEncryptionName(String encryption) {
        if (encryption.equals("4")) {
            return "WPA2PSK";
        }
        else if (encryption.equals("3")) {
            return "WPAPSK";
        }
        else if (encryption.equals("2")) {
            return "SHARED";
        }
        else if (encryption.equals("1")) {
            return "OPEN";
        }
        return "NULL";
    }


    /************************************************************************************
     * 方法名称：getAlgorithmName
     * 功能描述：获取加密方式名称
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private String getAlgorithmName(String algorithm) {
        if (algorithm.equals("5")) {
            return "AES";
        }
        else if (algorithm.equals("4")) {
            return "TKIP";
        }
        else if (algorithm.equals("3")) {
            return "WEP_A";
        }
        else if (algorithm.equals("2")) {
            return "WEP_H";
        }
        else if (algorithm.equals("1")) {
            return "NONE";
        }
        return "NULL";
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
        if (requestCode == HUBCONFIG_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiHubConfig.connectBluetooth(m_bluetoothDevice);
        }
        else if (requestCode == HUBCONFIG_TO_WIFI && resultCode == WiFiListActivity.WIFI_ACTIVITY_BACK) {
            m_wifiInfo = (Map<String,String>) data.getSerializableExtra("data");
            Log.e(TAG,m_wifiInfo.toString());
        }
    }


}
