/****************************************************************************************
 * 文件名称：SportListActivity.java
 * 内容摘要：壶铃详细数据
 * 版本编号：1.0.1
 * 创建日期：2019年08月23日
 ****************************************************************************************/

package com.app.kickboxing.Activitys.presentation.KettleBell;

import android.app.Activity;
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

import com.app.kickboxing.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SportListActivity extends Activity {

    // TAG值
    private static final String TAG = SportListActivity.class.getSimpleName();

    // ListView
    private ListView m_sportListView;

    // ListView BaseAdapter
    private BaseAdapter m_sportAdapter;

    // List
    private static List<Object> m_sportArray = new ArrayList<>();

    /************************************************************************************
     * 方法名称：onCreate
     * 功能描述：初始化
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timelist);

        Intent intent = getIntent();
        m_sportArray = (List<Object>) intent.getSerializableExtra("data");
        Log.e(TAG,m_sportArray.toString());
        initView();
    }


    /************************************************************************************
     * 方法名称：initView
     * 功能描述：获取成员变量
     * 输入参数：
     * 返回数据：
     ************************************************************************************/
    private void initView() {
        m_sportListView = (ListView) this.findViewById(R.id.sport_list);
        m_sportAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return m_sportArray.size();
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
                LayoutInflater inflater = SportListActivity.this.getLayoutInflater();
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.listview_main,null);
                }

                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
                Map<String,Object> dataDic = (Map<String,Object>) m_sportArray.get(position);
                String sportType = (String) dataDic.get("sportType");
                String sportTime = (String) dataDic.get("sportTime");
                String viewText = sportType+"         "+sportTime;
                if (sportType.equals("0")) {
                    viewText = "OTHER TYPE" + "   (" + sportTime + "ms)";
                }
                else if (sportType.equals("1")) {
                    viewText = "ATLAS SWING" + "   (" + sportTime + "ms)";
                }
                else if (sportType.equals("2")) {
                    viewText = "PULL OVER" + "   (" + sportTime + "ms)";
                }
                else if (sportType.equals("3")) {
                    viewText = "HIGH PULL" + "   (" + sportTime + "ms)";
                }
                else if (sportType.equals("4")) {
                    viewText = "EXTENSIONE" + "   (" + sportTime + "ms)";
                }

                title.setText(viewText);

                ImageView chooseImg = (ImageView) convertView.findViewById(R.id.list_image_choose);
                chooseImg.setVisibility(View.INVISIBLE);

                return convertView;
            }
        };


        m_sportListView.setAdapter(m_sportAdapter);
        m_sportListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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
