/****************************************************************************************
 * 文件名称：WiFiListActivity.java
 * 内容摘要：WiFi列表
 * 版本编号：1.0.1
 * 创建日期：2019年08月26日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.HubConfig;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;

import com.app.kickboxing.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WiFiListActivity extends Activity {

    // TAG值
    private static final String TAG = WiFiListActivity.class.getSimpleName();

    public static int WIFI_ACTIVITY_BACK = 11001;

    // ListView
    private ListView m_wifiListView;

    // ListView BaseAdapter
    private BaseAdapter m_wifiAdapter;

    // List
    private static List<Object> m_wifiArray = new ArrayList<>();

    private Map<String,String> m_wifiInfoMap = new HashMap<>();

    private TextView m_wifiSSID;

    private EditText m_wifiPassword;


    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifilist);

        Intent intent = getIntent();
        m_wifiArray = (List<Object>) intent.getSerializableExtra("data");
        Log.e(TAG,m_wifiArray.toString());
        initView();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_wifiSSID = (TextView) this.findViewById(R.id.wifi_ssid);
        m_wifiPassword = (EditText) this.findViewById(R.id.wifi_password);
        m_wifiListView = (ListView) this.findViewById(R.id.wifi_list);
        m_wifiAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_wifiArray.size();
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
                LayoutInflater inflater = WiFiListActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                Map<String,Object> dataDic = (Map<String,Object>) m_wifiArray.get(position);
                String ssidName = (String) dataDic.get("ssidName");
                String macAddress = (String) dataDic.get("macAddress");
                String encryptionName = (String) dataDic.get("encryptionName");
                String algorithmName = (String) dataDic.get("algorithmName");
                String viewText = ssidName+"\n"+encryptionName+"/"+algorithmName+"   "+macAddress;
                title.setText(viewText);

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_wifiListView.setAdapter(m_wifiAdapter);
        m_wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> dataDic = (Map<String,String>) m_wifiArray.get(position);
                String ssidName = (String) dataDic.get("ssidName");
                m_wifiSSID.setText(ssidName);
                m_wifiInfoMap = dataDic;
            }
        });
    }


    /************************************************************************************
     * 方法名称：saveAction
     * 功能描述：保存
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    public void saveAction(View view) {
        m_wifiInfoMap.put("password",m_wifiPassword.getText().toString());

        Intent backIntent = new Intent();
        backIntent.putExtra("data",(Serializable)m_wifiInfoMap);
        setResult(WIFI_ACTIVITY_BACK,backIntent);
        finish();
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

}
