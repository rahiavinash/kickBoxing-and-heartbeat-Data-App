//package com.app.kickboxing.Activitys.Fight;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.bluetooth.BluetoothDevice;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import com.onecoder.fitblekit.API.Base.FBKApiBsaeMethod;
//import com.onecoder.fitblekit.API.Base.FBKBleBaseInfo;
//import com.onecoder.fitblekit.API.Fight.FBKApiFight;
//import com.onecoder.fitblekit.API.Fight.FBKApiFightCallBack;
//import com.onecoder.fitblekit.Ble.FBKBleDevice.FBKBleDeviceStatus;
//import com.onecoder.fitblekit.Protocol.Fight.Command.FBKFightSandbag;
//import com.onecoder.fitblekit.Protocol.Fight.Protocol.FBKFightInfo;
//import com.ScanDevices.Activitys.fitblekitdemo.app.DevicesScanActivity;
//import com.app.kickboxing.R;
//
//import java.util.Map;
//
//import static com.ScanDevices.Activitys.fitblekitdemo.app.DevicesScanActivity.SCAN_ACTIVITY_BACK;
//
//public class FightActivity extends Activity {
//
//    // TAG值
//    private static final String TAG = FightActivity.class.getSimpleName();
//
//    // 获取扫描设备TAG
//    public static int FIGHTING_TO_SCAN = 16001;
//
//    // 蓝牙设备
//    private BluetoothDevice m_bluetoothDevice;
//
//    // 设备API
//    private FBKApiFight m_fightApi;
//
//    private FBKFightSandbag m_sandbag;
//
//    private FBKApiFightCallBack m_apiFightCallBack = initFightCallBack();
//
//    private FrameLayout m_bagView;
//    private Button   m_connectBtn;
//    private TextView m_blenameText;
//    private TextView m_numberText;
//    private TextView m_freText;
//    private TextView m_powerText;
//    private TextView m_lengthText;
//    private TextView m_widthText;
//    private TextView m_heightText;
//    private TextView m_weightText;
//    private TextView m_typeText;
//    private TextView m_sensText;
//
//    /************************************************************************************
//     * 方法名称：onCreate
//     * 功能描述：初始化
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_fighting);
//
//        m_fightApi = new FBKApiFight(FightActivity.this, m_apiFightCallBack);
//        m_fightApi.registerBleListenerReceiver();
//
//        m_sandbag = new FBKFightSandbag();
//        m_sandbag.setSandbagLength(400);
//        m_sandbag.setSandbagWidth(400);
//        m_sandbag.setSandbagHight(2000);
//        m_sandbag.setSandbagWeight(100);
//        m_sandbag.setSandbagType(0);
//        m_sandbag.setSandbagSensitivity(13);
//
//        initView();
//    }
//
//
//    /************************************************************************************
//     * 方法名称：onDestroy
//     * 功能描述：销毁页面
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (m_fightApi != null) {
//            m_fightApi.disconnectBle();
//            m_fightApi.unregisterBleListenerReceiver();
//        }
//    }
//
//
//    /************************************************************************************
//     * 方法名称：onActivityResult
//     * 功能描述：onActivityResult
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == FIGHTING_TO_SCAN && resultCode == SCAN_ACTIVITY_BACK) {
//            m_bluetoothDevice = data.getParcelableExtra("bluetooth");
//            m_fightApi.connectBluetooth(m_bluetoothDevice);
//            m_blenameText.setText(m_bluetoothDevice.getName());
//        }
//    }
//
//
//    private void initView() {
//        m_bagView = (FrameLayout) this.findViewById(R.id.fight_frame_view);
//        m_connectBtn = (Button) this.findViewById(R.id.fight_button_connect);
//        m_blenameText = (TextView) this.findViewById(R.id.fight_text_blename);
//        m_numberText = (TextView) this.findViewById(R.id.fight_text_number);
//        m_freText = (TextView) this.findViewById(R.id.fight_text_fre);
//        m_powerText = (TextView) this.findViewById(R.id.fight_text_power);
//        m_lengthText = (TextView) this.findViewById(R.id.fight_text_length);
//        m_widthText = (TextView) this.findViewById(R.id.fight_text_width);
//        m_heightText = (TextView) this.findViewById(R.id.fight_text_height);
//        m_weightText = (TextView) this.findViewById(R.id.fight_text_weight);
//        m_typeText = (TextView) this.findViewById(R.id.fight_text_type);
//        m_sensText = (TextView) this.findViewById(R.id.fight_text_sens);
//
//        m_lengthText.setText(String.valueOf(m_sandbag.getSandbagLength()));
//        m_widthText.setText(String.valueOf(m_sandbag.getSandbagWidth()));
//        m_heightText.setText(String.valueOf(m_sandbag.getSandbagHight()));
//        m_weightText.setText(String.valueOf(m_sandbag.getSandbagWeight()));
//        m_sensText.setText(String.valueOf(m_sandbag.getSandbagSensitivity()));
//
//        if (m_sandbag.getSandbagType() == 0) {
//            m_typeText.setText("Hang");
//        }
//        else {
//            m_typeText.setText("Stand");
//        }
//    }
//
//    public void viewOnClick(View view) {
//        switch (view.getId()) {
//            case R.id.fight_button_back:
//                finish();
//                break;
//
//            case R.id.fight_button_connect:
//                if (!m_fightApi.isConnected) {
//                    Intent intent = new Intent(FightActivity.this, DevicesScanActivity.class);
//                    startActivityForResult(intent,FIGHTING_TO_SCAN);
//                }
//                else {
//                    m_fightApi.disconnectBle();
//                }
//                break;
//
//            case R.id.fight_button_dfu:
//                enterDfuMode();
//                break;
//
//            case R.id.fight_button_turnoff:
//                turnoffDevice();
//                break;
//
//            case R.id.fight_button_bag:
//                if (m_fightApi.isConnected) {
//                    m_bagView.setVisibility(View.VISIBLE);
//                }
//                else {
//                    Toast.makeText(FightActivity.this, "No Device",Toast.LENGTH_SHORT).show();
//                }
//                break;
//
//            case R.id.fight_text_length:
//                setCmdNumber(0);
//                break;
//
//            case R.id.fight_text_width:
//                setCmdNumber(1);
//                break;
//
//            case R.id.fight_text_height:
//                setCmdNumber(2);
//                break;
//
//            case R.id.fight_text_weight:
//                setCmdNumber(3);
//                break;
//
//            case R.id.fight_text_type:
//                setFightType();
//                break;
//
//            case R.id.fight_text_sens:
//                setCmdNumber(5);
//                break;
//
//            case R.id.fight_text_cancel:
//                m_bagView.setVisibility(View.INVISIBLE);
//                break;
//
//            case R.id.fight_text_send:
//                m_bagView.setVisibility(View.INVISIBLE);
//                if (m_fightApi.isConnected) {
//                    m_fightApi.setSandbag(m_sandbag);
//                }
//                break;
//
//            default:
//                break;
//        }
//    }
//
//
//    /************************************************************************************
//     * 方法名称：enterDfuMode
//     * 功能描述：enterDfuMode
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    private void enterDfuMode() {
//        if (m_fightApi.isConnected) {
//            AlertDialog.Builder bulder = new AlertDialog.Builder(FightActivity.this);
//            bulder.setCancelable(false);
//            bulder.setTitle("The device will enter upgrade mode");
//            bulder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    return;
//                }
//            });
//
//            bulder.setPositiveButton("Done",new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    m_fightApi.enterDfuMode();
//                }
//            });
//
//            bulder.show();
//        }
//        else {
//            Toast.makeText(FightActivity.this, "No Device",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    /************************************************************************************
//     * 方法名称：turnoffDevice
//     * 功能描述：turnoffDevice
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    private void turnoffDevice() {
//        if (m_fightApi.isConnected) {
//            AlertDialog.Builder bulder = new AlertDialog.Builder(FightActivity.this);
//            bulder.setCancelable(false);
//            bulder.setTitle("Turn off device");
//            bulder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    return;
//                }
//            });
//
//            bulder.setPositiveButton("Done",new DialogInterface.OnClickListener(){
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    m_fightApi.turnOffDevice();
//                }
//            });
//
//            bulder.show();
//        }
//        else {
//            Toast.makeText(FightActivity.this, "No Device",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//
//    /************************************************************************************
//     * 方法名称：setCmdNumber
//     * 功能描述：setCmdNumber
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    private void setCmdNumber(int cmdId) {
//        AlertDialog.Builder bulder = new AlertDialog.Builder(FightActivity.this);
//        bulder.setCancelable(false);
//        if (cmdId == 0) {
//            bulder.setTitle("Punching bag a(mm)");
//        }
//        else if (cmdId == 1) {
//            bulder.setTitle("Punching bag b(mm)");
//        }
//        else if (cmdId == 2) {
//            bulder.setTitle("Punching bag c(mm)");
//        }
//        else if (cmdId == 3) {
//            bulder.setTitle("Punching bag weight(kg)");
//        }
//        else if (cmdId == 5) {
//            bulder.setTitle("Punching bag sensitivity (5~150)");
//        }
//        EditText enterText = new EditText(this);
//        enterText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
//        bulder.setView(enterText);
//        bulder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                return;
//            }
//        });
//
//        bulder.setPositiveButton("Done",new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (enterText.getText().toString().length() > 0) {
//                    if (cmdId == 0) {
//                        m_lengthText.setText(enterText.getText().toString());
//                        m_sandbag.setSandbagLength(Integer.valueOf(enterText.getText().toString()));
//                    }
//                    else if (cmdId == 1) {
//                        m_widthText.setText(enterText.getText().toString());
//                        m_sandbag.setSandbagWidth(Integer.valueOf(enterText.getText().toString()));
//                    }
//                    else if (cmdId == 2) {
//                        m_heightText.setText(enterText.getText().toString());
//                        m_sandbag.setSandbagHight(Integer.valueOf(enterText.getText().toString()));
//                    }
//                    else if (cmdId == 3) {
//                        m_weightText.setText(enterText.getText().toString());
//                        m_sandbag.setSandbagWeight(Integer.valueOf(enterText.getText().toString()));
//                    }
//                    else if (cmdId == 5) {
//                        int valueNo = Integer.valueOf(enterText.getText().toString());
//                        if (valueNo < 5 || valueNo > 150) {
//                            Toast.makeText(FightActivity.this, "Punching bag sensitivity range 5~150",Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        m_sensText.setText(enterText.getText().toString());
//                        m_sandbag.setSandbagSensitivity(valueNo);
//                    }
//                }
//            }
//        });
//
//        bulder.show();
//    }
//
//
//    /************************************************************************************
//     * 方法名称：setFightType
//     * 功能描述：setFightType
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    private void setFightType() {
//        AlertDialog.Builder bulder = new AlertDialog.Builder(FightActivity.this);
//        bulder.setCancelable(false);
//        bulder.setTitle("Punching bag type");
//        bulder.setNegativeButton("Hang",new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                m_typeText.setText("Hang");
//                m_sandbag.setSandbagType(0);
//            }
//        });
//
//        bulder.setPositiveButton("Stand",new DialogInterface.OnClickListener(){
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                m_typeText.setText("Stand");
//                m_sandbag.setSandbagType(1);
//            }
//        });
//
//        bulder.show();
//    }
//
//
//    /************************************************************************************
//     * 方法名称：initFightCallBack
//     * 功能描述：initFightCallBack
//     * 输入参数：
//     * 返回数据：
//     ************************************************************************************/
//    private FBKApiFightCallBack initFightCallBack() {
//        FBKApiFightCallBack apiFightCallBack = new FBKApiFightCallBack() {
//            @Override
//            public void realTimeFight(FBKFightInfo fightInfo, FBKApiFight apiFight) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
////                        double myKg = 0.01490 * m_sandbag.getSandbagWeight() * fightInfo.getStrengthIndex();
//                        int myKg = fightInfo.getStrengthIndex();
//                        m_numberText.setText(String.valueOf(fightInfo.getFightNumbers()));
//                        m_freText.setText(String.valueOf(fightInfo.getFightFrequency()));
//                        m_powerText.setText(String.format("%d",myKg));
//                    }
//                });
//            }
//
//            @Override
//            public void enterDfuResult(boolean status, FBKApiFight apiFight) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (status){
//                            Toast.makeText(FightActivity.this,"The device enter upgrade mode succeed.",Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(FightActivity.this,"The device enter upgrade mode failed.",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void turnOffDeviceResult(boolean status, FBKApiFight apiFight) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (status){
//                            Toast.makeText(FightActivity.this,"Turn off device succeed.",Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(FightActivity.this,"Turn off device failed.！",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void setSandbagResult(boolean status, FBKApiFight apiFight) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (status){
//                            Toast.makeText(FightActivity.this,"Set punching bag information succeed.",Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            Toast.makeText(FightActivity.this,"Set punching bag information failed.",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void bleConnectError(String error, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void bleConnectStatus(FBKBleDeviceStatus status, FBKApiBsaeMethod apiBsaeMethod) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (status == FBKBleDeviceStatus.BleConnecting) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_button_connect);
//                            m_connectBtn.setText("Connecting...");
//                        }
//                        else if (status == FBKBleDeviceStatus.BleConnected) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_connect_red);
//                            m_connectBtn.setText("Disconnect");
//                        }
//                        else if (status == FBKBleDeviceStatus.BleReconnect) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_button_connect);
//                            m_connectBtn.setText("Connecting...");
//
//                            m_numberText.setText("--");
//                            m_freText.setText("--");
//                            m_powerText.setText("--");
//                        }
//                        else if (status == FBKBleDeviceStatus.BleDisconnected) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_button_connect);
//                            m_connectBtn.setText("Device");
//
//                            m_numberText.setText("--");
//                            m_freText.setText("--");
//                            m_powerText.setText("--");
//                        }
//                        else if (status == FBKBleDeviceStatus.BleTurnOn) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_button_connect);
//                            m_connectBtn.setText("Device");
//                        }
//                        else if (status == FBKBleDeviceStatus.BleTurnOff) {
//                            m_connectBtn.setBackgroundResource(R.drawable.circel_button_connect);
//                            m_connectBtn.setText("Device");
//                        }
//                    }
//                });
//            }
//
//            @Override
//            public void bleConnectStatusLog(String infoString, FBKApiBsaeMethod apiBsaeMethod) {
//
//            }
//
//            @Override
//            public void batteryPower(int power, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void protocolVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void firmwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void hardwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void softwareVersion(String version, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void privateVersion(Map<String, String> versionMap, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void privateMacAddress(Map<String, String> macMap, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void bleConnectInfo(String infoString, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void deviceSystemData(byte[] systemData, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void deviceModelString(String modelString, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void deviceSerialNumber(String serialNumber, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void deviceManufacturerName(String manufacturerName, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//
//            @Override
//            public void deviceBaseInfo(FBKBleBaseInfo baseInfo, FBKApiBsaeMethod apiBsaeMethod) {
//            }
//        };
//
//        return apiFightCallBack;
//    }
//
//}
