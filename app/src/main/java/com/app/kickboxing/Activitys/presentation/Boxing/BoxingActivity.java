package com.app.kickboxing.Activitys.presentation.Boxing;

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
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxing;
import com.onecoder.fitblekit.API.Boxing.FBKApiBoxingCallBack;
import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
import com.onecoder.fitblekit.Protocol.Boxing.FBKBoxingAxis;
import com.onecoder.fitblekit.Tools.FBKSpliceBle;
import com.app.kickboxing.Activitys.presentation.Main.DeviceClass;
import com.app.kickboxing.Activitys.presentation.NewTracker.ModelStorage;
import com.app.kickboxing.Activitys.presentation.NewTracker.NewTrackerActivity;
import com.app.kickboxing.Activitys.presentation.NewTracker.RecordActivity;
import com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity;
import com.app.kickboxing.R;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.app.kickboxing.Activitys.presentation.ScanDevices.DevicesScanActivity.SCAN_ACTIVITY_BACK;

public class BoxingActivity extends Activity {

    // TAG值
    private static final String TAG = NewTrackerActivity.class.getSimpleName();

    // 获取扫描设备TAG
    public static int BOXING_TO_SCAN = 8001;

    // List
    private static List<DeviceClass> m_deviceArray = new ArrayList<>();

    // 新协议手环
    private FBKApiBoxing m_apiBoxing;

    // ListView
    private ListView m_boxingListView;

    // ListView BaseAdapter
    private BaseAdapter m_boxingAdapter;

    // List
    private static List<String> m_boxingArray = new ArrayList<>();

    private int deviceRow = 0;

    // 连接状态
    private TextView m_statusTitle;
    private TextView m_statusText;
    private TextView m_boxNumberText;
    private TextView m_boxTypeText;
    private TextView m_outTimeText;
    private TextView m_inTimeText;
    private TextView m_powerText;
    private TextView m_speedText;
    private TextView m_distanceText;
    private TextView m_dateText;
    private TextView m_axisText;

    // 拳击器回调
    private FBKApiBoxingCallBack m_apiBoxingCallBack = new FBKApiBoxingCallBack() {
        @Override
        public void realTimeBoxing(Object data, FBKApiBoxing apiBoxing) {
            if (apiBoxing == m_apiBoxing) {
                final Map<String, String> resultMap = (Map<String, String>) data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String typeString = resultMap.get("fistType");
                        if (typeString.equals("1")) {
                            typeString = "Hook";
                        }
                        else if (typeString.equals("2")) {
                            typeString = "Uppercut";
                        }
                        else if (typeString.equals("3")) {
                            typeString = "Straight punch";
                        }
                        m_boxNumberText.setText("Total Punches\n"+resultMap.get("fistNumber"));
                        m_boxTypeText.setText("Punching Type\n"+typeString);
                        m_outTimeText.setText("Punching Time\n"+resultMap.get("fistOutTime")+"ms");
                        m_inTimeText.setText("Withdrawing Time\n"+resultMap.get("fistInTime")+"ms");
                        m_powerText.setText("G-Force\n"+resultMap.get("fistPower"));
                        m_speedText.setText("Punching Speed\n"+resultMap.get("fistSpeed")+"km/h");
                        m_distanceText.setText("Punching Distance\n"+resultMap.get("fistDistance")+"m");
                        m_dateText.setText("Punching Time\n"+resultMap.get("fistDate"));
                    }
                });
            }
        }

        @Override
        public void boxingRecord(Object data, FBKApiBoxing apiBoxing) {
            if (apiBoxing == m_apiBoxing) {
                final Map<String, Object> resultMap = (Map<String, Object>) data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (resultMap.keySet().size() > 0) {
                            ModelStorage.getInstance().putModel(resultMap);
                            Intent intent = new Intent(BoxingActivity.this, RecordActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public void boxingAxisSwitchResult(boolean status, FBKApiBoxing apiBoxing) {
            if (apiBoxing == m_apiBoxing) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (status) {
                            Toast.makeText(BoxingActivity.this,"Axis switch Set Succeed !",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(BoxingActivity.this,"Axis switch Set failed !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        @Override
        public void realtimeAxis(List<FBKBoxingAxis> axisList, int sortNo, int timestamps, FBKApiBoxing apiBoxing) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String showString = "sortNo: " + sortNo + " --- ";
                    showString = "timestamps: " + timestamps + " --- ";
                    if (axisList.size() > 0){
                        FBKBoxingAxis myAxis = axisList.get(0);
                        showString = showString + "accX:" + myAxis.getAccelerationX() + " g --- ";
                        showString = showString + "accY:" + myAxis.getAccelerationY() + " g --- ";
                        showString = showString + "accZ:" + myAxis.getAccelerationZ() + " g --- ";
                        showString = showString + "angX:" + myAxis.getAngularX() + " dps --- ";
                        showString = showString + "angY:" + myAxis.getAngularY() + " dps --- ";
                        showString = showString + "angZ:" + myAxis.getAngularZ() + " dps --- ";
                    }
                    m_axisText.setText(showString);
                }
            });
        }

        @Override
        public void bleConnectError(String error, FBKApiBsaeMethod apiBaseMethod) {
            final String errorString = error;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BoxingActivity.this,errorString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void bleConnectStatus(FBKBleDeviceStatus connectStatus, FBKApiBsaeMethod apiBsaeMethod) {
            final FBKBleDeviceStatus status = connectStatus;
            for (int i = 0; i < m_deviceArray.size(); i++) {
                DeviceClass myDevice = m_deviceArray.get(i);
                FBKApiBoxing apiBoxing = (FBKApiBoxing) myDevice.getBleDevice();
                if (apiBoxing == apiBsaeMethod) {
                    myDevice.setConnectStatus(status);
                    if (status == FBKBleDeviceStatus.BleReconnect) {
                        apiBoxing.connectBluetooth(myDevice.getBluetoothDevice());
                    }
                    break;
                }
            }

            if (apiBsaeMethod == m_apiBoxing) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDeviceStatusString(status);
                    }
                });
            }
        }

        @Override
        public void bleConnectStatusLog(String infoString, FBKApiBsaeMethod apiBsaeMethod) {

        }

        @Override
        public void batteryPower(final int power, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                final int batteryPower = power;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_boxingArray.set(4,"Read Battery Power"+"  ("+String.valueOf(batteryPower)+"%"+")");
                        m_boxingAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
        }

        @Override
        public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                final String nowVersion = version;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_boxingArray.set(5,"Read Firmware Version"+"   ("+nowVersion+")");
                        m_boxingAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                final String nowVersion = version;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_boxingArray.set(6,"Read Hardware Version"+"   ("+nowVersion+")");
                        m_boxingAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                final String nowVersion = version;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        m_boxingArray.set(7,"Read Software Version"+"   ("+nowVersion+")");
                        m_boxingAdapter.notifyDataSetChanged();
                    }
                });
            }
        }

        @Override
        public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxingActivity.this,versionMap.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
            if (apiBsaeMethod == m_apiBoxing) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BoxingActivity.this,macMap.toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                    Toast.makeText(BoxingActivity.this, FBKSpliceBle.bytesToHexString(systemData),Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BoxingActivity.this,modelString,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BoxingActivity.this,serialNumber,Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BoxingActivity.this,manufacturerName,Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(BoxingActivity.this,showString,Toast.LENGTH_SHORT).show();
                }
            });
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boxing);

        m_deviceArray.clear();
        for (int i = 0; i < 8; i++) {
            FBKApiBoxing apiBoxing = new FBKApiBoxing(BoxingActivity.this, m_apiBoxingCallBack);
            apiBoxing.registerBleListenerReceiver();

            DeviceClass myDevice = new DeviceClass();
            myDevice.setBleDevice(apiBoxing);
            myDevice.setDeviceId("");
            myDevice.setDeviceName("");
            myDevice.setAvailable(true);
            myDevice.setDeviceInfo(null);
            myDevice.setConnectStatus(FBKBleDeviceStatus.BleTurnOff);
            m_deviceArray.add(myDevice);

            if (i == 0) {
                m_apiBoxing = apiBoxing;
            }
        }

        getDefaultList();
        initView();
    }

    public void getDefaultList() {
        m_boxingArray.clear();
        m_boxingArray.add("Set Axis switch on");
        m_boxingArray.add("Set Axis switch off");
        m_boxingArray.add("Set Time");
        m_boxingArray.add("Get Record");
        m_boxingArray.add("Read Battery Power");
        m_boxingArray.add("Read Firmware Version");
        m_boxingArray.add("Read Hardware Version");
        m_boxingArray.add("Read Software Version");
        m_boxingArray.add("Private get version");
        m_boxingArray.add("Private get mac");
        m_boxingArray.add("Private Enter OTA Mode");
        m_boxingArray.add("Read System data");
        m_boxingArray.add("Read Model String");
        m_boxingArray.add("Read Serial Number");
        m_boxingArray.add("Read Manufacturer Name");

        if (m_dateText != null && m_boxingAdapter != null) {
            m_boxNumberText.setText("Total Punches\n"+"--");
            m_boxTypeText.setText("Punching Type\n"+"--");
            m_outTimeText.setText("Punching Time\n"+"--");
            m_inTimeText.setText("Withdrawing Time\n"+"--");
            m_powerText.setText("G-Force\n"+"--");
            m_speedText.setText("Punching Speed\n"+"--");
            m_distanceText.setText("Punching Distance\n"+"--");
            m_dateText.setText("Punching Time\n"+"--");
            m_boxingAdapter.notifyDataSetChanged();
        }
    }


    private void getDeviceStatusString(FBKBleDeviceStatus status) {
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
        }
        else if (status == FBKBleDeviceStatus.BleTurnOff) {
            m_statusText.setText("BleTurnOff");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_apiBoxing.disconnectBle();
        m_apiBoxing.unregisterBleListenerReceiver();
    }

    private void initView() {

        m_statusTitle = (TextView) this.findViewById(R.id.boxing_text_title);
        m_statusText = (TextView) this.findViewById(R.id.boxing_text_status);
        m_boxNumberText = (TextView) this.findViewById(R.id.boxing_text_boxNumber);
        m_boxTypeText = (TextView) this.findViewById(R.id.boxing_text_boxtype);
        m_outTimeText = (TextView) this.findViewById(R.id.boxing_text_outtime);
        m_inTimeText = (TextView) this.findViewById(R.id.boxing_text_intime);
        m_powerText = (TextView) this.findViewById(R.id.boxing_text_power);
        m_speedText = (TextView) this.findViewById(R.id.boxing_text_speed);
        m_distanceText = (TextView) this.findViewById(R.id.boxing_text_distance);
        m_dateText = (TextView) this.findViewById(R.id.boxing_text_date);
        m_axisText = (TextView) this.findViewById(R.id.boxing_text_axis);

        m_boxingListView = (ListView) this.findViewById(R.id.boxing_list);
        m_boxingAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_boxingArray.size();
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
                LayoutInflater inflater = BoxingActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                title.setText((position+1) + "、" + m_boxingArray.get(position));

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_boxingListView.setAdapter(m_boxingAdapter);
        m_boxingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    m_apiBoxing.setBoxingAxisSwitch(true);
                }
                else if (position == 1) {
                    m_apiBoxing.setBoxingAxisSwitch(false);
                }
                else if (position == 2) {
                    m_apiBoxing.setUTC(new Date());
                }
                else if (position == 3) {
                    m_apiBoxing.getBoxingRecord();
                }
                else if (position == 4) {
                    m_apiBoxing.readDeviceBatteryPower();
                }
                else if (position == 5) {
                    m_apiBoxing.readFirmwareVersion();
                }
                else if (position == 6) {
                    m_apiBoxing.readHardwareVersion();
                }
                else if (position == 7) {
                    m_apiBoxing.readSoftwareVersion();
                }
                else if (position == 8) {
                    m_apiBoxing.getPrivateVersion();
                }
                else if (position == 9) {
                    m_apiBoxing.getPrivateMacAddress();
                }
                else if (position == 10) {
                    m_apiBoxing.enterOTAMode();
                }
                else if (position == 11) {
                    m_apiBoxing.readSystemId();
                }
                else if (position == 12) {
                    m_apiBoxing.readModelString();
                }
                else if (position == 13) {
                    m_apiBoxing.readSerialNumber();
                }
                else if (position == 14) {
                    m_apiBoxing.readManufacturerName();
                }
            }
        });
    }

    public void backAction(View view) {
        for (int i = 0; i < m_deviceArray.size(); i++) {
            DeviceClass myDevice = m_deviceArray.get(i);
            FBKApiBoxing apiBoxing = (FBKApiBoxing) myDevice.getBleDevice();
            if (!myDevice.isAvailable()) {
                apiBoxing.disconnectBle();
            }
        }
        finish();
    }

    public void changeAction(View view) {
        int myRow = deviceRow+1;
        if (myRow == 8) {
            myRow = 0;
        }

        getDefaultList();
        DeviceClass myDevice = m_deviceArray.get(myRow);
        if (!myDevice.isAvailable()) {
            deviceRow = myRow;
            int showRow = myRow + 1;
            m_statusTitle.setText(showRow+": "+myDevice.getBluetoothDevice().getName());
            m_apiBoxing = (FBKApiBoxing) myDevice.getBleDevice();
            getDeviceStatusString(myDevice.getConnectStatus());
        }
        else {
            myRow = 0;
            deviceRow = 0;
            myDevice = m_deviceArray.get(myRow);
            if (!myDevice.isAvailable()) {
                int showRow = myRow + 1;
                m_statusTitle.setText(showRow+": "+myDevice.getBluetoothDevice().getName());
                m_apiBoxing = (FBKApiBoxing) myDevice.getBleDevice();
                getDeviceStatusString(myDevice.getConnectStatus());
            }
        }
    }


    public void deviceAction(View view) {
        Intent intent = new Intent(BoxingActivity.this, DevicesScanActivity.class);
        startActivityForResult(intent,BOXING_TO_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BOXING_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
            Log.e(TAG,"onActivityResult");
            BluetoothDevice bluetoothDevice = data.getParcelableExtra("bluetooth");

            for (int i = 0; i < m_deviceArray.size(); i++) {
                DeviceClass myDevice = m_deviceArray.get(i);
                FBKApiBoxing apiBoxing = (FBKApiBoxing) myDevice.getBleDevice();
                if (myDevice.isAvailable()) {
                    myDevice.setBluetoothDevice(bluetoothDevice);
                    myDevice.setAvailable(false);
                    apiBoxing.connectBluetooth(bluetoothDevice);
                    if (i == 0) {
                        int row = i + 1;
                        m_statusTitle.setText(row+": "+bluetoothDevice.getName());
                    }
                    break;
                }
            }
        }
    }


}
