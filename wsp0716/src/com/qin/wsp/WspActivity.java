package com.qin.wsp;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import com.esri.android.map.MapView;
//import com.jsondemo.activity.MainActivity.MyTask;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.qin.wsp.WifiConnect.WifiCipherType;
import com.qin.wsp.WifiAsyncTask;
import com.qin.wsp.WifiConnect.WifiCipherType;
public class WspActivity extends Activity {
	
	int cishu=0;

	WifiManager wifiManager;
	ListView listView;
	List<ScanResult> scanResults;

	ListAdapter adapter;
	static Timer timer;

	Handler handler = new MyHandler(this);

	LocationManager locationManager;
	TextView tvLocation;
	EditText scanPosition;
	RadioButton radioButton;
	final int MAX_SCAN = 5;
	static int curScan = 0;
	int count = 0;
    String wifiname;
	SQLiteDatabase scanDatabase;
    String	mStrResult;

    String SSID = "";
   String passwd = "";
   private MyTask mTask;
   String SSIDArray[];
   String passwdArray[];
   WifiManager wifiM;
   WifiConnect wc;
   WifiAsyncTask wa;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		listView = (ListView) findViewById(R.id.list_wifi_info);
		tvLocation = (TextView) findViewById(R.id.tv_location);
	//	scanPosition = (EditText) findViewById(R.id.scan_position);
	//	radioButton = (RadioButton) findViewById(R.id.scan_record);
		wifiM = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		// WifiConnect wc=new WifiConnect(wifiM);
		// boolean flage=wc.Connect("TP-LINK_318", "201331804", WifiCipherType.WIFICIPHER_WPA);
		//	System.out.println("连接状态： "+flage);

		wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			Toast.makeText(this, "请打开Wifi连接", Toast.LENGTH_SHORT).show();
			return;
		}
		wifiManager.startScan();
		 wifiManager.getScanResults();

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			updateLocation(location);
		} else {
			Toast.makeText(this, "Last known location is NULL",
					Toast.LENGTH_SHORT).show();
		}

		String scanDbPath = getResources().getString(R.string.scan_db);
		scanDatabase = SQLiteDatabase.openOrCreateDatabase(scanDbPath, null);
		String createTb = "create table if not exists record(_id integer primary key autoincrement not null, "
				+ "position text, ssid text, level text, bssid text, all_info text)";
		scanDatabase.execSQL(createTb);

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				3000, 0.1f, new LocationListener() {
					public void onStatusChanged(String provider, int status,
							Bundle extras) {
					}

					public void onProviderEnabled(String provider) {

					}

					public void onProviderDisabled(String provider) {
					}

					public void onLocationChanged(Location location) {
						if (location != null) {
							Toast.makeText(WspActivity.this, "LocationUpdate",
									Toast.LENGTH_SHORT).show();
							updateLocation(location);
						} else {
							Toast.makeText(WspActivity.this,
									" ",
									Toast.LENGTH_SHORT).show();
						}

					}
				});
		
		startMonotor();
	
		/*for(int i=0;i<scanResults.size();i++)
			{
			 wifiname+=scanResults.get(i).SSID+",";
			}
		System.out.println("scanResults.size:"+scanResults.size());
		System.out.println("wifiname :"+wifiname);
		mTask = new MyTask();
		mTask.execute(wifiname);*/
		
	}

	private class MyTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient hc = new DefaultHttpClient();
			// 这里是服务器的IP，不要写成localhost了，即使在本机测试也要写上本机的IP地址，localhost会被当成模拟器自身的
			//String address = "http://192.168.1.125:8080/ServerJsonDemo/JsonServlet";

			//String gender = "";
			try {
				String[] strs = params[0].split(",");//请求单个或多个对象
				if(strs.length == 1){			//请求单个对象
					// 封装JSON对象
					System.out.println("strs.lenth==1");
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("SSID", params[0]);
					//设置请求IP
					String address = "http://192.168.1.111:8080/test_struts/getwifi.action";
					// 创建请求并绑定请求到Entity
					HttpPost hp = new HttpPost(address);
					hp.setEntity(new StringEntity(jsonObj.toString()));
					HttpResponse response = hc.execute(hp);
					// 返回200即请求成功
					System.out.println("StatusCode: " + response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == 200) {
						// 获取响应中的数据，这也是一个JSON格式的数据
						mStrResult = EntityUtils.toString(response.getEntity());//string格式
						// 将返回结果生成JSON对象
						JSONObject result = new JSONObject(mStrResult);
						// 从中提取需要的值
						SSID = result.getString("SSID");
						passwd = result.getString("passwd");
						//gender = result.getString("gender");
						System.out.println("result" + mStrResult);
					} else {
						System.out.println("连接失败0");
					}
				}else if(strs.length > 1){		
					//请求多个对象，封装成json数组
					JSONArray jsons = new JSONArray();
					for(int i = 0; i < strs.length; i++){
						JSONObject obj = new JSONObject();
						obj.put("SSID", strs[i]);//strs[]中存储的为id
						jsons.put(obj);
					}
					String tmp=jsons.toString();
					System.out.println("jsons  :"+tmp);
					String address = "http://192.168.1.111:8080/test_struts/getwifiList.action";
					HttpPost hp = new HttpPost(address);
					hp.setEntity(new StringEntity(jsons.toString()));//数组toString

					HttpResponse response = hc.execute(hp);
					System.out.println("StatusCode2: " + response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == 200) {
						mStrResult = EntityUtils.toString(response.getEntity());
						JSONArray jsonArray = new JSONArray(mStrResult);
						for(int i = 0; i < jsonArray.length(); i++){
							JSONObject obj = jsonArray.getJSONObject(i);
							SSID += obj.getString("SSID") + ", ";
							//passwd += obj.getString("passwd") + ", ";
						//	gender += obj.getString("gender") + ", ";
						}
					} else {
					//	Toast.makeText(getApplicationContext(), "连接服务器失败", Toast.LENGTH_SHORT).show();
						System.out.println("连接失败1");
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return "SSID: " + SSID;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//mTvResult.setText(result);
			SSIDArray=SSID.split(",");
			//passwdArray=passwd.split(",");
			wifiInfo();
		}
	}
	private void updateLocation(Location location) {
		tvLocation.setText("X:" + String.format("%3.7f", location.getLongitude()) + " Y:"
				+ String.format("%3.7f", location.getLatitude())
				+ " A:" + location.getAccuracy() + "米");
	}

	static class MyHandler extends Handler {
		private WeakReference<WspActivity> _myWifiInfo;

		public MyHandler(WspActivity wifiInfo) {
			_myWifiInfo = new WeakReference<WspActivity>(wifiInfo);
		}

		@Override
		public void handleMessage(Message msg) {
			WspActivity activity = _myWifiInfo.get();
			if (activity != null) {
				activity.wifi();
			}
			super.handleMessage(msg);
		}
	}
private void wifi()
    {
	/*if(radioButton.isChecked()){
	curScan++;
   }*/
  wifiManager.startScan();
   scanResults = wifiManager.getScanResults();

   String srinfo = scanResults.toString();//扫描的结果，为一个数组
   System.out.println(srinfo);
    Collections.sort(scanResults, new Comparator<ScanResult>() {
	public int compare(ScanResult lhs, ScanResult rhs) {
		return lhs.SSID.compareTo(rhs.SSID);
	      }
       });
    wifiname=scanResults.get(0).SSID+",";
    for(int i=1;i<scanResults.size();i++)
	{
	 wifiname+=scanResults.get(i).SSID;
	 if(i<scanResults.size()-1)
	 {
		 wifiname+=",";
	 }
	}
    System.out.println("scanResults.size:"+scanResults.size());
    System.out.println("wifiname :"+wifiname);
   mTask = new MyTask();
     mTask.execute(wifiname);
	}
	private void wifiInfo() {
		/*if(radioButton.isChecked()){
			curScan++;
		}
		wifiManager.startScan();
		scanResults = wifiManager.getScanResults();
		
		String srinfo = scanResults.toString();//扫描的结果，为一个数组
		System.out.println(srinfo);
		Collections.sort(scanResults, new Comparator<ScanResult>() {
			public int compare(ScanResult lhs, ScanResult rhs) {
				return lhs.SSID.compareTo(rhs.SSID);
			}
		});*/
		//循环向BaseAdapter中加入数据
		adapter = new BaseAdapter() {
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(R.layout.wifi_info, null);
				TextView tvSSID = (TextView) v.findViewById(R.id.tv_ssid_info);//获取控件
				ProgressBar progressLevel = (ProgressBar) v
						.findViewById(R.id.level);//获取控件
				//ScanResult sr = scanResults.get(position);
				String S=SSIDArray[position];
			//	String passwd=passwdArray[position];  
				//tvSSID.setText("SSID: " + sr.SSID + "  LEVEL: " + sr.level);//设置控件显示内容
				tvSSID.setText("SSID: "+S);
				/*if (sr.SSID.equals("B1-202east")) {
					Log.i("MyWifiInfo", sr.SSID + ": " + sr.level);
				}
			//	progressLevel.setProgress(sr.level + 100);//设置控件显示内容
				if (radioButton.isChecked() && curScan < MAX_SCAN
						&& scanPosition.getText().toString() != null
						&& !scanPosition.getText().toString().trim().equals("")) {
					String sql = "insert into record values(null,'"
							+ scanPosition.getText().toString() + "','"
							+ sr.SSID + "','" + sr.level + "','" + sr.BSSID
							+ "','" + sr.toString() + "')";
					scanDatabase.execSQL(sql);
					count++;
					// insert into record values(null, "123", "b1-202east",
					// "-45.6", "1.2.2.2", "sdff");

				}*/
                
				return v;
			}

			public long getItemId(int position) {
				return position;
			}

			public Object getItem(int position) {
				return null;
			}

			public int getCount() {
				return SSIDArray.length-1;
			}

		
		};//BaseAdapter
		listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//Intent intent = new Intent();
				//intent.setClass(WspActivity.this, wifiActivity.class);
				//intent.putExtra("ssid", SSIDArray[position]);
				//intent.putExtra("passwd",passwdArray[position]);
				//startActivity(intent);
				//WifiManager wifiM = (WifiManager) getSystemService(Context.WIFI_SERVICE);
				Intent intent= new Intent(WspActivity.this,wifiActivity.class);
				intent.putExtra("ssid", SSIDArray[position].toString().trim());
				startActivity(intent);
				/*wc=new WifiConnect(wifiM);
				 wa= new WifiAsyncTask(SSIDArray[position].toString().trim(),wc);  
				 wa.execute(SSIDArray[position].toString().trim());*/
                	
                /* System.out.println("wa.flag:::::::"+wa.getflag());
				if(wa.getflag()==true)
				 {
					 Toast toast=Toast.makeText(getApplicationContext(), "wifi 连接成功", Toast.LENGTH_SHORT);
					 toast.show();
				 }
				 else{
					 Toast toast=Toast.makeText(getApplicationContext(), "wifi 连接失败", Toast.LENGTH_SHORT);
                      toast.show();
				 } *///if(wa.Authorise==1)
			//{
				//boolean flage=wc.Connect(SSIDArray[position].toString().trim(),passwdArray[position].toString().trim(), WifiCipherType.WIFICIPHER_WPA);
				//System.out.println("lianjiezhuangtai:"+flage);  
			//}else
			//{
				//System.out.println("您无此权限");
			//}
				//boolean flage=wc.Connect(SSIDArray[position].toString().trim(),passwdArray[position].toString().trim(), WifiCipherType.WIFICIPHER_WPA);
				//System.out.println(SSIDArray[position].toString().trim());
				//System.out.println(passwdArray[position].toString().trim());
				//System.out.println("连接状态： "+flage);//SSIDArray[position];
				 
				 }
        	
		}); 
	/*	if(curScan == 5){
			scanPosition.setText("");
			radioButton.setChecked(false);
			Toast.makeText(WspActivity.this, "扫描信息记录停止, 记录" + count + "条", Toast.LENGTH_SHORT).show();
			count = 0;
			curScan = 0;
		}*/
	}//wifiInfo

	private void startMonotor() {
		if (timer == null)
			timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = 0x111;
				handler.sendMessage(msg);
			}
		}, 0, 3000000);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wifi_info_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_open_wifi:
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			break;
		case R.id.menu_scan_wifi:
			startMonotor();
			break;
		case R.id.menu_close_monitor:
			Log.i("MyWifiInfo", "Stop timer");
			timer.cancel();
			timer = null;
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}*/

	@Override
	protected void onDestroy() {
		if (scanDatabase.isOpen()) {
			scanDatabase.close();
		}
		super.onDestroy();
	}
}