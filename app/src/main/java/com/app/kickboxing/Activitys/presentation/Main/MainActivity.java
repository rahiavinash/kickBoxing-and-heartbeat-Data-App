
package com.app.kickboxing.Activitys.presentation.Main;

import android.os.Bundle;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;

import com.onecoder.fitblekit.Tools.FBKTemAlgorithm;
import com.app.kickboxing.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // TAG值
    private static final String TAG = MainActivity.class.getSimpleName();
    private GridView m_mainListView;
    private BaseAdapter m_mainListAdapter;

    private NavController navController;
    private TextView mainText;

    private static List<String> m_mainArray = new ArrayList<>();
    private FBKTemAlgorithm myTemAlgorithm = new FBKTemAlgorithm();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        NavHostFragment navHostController = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.fragmentContainerView);
//        navController = navHostController.getNavController();


      //  CrashReport.initCrashReport(getApplicationContext(), "6782ea0e3d", false);

//        m_mainArray.clear();
//        m_mainArray.add("Fighting");
//        m_mainArray.add("Running");
//        m_mainArray.add("ECG");
//        m_mainArray.add("Power");
//        m_mainArray.add("ArmBand");
//        m_mainArray.add("HubConfig");
//        m_mainArray.add("Boxing");
//        m_mainArray.add("HeartBeat");
//        m_mainArray.add("Cadence");
//        m_mainArray.add("Skipping");
//        m_mainArray.add("KTBBQ");
//        m_mainArray.add("New Scale");
//        m_mainArray.add("Old Tracker");
//        m_mainArray.add("New Tracker");
//        m_mainArray.add("KettleBell");
//        m_mainArray.add("BikeComputer");
//        m_mainArray.add("Function");

//        initView();
//        verifyStoragePermissions(this);
//
//        FBKToolSport sportTool = new FBKToolSport();
//        double cal = sportTool.speedToCalories(20.0,30.0);
//        double slope = sportTool.groundSlopeCalc(30.0,10);
//        double changeHi = sportTool.altitudeChange(10,slope);
//
//        FBKXYAlgorithms myXY = new FBKXYAlgorithms();
//        List<Double> myList =  myXY.dataXYAlgorithms(0, 23050, FBKXYChartType.XYChartDistance);
//        Log.e(TAG,myList.toString());
//
//        double pressure = 99930.546875; // 116.74623012542725
//        double pressureAltitude = 44330 * (1.0 - Math.pow((pressure/101325.0), (1.0/5.255)));
//        Log.e(TAG,"pressureAltitude -------------- "+pressureAltitude);
//
//        Log.e(TAG,"library -------------- "+System.getProperty("java.library.path"));

//        FBKECGFilter myFilter = new FBKECGFilter();
//        double ecg[] = new double[]{-13228,-13332,-13402,-13336,-13231,-13221,-13327,-13404,-13344,-13224,-13220,-13323,-13396,-13340,-13228,-13221,-13319,-13388,-13323,-13226,-13222,-13320,-13394,-13322,-13215,-13220,-13327,-13397,-13333,-13222,-13220,-13330,-13406,-13339,-13224,-13227,-13334,-13402,-13336,-13229,-13224,-13333,-13405,-13332,-13227,-13224,-13324,-13379,-13286,-13164,-13161,-13285,-13370,-13320,-13244,-13249,-13356,-13424,-13349,-13233,-13225,-13340,-13399,-13328,-13217,-13222,-13325,-13395,-13330,-13228,-13219,-13319,-13396,-13331,-13221,-13215,-13322,-13397,-13319,-13211,-13220,-13337,-13400,-13327,-13216,-13210,-13319,-13391,-13326,-13220,-13216,-13328,-13386,-13319,-13217,-13212,-13312,-13379,-13315,-13208,-13208,-13311,-13375,-13316,-13207,-13204,-13317,-13382,-13309,-13205,-13207,-13319,-13392,-13318,-13206,-13211,-13318,-13395,-13333,-13219,-13224,-13319,-13395,-13336,-13231,-13229,-13339,-13405,-13341,-13236,-13234,-13335,-13404,-13345,-13236,-13234,-13337,-13401,-13337,-13235,-13233,-13328,-13401,-13342,-13238,-13235,-13341,-13400,-13340,-13238,-13225,-13337,-13405,-13343,-13245,-13232,-13334,-13404,-13332,-13226,-13234,-13340,-13402,-13340,-13224,-13228,-13333,-13400,-13332,-13238,-13243,-13337,-13400,-13337,-13233,-13230,-13331,-13402,-13340,-13232,-13232,-13334,-13401,-13348,-13234,-13229,-13335,-13412,-13342,-13238,-13241,-13334,-13401,-13343,-13232,-13237,-13335,-13405,-13342,-13235,-13224,-13324,-13391,-13328,-13225,-13210,-13316,-13387,-13330,-13226,-13219,-13321,-13392,-13332,-13230,-13230,-13331,-13396,-13331,-13231,-13227,-13327,-13397,-13330,-13226,-13222,-13328,-13396,-13336,-13228,-13223,-13332,-13389,-13313,-13194,-13174,-13276,-13350,-13307,-13237,-13260,-13364,-13424,-13352,-13245,-13229,-13325,-13385,-13330,-13237,-13233,-13328,-13398,-13325,-13224,-13230,-13329,-13396,-13331,-13221,-13229,-13325,-13389,-13324,-13220,-13221,-13325,-13396,-13325,-13222,-13225,-13324,-13393,-13333,-13222,-13211,-13321,-13384,-13323,-13215,-13220,-13325,-13387,-13313,-13214,-13213,-13311,-13380,-13318,-13212,-13205,-13311,-13379,-13317,-13214,-13219,-13318,-13386,-13321,-13215,-13216,-13321,-13381,-13320,-13220,-13220,-13324,-13390,-13335,-13230,-13228,-13336,-13401,-13333,-13233,-13235,-13338,-13396,-13339,-13228,-13231,-13337,-13403,-13337,-13232,-13235,-13345,-13400,-13325,-13225,-13237,-13338,-13400,-13331,-13225,-13231,-13332,-13390,-13328,-13226,-13232,-13337,-13400,-13332,-13227,-13224,-13335,-13400,-13326,-13220,-13231,-13336,-13394,-13325,-13220,-13234,-13340,-13399,-13331,-13224,-13231,-13332,-13396,-13325,-13221,-13221,-13329,-13403,-13327,-13218,-13227,-13326,-13391,-13324,-13218,-13222,-13334,-13395,-13321,-13218,-13223,-13329,-13393,-13326,-13217,-13223,-13324,-13391,-13322,-13212,-13213,-13323,-13388,-13313,-13215,-13229,-13337,-13395,-13316,-13213,-13224,-13340,-13393,-13318,-13216,-13225,-13331,-13398,-13321,-13201,-13224,-13345,-13407,-13326,-13222,-13226,-13339,-13384,-13282,-13165,-13166,-13292,-13373,-13317,-13240,-13255,-13369,-13429,-13340,-13213,-13227,-13337,-13398,-13320,-13219,-13219,-13334,-13397,-13319,-13220,-13221,-13333,-13396,-13317,-13207,-13222,-13330,-13394,-13320,-13207,-13215,-13323,-13388,-13313,-13204,-13216,-13327,-13387,-13314,-13205,-13216,-13330,-13387,-13310,-13209,-13213,-13328,-13388,-13305,-13197,-13212,-13327,-13379,-13302,-13198,-13201,-13318,-13380,-13302,-13193,-13205,-13324,-13379,-13298,-13197,-13222,-13338,-13392,-13306,-13205,-13217,-13341,-13397,-13311,-13218,-13230,-13340,-13406,-13327,-13216,-13232,-13348,-13407,-13333,-13221,-13236,-13343,-13406,-13333,-13220,-13230,-13345,-13415,-13336,-13220,-13228,-13340,-13404,-13329,-13221,-13226,-13349,-13417,-13329,-13231,-13239,-13343,-13407,-13326,-13225,-13236,-13355,-13416,-13332,-13219,-13225,-13348,-13399,-13335,-13223,-13240,-13344,-13399,-13329,-13224,-13234,-13349,-13414,-13333,-13220,-13232,-13341,-13410,-13329,-13222,-13239,-13347,-13408,-13329,-13220,-13234,-13344,-13400,-13321,-13214,-13233,-13345,-13395,-13318,-13215,-13229,-13343,-13395,-13315,-13212,-13222,-13339,-13404,-13332,-13222,-13231,-13339,-13406,-13316,-13214,-13234,-13347,-13400,-13317,-13210,-13225,-13345,-13400,-13313,-13215,-13235,-13346,-13405,-13321,-13205};
//        double resultEcg[] = myFilter.ecgFilterC(ecg);
//
//        String ecgString1 = "";
//        for (int i = 0; i < resultEcg.length; i++) {
//            ecgString1 = ecgString1 +"," + resultEcg[i];
//        }
//        Log.e(TAG,ecgString1);
//
//        double ecg1[] = new double[]{-13207,-13305,-13340,-13266,-13173,-13202,-13352,-13437,-13358,-13244,-13244,-13352,-13407,-13315,-13209,-13221,-13342,-13396,-13315,-13202,-13223,-13334,-13389,-13305,-13199,-13217,-13326,-13382,-13300,-13200,-13213,-13327,-13388,-13300,-13192,-13215,-13323,-13388,-13306,-13203,-13218,-13325,-13388,-13307,-13198,-13213,-13324,-13376,-13299,-13197,-13216,-13314,-13373,-13296,-13187,-13204,-13319,-13381,-13292,-13190,-13212,-13319,-13376,-13299,-13198,-13206,-13325,-13388,-13307,-13196,-13210,-13328,-13380,-13312,-13212,-13226,-13346,-13394,-13301,-13208,-13237,-13349,-13405,-13319,-13213,-13227,-13331,-13397,-13313,-13207,-13218,-13342,-13397,-13312,-13205,-13216,-13337,-13402,-13314,-13206,-13225,-13343,-13396,-13317,-13209,-13222,-13343,-13392,-13309,-13203,-13224,-13340,-13390,-13318,-13202,-13222,-13344,-13387,-13312,-13205,-13223,-13341,-13397,-13315,-13209,-13223,-13344,-13396,-13314,-13207,-13227,-13342,-13395,-13310,-13189,-13220,-13338,-13388,-13307,-13199,-13228,-13340,-13391,-13312,-13200,-13210,-13336,-13393,-13316,-13209,-13222,-13339,-13396,-13299,-13195,-13221,-13338,-13387,-13300,-13186,-13214,-13341,-13386,-13302,-13196,-13224,-13339,-13397,-13307,-13195,-13220,-13341,-13396,-13306,-13194,-13225,-13342,-13390,-13300,-13191,-13218,-13346,-13391,-13287,-13163,-13167,-13283,-13345,-13275,-13203,-13253,-13384,-13426,-13324,-13226,-13237,-13346,-13391,-13296,-13196,-13218,-13340,-13389,-13294,-13197,-13223,-13343,-13391,-13294,-13193,-13218,-13345,-13404,-13304,-13190,-13214,-13346,-13395,-13292,-13188,-13215,-13336,-13393,-13295,-13188,-13211,-13338,-13393,-13295,-13196,-13223,-13344,-13389,-13292,-13185,-13217,-13331,-13384,-13287,-13187,-13210,-13328,-13381,-13285,-13174,-13210,-13332,-13385,-13300,-13190,-13217,-13341,-13391,-13299,-13192,-13220,-13356,-13401,-13306,-13204,-13233,-13360,-13404,-13305,-13197,-13234,-13362,-13416,-13317,-13215,-13247,-13353,-13396,-13313,-13206,-13229,-13359,-13407,-13300,-13202,-13232,-13354,-13403,-13302,-13194,-13228,-13351,-13401,-13307,-13199,-13225,-13353,-13397,-13304,-13205,-13234,-13342,-13391,-13307,-13201,-13228,-13348,-13396,-13300,-13197,-13230,-13353,-13400,-13301,-13190,-13232,-13346,-13390,-13299,-13196,-13220,-13343,-13396,-13297,-13195,-13223,-13350,-13388,-13301,-13198,-13221,-13340,-13391,-13301,-13200,-13222,-13351,-13400,-13304,-13189,-13224,-13344,-13384,-13289,-13189,-13218,-13340,-13380,-13290,-13190,-13221,-13346,-13400,-13295,-13197,-13236,-13358,-13395,-13307,-13200,-13232,-13356,-13399,-13298,-13192,-13229,-13354,-13399,-13301,-13199,-13234,-13351,-13370,-13244,-13130,-13169,-13310,-13371,-13303,-13231,-13265,-13384,-13427,-13317,-13205,-13232,-13349,-13390,-13287,-13190,-13228,-13352,-13388,-13283,-13184,-13227,-13354,-13387,-13282,-13187,-13225,-13353,-13392,-13276,-13173,-13223,-13360,-13380,-13252,-13155,-13211,-13349,-13374,-13246,-13143,-13214,-13354,-13377,-13242,-13129,-13207,-13361,-13376,-13250,-13151,-13219,-13361,-13396,-13270,-13171,-13227,-13365,-13401,-13278,-13177,-13236,-13370,-13404,-13301,-13209,-13251,-13370,-13427,-13354,-13257,-13252,-13365,-13446,-13394,-13286,-13271,-13366,-13453,-13411,-13285,-13239,-13321,-13417,-13379,-13245,-13188,-13307,-13425,-13408,-13292,-13123,-13120,-13364,-13571,-13418,-13027,-12905,-13256,-13631,-13449,-12811,-12508,-13017,-13713,-13516,-12550,-12078,-12813,-13788,-13552,-12294,-11737,-12668,-13868,-13566,-11995,-11267,-12494,-14097,-13711,-11694,-10800,-12326,-14269,-13843,-11556,-10558,-12271,-14415,-13974,-11544,-10479,-12262,-14499,-14094,-11648,-10533,-12293,-14555,-14171,-11778,-10683,-12337,-14501,-14191,-11866,-10692,-12302,-14532,-14212,-11699,-10436,-12180,-14607,-14279,-11669,-10342,-12180,-14758,-14413,-11586,-10174,-12193,-14987,-14569,-11374,-9794,-12100,-15276,-14743,-11087,-9388,-12019,-15498,-14832,-10729,-8842,-11972,-15808,-14954,-10688,-8920,-12088,-15834,-15009,-10817,-8962,-12075,-15943,-15050,-10386,-8471,-12023,-16248,-15068,-9732,-7527,-11511,-16105,-14837,-9588,-7668,-11557,-15765,-14485,-9495,-7657,-11432,-15203,-13559,-9032,-7575,-8621,8346,27079,-21002,-2433,32767,32767,32767,32767,32767,32767,32767,32767,32767,32767,32767,32767,32767};
//        double resultEcg1[] = myFilter.ecgFilterC(ecg1);
//
//        String ecgString2 = "";
//        for (int i = 0; i < resultEcg1.length; i++) {
//            ecgString2 = ecgString2 +"," + resultEcg1[i];
//        }
//        Log.e(TAG,ecgString2);
    }


//    public static String getAppVersionName(Context context) {
//        String versionName = null;
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
//            versionName = pi.versionName;
//        } catch (Exception e) {
//        }
//        return versionName;
//    }
//

//    private void initView() {
//         mainText = (TextView) this.findViewById(R.id.main_title);
//        mainText.setText("Tracking App");
//
//        m_mainListView = (GridView) this.findViewById(R.id.main_list);
//        m_mainListAdapter = new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return m_mainArray.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return null;
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return 0;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
//                if (convertView == null) {
//                    convertView = inflater.inflate(R.layout.listview_main,null);
//                }
//
//                TextView title = (TextView) convertView.findViewById(R.id.list_text_name);
//                title.setText(m_mainArray.get(position));
//
//                return convertView;
//            }
//        };
//
//
//        m_mainListView.setAdapter(m_mainListAdapter);
//        m_mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int baseRow = 2;
//                if (position == 0) {
//                    Intent intent = new Intent(MainActivity.this, BoxingActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 1) {
//                    Intent intent = new Intent(MainActivity.this, HeartRateActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 0+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, ECGActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 1+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, PowerActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 2+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, ArmBandActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 3+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, HubConfigActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 4+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, HeartRateActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 5+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, BoxingActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 6+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, CadenceActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 7+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, SkippingActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 8+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, KTBBQActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 9+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, NewScaleActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 10+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, OldTrackerActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 11+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, NewTrackerActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 12+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, KettleBellActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 13+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, BikeComputerActivity.class);
//                    startActivity(intent);
//                }
//                else if (position == 14+baseRow) {
//                    Intent intent = new Intent(MainActivity.this, FunctionActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
//    }


//    public static void verifyStoragePermissions(Activity activity) {
//        int permission2 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.BLUETOOTH);
//        if (permission2 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH}, 1003);
//        }
//
//        int permission3 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.BLUETOOTH_ADMIN);
//        if (permission3 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, 1004);
//        }
//
//        int permission4 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION);
//        if (permission4 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1005);
//        }
//
//        int permission5 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.ACCESS_FINE_LOCATION);
//        if (permission5 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1006);
//        }
//
//        int permission6 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.READ_EXTERNAL_STORAGE);
//        if (permission6 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1006);
//        }
//
//        int permission7 = ActivityCompat.checkSelfPermission(activity,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permission7 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1006);
//        }
//
////        int permission8 = ActivityCompat.checkSelfPermission(activity,
////                Manifest.permission.BLUETOOTH_CONNECT);
////        if (permission8 != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1007);
////        }
////
////        int permission9 = ActivityCompat.checkSelfPermission(activity,
////                Manifest.permission.BLUETOOTH_SCAN);
////        if (permission9 != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, 1008);
////        }
//    }


}
