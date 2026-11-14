/****************************************************************************************
 * 文件名称：BellListActivity.java
 * 内容摘要：壶铃数据
 * 版本编号：1.0.1
 * 创建日期：2019年08月23日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.KettleBell;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.kickboxing.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BellListActivity extends Activity {

    // TAG值
    private static final String TAG = BellListActivity.class.getSimpleName();

    // ListView
    private ListView m_bellListView;

    // ListView BaseAdapter
    private BaseAdapter m_bellAdapter;

    // List
    private static List<Object> m_bellArray = new ArrayList<>();

    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_belllist);

        Intent intent = getIntent();
        m_bellArray = (List<Object>) intent.getSerializableExtra("data");
        initView();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_bellListView = (ListView) this.findViewById(R.id.bell_list);
        m_bellAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_bellArray.size();
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
                LayoutInflater inflater = BellListActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_bell,null);
                }

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_bell_choose);
                chooseImg.setVisibility(View.VISIBLE);

                TextView title = (TextView) convertView.findViewById(R.id.list_bell_name);
                Map<String,Object> dataDic = (Map<String,Object>) m_bellArray.get(position);
                String weightGrade = "User Number: P" + (String) dataDic.get("weightGrade");
                String userNumber = "Weight: " + (String) dataDic.get("userNumber")+"LB";
                String sportNumber = "Number: " + (String) dataDic.get("sportNumber");
                String startTime = "startTime: " + (String) dataDic.get("startTime");
                String endTime = "endTime: " + (String) dataDic.get("endTime");
                title.setText(weightGrade+"\n"+userNumber+"\n"+sportNumber+"\n"+startTime+"\n"+endTime);

                return convertView;
            }
        };


        m_bellListView.setAdapter(m_bellAdapter);
        m_bellListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,Object> dataDic = (Map<String,Object>) m_bellArray.get(position);
                List<Object> sportList = (List<Object>) dataDic.get("sportList");

                if (sportList != null) {
                    Intent intent = new Intent(BellListActivity.this, SportListActivity.class);
                    intent.putExtra("data",(Serializable)sportList);
                    startActivity(intent);
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


}
