package com.qin.wsp;

import java.util.List;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration;


public class WifiConnect {
	 WifiManager wifiManager;
	    
		//���弸�ּ��ܷ�ʽ��һ����WEP��һ����WPA������û����������
		    public enum WifiCipherType
		    {
		  	  WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
		    }
			
		//���캯��
			public WifiConnect(WifiManager wifiManager)
			{
			  this.wifiManager = wifiManager;
			}
			
		//��wifi����
		     private boolean OpenWifi()
		     {
		    	 boolean bRet = true;
		         if (!wifiManager.isWifiEnabled())
		         {
		       	  bRet = wifiManager.setWifiEnabled(true);  
		         }
		         return bRet;
		     }
		    
		//�ṩһ���ⲿ�ӿڣ�����Ҫ���ӵ�������
		     public boolean Connect(String SSID, String Password, WifiCipherType Type)
		     {
		        if(!this.OpenWifi())
		    	{
		    		 return false;
		    	}
		//����wifi������Ҫһ��ʱ��(�����ֻ��ϲ���һ����Ҫ1-3������)������Ҫ�ȵ�wifi
		//״̬���WIFI_STATE_ENABLED��ʱ�����ִ����������
		        while(wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING )
		        {
		        	 try{
		     //Ϊ�˱������һֱwhileѭ��������˯��100�����ڼ�⡭��
		           	  Thread.currentThread();
					  Thread.sleep(10);
		           	}
		           	catch(InterruptedException ie){
		           }
		        }
		       
		    WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password, Type);
				//
		    	if(wifiConfig == null)
				{
		    	       return false;
				}
			   	
		        WifiConfiguration tempConfig = this.IsExsits(SSID);
		        
		        if(tempConfig != null)
		        {
		        	wifiManager.removeNetwork(tempConfig.networkId);
		        }
		        
		      int netID = wifiManager.addNetwork(wifiConfig);
		    	boolean bRet =false;
		    
		    	bRet= wifiManager.enableNetwork(netID, true);  
		    	//wifiManager.disconnect();
		    	wifiManager.reconnect();
				return bRet;
		     }
		     
		    //�鿴��ǰ�Ƿ�Ҳ���ù��������
		     private WifiConfiguration IsExsits(String SSID)
		     {
		    	 List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
		    	    for (WifiConfiguration existingConfig : existingConfigs) 
		    	    {
		    	      if (existingConfig.SSID.equals("\""+SSID+"\""))
		    	      {
		    	          return existingConfig;
		    	      }
		    	    }
		    	 return null; 
		     }
		     
		     private WifiConfiguration CreateWifiInfo(String SSID, String Password, WifiCipherType Type)
		     {
		     	WifiConfiguration config = new WifiConfiguration();  
		         config.allowedAuthAlgorithms.clear();
		         config.allowedGroupCiphers.clear();
		         config.allowedKeyManagement.clear();
		         config.allowedPairwiseCiphers.clear();
		         config.allowedProtocols.clear();
		     	config.SSID = "\"" + SSID + "\"";  
		     	if(Type == WifiCipherType.WIFICIPHER_NOPASS)
		     	{
		     		 config.wepKeys[0] = "";
		     		 config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		     		 config.wepTxKeyIndex = 0;
		     	}
		     	if(Type == WifiCipherType.WIFICIPHER_WEP)
		     	{
		     		config.preSharedKey = "\""+Password+"\""; 
		     		config.hiddenSSID = true;  
		     	    config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		     	    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		     	    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		     	    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		     	    config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
		     	    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		     	    config.wepTxKeyIndex = 0;
		     	}
		     	if(Type == WifiCipherType.WIFICIPHER_WPA)
		     	{
		     	config.preSharedKey = "\""+Password+"\"";
		     	config.hiddenSSID = true;  
		     	config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
		     	config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);                        
		     	config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);                        
		     	config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);                   
		     	//config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);  
		     	config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP); 
		     	config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		     	config.status = WifiConfiguration.Status.ENABLED;  
		     	}
		     	else
		     	{
		     		return null;
		     	}
		     	return config;
		     }
		     
		}
