package org.ble.find;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;


public class Get_PostUtil {

	public static String sendGet(String address,String param){
		HttpURLConnection connection=null;
		String result="";
		try{
			URL url=new URL(address+"?"+param);   //TODO��ô���ʴ���ȶ
			connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);		
			
			InputStream in=connection.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			StringBuilder response=new StringBuilder();
			String line;
			while((line=reader.readLine())!=null){
				response.append(line);
			}
			return result=response.toString();
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			if(connection!=null){
				connection.disconnect();
			}
		}
		return result;
	}
	
	public static boolean sendPost(String address,String param){
		Boolean flag=false;
		try{
			URL url=new URL(address+"?"+param); 
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true); // ��������д������
			connection.setDoOutput(true); // �������ж�ȡ����
			connection.setUseCaches(false); // ��ֹ����
			connection.setInstanceFollowRedirects(true);	//�Զ�ִ��HTTP�ض���
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded"); // ������������
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream()); // ��ȡ�����
			/*String param = "phone="
					+ URLEncoder.encode(x, "utf-8")
					+ "&password="
					+ URLEncoder.encode(y, "utf-8");	//����Ҫ�ύ������
*/			out.writeBytes(param);//��Ҫ���ݵ�����д�����������
			out.flush();	//�������
			out.close();	//�ر����������
			// �ж��Ƿ���Ӧ�ɹ�
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				flag=true;
			}
			/*	InputStreamReader in = new InputStreamReader(
						connection.getInputStream()); // ��ö�ȡ������
				BufferedReader buffer = new BufferedReader(in); // ��ȡ����������
				String inputLine = null;
				while ((inputLine = buffer.readLine()) != null) {
					str= inputLine;
				}
				in.close();	//�ر��ַ�������
		}         */	                                                   //����Ҫ��������~��~
			connection.disconnect();	//�Ͽ�����
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(flag==true)
			return true;
		else
			return false;
	}
/*���ز�����*/	
	public static String sendGet(String address){
		HttpURLConnection connection=null;
		String result="";
		try{
			URL url=new URL(address);   //TODO��ô���ʴ���ȶ
			connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(8000);		
			
			InputStream in=connection.getInputStream();
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
			StringBuilder response=new StringBuilder();
			String line;
			while((line=reader.readLine())!=null){
				response.append(line);
			}
			return result=response.toString();
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			if(connection!=null){
				connection.disconnect();
			}
		}
		return result;
	}
	
	/* public Map<Object,Object> jsonToMapObject(String json){
    Map<Object,Object> map = new HashMap<Object,Object>();//newһ��map����
    String str = json.replace("{","").replace("}","").replaceAll("\"", "");//ȥ�������š�˫����
    String[] ary1 = str.split(",");//���ݡ�,���ŷָ��ַ���������������
    for(String s : ary1){//ѭ��ȡ�������еĶ���
        String[] ary2 = s.split(":");//���ݡ�:��ð�ŷָ��ַ�������ü�-ֵ�������
        map.put(ary2[0],ary2[1]);//�������Լ�-ֵ�Եķ�ʽ����map��
    }
    return map;
}*/
public static String parseJSON(String jsonData,String str){
String parsString="";
try {
	JSONObject jsonObj=new JSONObject(jsonData);
	/*String phone=jsonObj.getString("phone");
	String password=jsonObj.getString("password");*/
	parsString=jsonObj.getString(str);
	//Toast.makeText(UserActivity.this, "�˺�"+str, Toast.LENGTH_LONG).show();	
} catch (JSONException e1) {	
	e1.printStackTrace();
}
return parsString;
}
@SuppressWarnings("null")
public static String[] parseJSONArray(String jsonData,String armData){
	String[] arm_data = null;
	try {
		JSONArray jsonArray=new JSONArray(jsonData);
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			arm_data[i] = jsonObject.getString(armData);
		}
	} catch (JSONException e1) {	
		e1.printStackTrace();
	}
	return arm_data;
}

}
