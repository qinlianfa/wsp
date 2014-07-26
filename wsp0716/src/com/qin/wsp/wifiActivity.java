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
		 System.out.println("�����WIFI����Ϊ:"+wifiname);
		wificonnecttext.setText("�ѷ���WIFI������:"+wifiname+"\n��ȴ���Ȩ");
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
			System.out.println("����״̬�� "+flage);*/
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
			// �����Ƿ�������IP����Ҫд��localhost�ˣ���ʹ�ڱ�������ҲҪд�ϱ�����IP��ַ��localhost�ᱻ����ģ���������
			//String address = "http://192.168.1.125:8080/ServerJsonDemo/JsonServlet";

			//String gender = "";
			try {
				String[] strs = params[0].split(",");//���󵥸���������
				if(strs.length == 1){			//���󵥸�����
					// ��װJSON����
					System.out.println("strs.lenth==1");
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("SSID", params[0]);
					System.out.println("jsonObj:"+jsonObj);
					//��������IP
					String address = "http://192.168.1.111:8080/test_struts/getAuthorise.action";
					// �������󲢰�����Entity
					HttpPost hp = new HttpPost(address);
					hp.setEntity(new StringEntity(jsonObj.toString()));
					HttpResponse response = hc.execute(hp);
					// ����200������ɹ�
					System.out.println("StatusCode: " + response.getStatusLine().getStatusCode());
					if (response.getStatusLine().getStatusCode() == 200) {
						// ��ȡ��Ӧ�е����ݣ���Ҳ��һ��JSON��ʽ������
						mStrResult = EntityUtils.toString(response.getEntity());//string��ʽ
						// �����ؽ������JSON����
						JSONObject result = new JSONObject(mStrResult);
						// ������ȡ��Ҫ��ֵ
						ssid = result.getString("SSID");
						passwd = result.getString("passwd");
						Authorise=result.getInt("authorise");
						//gender = result.getString("gender");
						System.out.println("ssid" + ssid);
						System.out.println("result::::::" + mStrResult);
						
					} else {
						System.out.println("����ʧ��0");
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
					//System.out.println("�첽����״̬�� "+flage);//SSIDArray[position];
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
				 Toast toast=Toast.makeText(getApplicationContext(), "wifi ���ӳɹ�", Toast.LENGTH_SHORT);
				 toast.show();
				 wificonnecttext.setText("�ѻ��"+wifiname+"ʹ��wifi����Ȩ");
				 wifibt.setText("�������");
				 wififlag=true;
				 
			 }
			 else{
				 Toast toast=Toast.makeText(getApplicationContext(), "wifi ����ʧ��", Toast.LENGTH_SHORT);
                 toast.show();
                 wificonnecttext.setText(wifiname+"��Ȩʧ��");
                 wifibt.setText("��˷���");
                 wififlag=false;
			 } 
		}

		
	}

}
