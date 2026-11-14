package com.app.kickboxing.Activitys.presentation.ECG;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

import com.app.kickboxing.R;
import com.hb.dialog.BuildConfig;
import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
import com.onecoder.fitblekit.API.ECG.FBKApiECG;
import com.onecoder.fitblekit.API.ECG.FBKApiECGCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.ECG.Command.ECGShowColor;
import com.onecoder.fitblekit.Tools.ECGAnalyze.FBKECGAnalyze;
import com.onecoder.fitblekit.Tools.ECGAnalyze.FBKECGAnalyzeCallback;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

import androidx.core.content.FileProvider;

public class ECGActivity extends Activity {

    // TAG值
    private static final String TAG = ECGActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int ECG_TO_SCAN = 20002;

    // 蓝牙设备
    private BluetoothDevice m_bluetoothDevice;

    // 设备API
    private FBKApiECG m_apiECG;

    // ListView
    private ListView m_ECGListView;

    // ListView BaseAdapter
    private BaseAdapter m_ECGAdapter;

    // List
    private static List<String> m_ECGArray = new ArrayList<>();

    // List
    private static List<String> m_dataArray = new ArrayList<>();

    // 连接状态
    private TextView m_statusText;

    // 速度
    private TextView m_ECGText;

    private ECGShowColor m_showColor = ECGShowColor.ShowGreenColor;

    private FBKECGAnalyze m_ecgAnalyze;

    // 回调
    private FBKApiECGCallBack m_apiECGCallBack = new FBKApiECGCallBack() {
        @Override
        public void realTimeECG(List<String> ecgList, int sortNo, FBKApiECG apiECG) {
//            m_ecgAnalyze.ecgListAnalyzeFilter(ecgList);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_dataArray.addAll(ecgList);
                    m_ECGText.setText(sortNo+" ECG: "+ecgList.toString());
                }
            });
        }

        @Override
        public void realTimeHR(Object data, FBKApiECG apiECG) {
            Map<String, Object> resultMap = (Map<String, Object>) data;
            String heartRateStr = (String) resultMap.get("heartRate");
            final int heartRate = Integer.parseInt(heartRateStr);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    m_ECGText.setText("   HeartRate: "+resultMap.toString());
                    Toast.makeText(ECGActivity.this,"heartRate:" + resultMap.toString(),Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"realTimeHR ---*******---"+resultMap.toString());
                }
            });
        }

        @Override
        public void setColorResult(boolean status, FBKApiECG apiECG) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,"SetColorResult:" + status,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void ecgSwitchResult(boolean status, FBKApiECG apiECG) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,"ecgSwitchResult:" + status,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void ECGHrvResultData(boolean status, Object data, FBKApiECG apiECG) {
            Log.e(TAG,"HRHrvResult:"+status+"---"+data.toString());
        }

        @Override
        public void testECG(boolean status, List<String> ecgList, FBKApiECG apiECG) {
            Log.e(TAG,"testECG:"+status+"---"+ecgList.toString());
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,errorString,Toast.LENGTH_SHORT).show();
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
                            m_apiECG.connectBluetooth( m_bluetoothDevice);
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
                    m_ECGArray.set(0,"Read Battery Power"+"   ("+String.valueOf(batteryPower)+"%"+")");
                    m_ECGAdapter.notifyDataSetChanged();
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
                    m_ECGArray.set(1,"Read Firmware Version"+"   ("+nowVersion+")");
                    m_ECGAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_ECGArray.set(2,"Read Hardware Version"+"   ("+nowVersion+")");
                    m_ECGAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            final String nowVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    m_ECGArray.set(3,"Read Software Version"+"   ("+nowVersion+")");
                    m_ECGAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectInfo(String infoString, FBKApiBsaeMethod apiBsaeMethod) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String timeString = dateFormat.format(date);
//            Log.e(TAG,timeString+" --- bleConnectInfo --- "+infoString);
        }

        @Override
        public void deviceSystemData(byte[] systemData, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ECGActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ECGActivity.this,showString,Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private FBKECGAnalyzeCallback m_analyzeCallback = new FBKECGAnalyzeCallback() {
        @Override
        public void realTimeEcg(Map<String, Object> ecgMap, byte[] rawByte, FBKECGAnalyze ecgAnalyze) {
            Log.e(TAG,"realTimeEcg ------- "+ecgMap.toString());
        }

        @Override
        public void ecgFilterResult(List<String> ecgList, List<String> ecgFilterList, FBKECGAnalyze ecgAnalyze) {
            Log.e(TAG,"ecgFilterResult ------- ecgList:"+ecgList.size() + "--- ecgFilterList:" + ecgFilterList.size());
        }

        @Override
        public void realTimeHR(Map<String, Object> hrMap, FBKECGAnalyze ecgAnalyze) {
            Log.e(TAG,"realTimeHR JJJJJJJJJJJJJJ  ------- "+hrMap.toString());
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
        setContentView(R.layout.activity_ecg);

        m_apiECG = new FBKApiECG(ECGActivity.this, m_apiECGCallBack);
        m_apiECG.registerBleListenerReceiver();

        List<String> ecgList = new ArrayList<>();
        ecgList.add("50");
        ecgList.add("52");
        ecgList.add("53");
        ecgList.add("54");
        ecgList.add("55");
        ecgList.add("56");

        m_ecgAnalyze = new FBKECGAnalyze(m_analyzeCallback, 600);

        m_ECGArray.clear();
        m_ECGArray.add("Read Battery Power");
        m_ECGArray.add("Read Firmware Version");
        m_ECGArray.add("Read Hardware Version");
        m_ECGArray.add("Read Software Version");
        m_ECGArray.add("Private get version");
        m_ECGArray.add("Private get mac");
        m_ECGArray.add("Private Enter OTA Mode");
        m_ECGArray.add("Set Light Color");
        m_ECGArray.add("Enter ECG Mode");
        m_ECGArray.add("Exit ECG Mode");
        m_ECGArray.add("Enter HRV Mode");
        m_ECGArray.add("Exit HRV Mode");
        m_ECGArray.add("Open ECG Data");
        m_ECGArray.add("Close ECG Data");
        m_ECGArray.add("Read System data");
        m_ECGArray.add("Read Model String");
        m_ECGArray.add("Read Serial Number");
        m_ECGArray.add("Read Manufacturer Name");
        m_ECGArray.add("Get SDK Version");
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
        m_apiECG.disconnectBle();
        m_apiECG.unregisterBleListenerReceiver();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_statusText = (TextView) this.findViewById(R.id.ecg_text_status);
        m_ECGText = (TextView) this.findViewById(R.id.ecg_text_ecg);

        m_ECGListView = (ListView) this.findViewById(R.id.ecg_list);
        m_ECGAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_ECGArray.size();
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
                LayoutInflater inflater = ECGActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_ECGArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_ECGListView.setAdapter(m_ECGAdapter);
        m_ECGListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiECG.readDeviceBatteryPower();
                    logEcgList();
                }
                else if (position == 1) {
                    m_apiECG.readFirmwareVersion();
                }
                else if (position == 2) {
                    m_apiECG.readHardwareVersion();
                }
                else if (position == 3) {
                    m_apiECG.readSoftwareVersion();
                }
                else if (position == 4) {
                    m_apiECG.getPrivateVersion();
                }
                else if (position == 5) {
                    m_apiECG.getPrivateMacAddress();
                }
                else if (position == 6) {
                    showSystemDialog("Device will enter OTA Mode?",0);
                }
                else if (position == 7) {
                    if (m_showColor == ECGShowColor.ShowGreenColor) {
                        m_showColor = ECGShowColor.ShowBlueColor;
                    }
                    else if (m_showColor == ECGShowColor.ShowBlueColor) {
                        m_showColor = ECGShowColor.ShowRedColor;
                    }
                    else if (m_showColor == ECGShowColor.ShowRedColor) {
                        m_showColor = ECGShowColor.ShowGreenColor;
                    }

                    m_apiECG.setDeviceColor(ECGShowColor.ShowRedColor);
                }
                else if (position == 8) {
                    m_apiECG.enterECGMode(true);
                }
                else if (position == 9) {
                    m_apiECG.enterECGMode(false);
                }
                else if (position == 10) {
                    m_apiECG.enterHRVMode(true);
                }
                else if (position == 11) {
                    m_apiECG.enterHRVMode(false);
                }
                else if (position == 12) {
                    m_apiECG.ecgDataSwitch(true);
                }
                else if (position == 13) {
                    m_apiECG.ecgDataSwitch(false);
                }
                else if (position == 14) {
                    m_apiECG.readSystemId();
                }
                else if (position == 15) {
                    m_apiECG.readModelString();
                }
                else if (position == 16) {
                    m_apiECG.readSerialNumber();
                }
                else if (position == 17) {
                    m_apiECG.readManufacturerName();
                }
                else if (position == 18) {
                    Map<String,Object> versionMap = m_apiECG.getSDKVersion();
                    int versionCode = (int)versionMap.get("versionCode");
                    String versionName = (String)versionMap.get("versionName");
                    String showString = "versionCode:"+versionCode+"---versionName:"+versionName;
                    Toast.makeText(ECGActivity.this,showString,Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showSystemDialog(String alertString, int tag) {
        AlertDialog.Builder bulder = new AlertDialog.Builder(ECGActivity.this);
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
                m_apiECG.enterOTAMode();
            }
        });

        bulder.show();
    }


    /************************************************************************************
     * 方法名称：logEcgList
     * 功能描述：
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void logEcgList() {
        String ecgString = "";
        for (int i = 0; i < m_dataArray.size(); i++) {
            ecgString = ecgString + m_dataArray.get(i)+",";
        }

        String path = ECGActivity.this.getFilesDir().getPath();
        writeTxtToFile(ecgString, path, "ECGDataLog2.txt");
        String pathString = path+"/"+"ECGDataLog2.txt";

        shareFile(ECGActivity.this, pathString);
        Log.e(TAG,"AA------"+pathString);
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
        Intent intent = new Intent(ECGActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,ECG_TO_SCAN);
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
        if (requestCode == ECG_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
            m_apiECG.connectBluetooth(m_bluetoothDevice);
        }
    }


    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeRootDirectory(filePath);

        String strFilePath = filePath+"/"+fileName;
        try {
            File file = new File(strFilePath);
            if (!file.exists() && file.getParentFile() != null) {
                boolean isMake = file.getParentFile().mkdirs();
                boolean isCreate = file.createNewFile();
                Log.d("========", "writeTxtToFile: " + isMake + isCreate);
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strcontent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件夹
    private static void makeRootDirectory(String filePath) {
        File file;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                boolean isMkdir = file.mkdir();
                // 返回值是false  因为没有SD卡权限
                Log.d("========", "writeTxtToFile: " + isMkdir);
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }

    public static void shareFile(Context context, String fileName) {
        File file = new File(fileName);
        if (null != file && file.exists()) {
            Intent share = new Intent(Intent.ACTION_SEND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", file);
                share.putExtra(Intent.EXTRA_STREAM, contentUri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            }
            share.setType("application/vnd.ms-excel");//此处可发送多种文件
            share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(share, "分享文件"));
        } else {
            Toast.makeText(context, "分享文件不存在", Toast.LENGTH_SHORT).show();
        }
    }

}
