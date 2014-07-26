package com.qin.wsp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.qin.wsp.WifiConnect.WifiCipherType;
import com.qin.wsp.WifiConnect;

public class wifiActivity extends Activity { 
	//WifiManager wifiManager;
	  WifiManager wifiM;
	   WifiConnect wc;
	 WifiAsyncTask wa;
	 TextView wificonnecttext;
	 String wifiname;
	 Button wifibt;
	 boolean wififlag;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi);
		wificonnecttext =(TextView)findViewById(R.id.wificonnectinfo);
		wifibt = (Button)findViewById(R.id.wifibt);
		Intent i=getIntent();
		 wifiname=i.getStringExtra("ssid");
		 System.out.println("请求的WIFI名字为:"+wifiname);
		wificonnecttext.setText("已发送WIFI请求至:"+wifiname+"\n请等待授权");
		wifiM=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		wc=new WifiConnect(wifiM);
		 wa= new WifiAsyncTask(wifiname,wc);  
		 wa.execute(wifiname);
		wifibt.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(wififlag==true)
				{
					Intent i =new Intent(wifiActivity.this,longhuActivity.class);
					startActivity(i);
					
				}
				else
				{
					finish();
				}
			}
		});
		/*Intent intent = getIntent();
		String ssid = intent.getStringExtra("ssid");   
		String passwd = intent.getStringExtra("passwd");   
		 wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			WifiConnect wc=new WifiConnect(wifiManager);
			boolean flage=wc.Connect("TP-LINK_318", "201331804", WifiCipherType.WIFICIPHER_WPA);
			System.out.println("连接状态： "+flage);*/
	}
	public class WifiAsyncTask extends AsyncTask<String, Void, String> {
		WifiConnect wc;
		String passwd;
		String ssid;
		String mStrResult;
		int Authorise;
	private	boolean flag=false;
		public WifiAsyncTask(String ssid,WifiConnect wc)
		  { this.wc=wc;
			this.ssid=ssid;
			//this.passwd=passwd;
		  }
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
					System.out.println("jsonObj:"+jsonObj);
					//设置请求IP
					String address = "http://192.168.1.111:8080/test_struts/getAuthorise.action";
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
						ssid = result.getString("SSID");
						passwd = result.getString("passwd");
						Authorise=result.getInt("authorise");
						//gender = result.getString("gender");
						System.out.println("ssid" + ssid);
						System.out.println("result::::::" + mStrResult);
						
					} else {
						System.out.println("连接失败0");
					}
				}
			}
				 catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
					// boolean flage=this.wc.Connect(this.ssid, this.passwd, WifiCipherType.WIFICIPHER_WPA);
					//System.out.println(SSIDArray[position]);
					//System.out.println(passwdArray[position]);
					//System.out.println("异步连接状态： "+flage);//SSIDArray[position];
			return "SSID:"+ssid+"passwd:"+passwd+"Authorise:"+Authorise;
				

		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(Authorise==1)
			{
				 this.flag=wc.Connect(ssid, passwd,WifiCipherType.WIFICIPHER_WPA);
				System.out.println("flag: "+flag);
				
			}
			if(flag==true)
			 {
				 Toast toast=Toast.makeText(getApplicationContext(), "wifi 连接成功", Toast.LENGTH_SHORT);
				 toast.show();
				 wificonnecttext.setText("已获得"+wifiname+"使用wifi的授权");
				 wifibt.setText("点此体验");
				 wififlag=true;
				 
			 }
			 else{
				 Toast toast=Toast.makeText(getApplicationContext(), "wifi 连接失败", Toast.LENGTH_SHORT);
                 toast.show();
                 wificonnecttext.setText(wifiname+"授权失败");
                 wifibt.setText("点此返回");
                 wififlag=false;
			 } 
		}

		
	}

}
