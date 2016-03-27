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
			URL url=new URL(address+"?"+param);   //TODO怎么访问待商榷
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
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true);	//自动执行HTTP重定向
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded"); // 设置内容类型
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream()); // 获取输出流
			/*String param = "phone="
					+ URLEncoder.encode(x, "utf-8")
					+ "&password="
					+ URLEncoder.encode(y, "utf-8");	//连接要提交的数据
*/			out.writeBytes(param);//将要传递的数据写入数据输出流
			out.flush();	//输出缓存
			out.close();	//关闭数据输出流
			// 判断是否响应成功
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				flag=true;
			}
			/*	InputStreamReader in = new InputStreamReader(
						connection.getInputStream()); // 获得读取的内容
				BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
				String inputLine = null;
				while ((inputLine = buffer.readLine()) != null) {
					str= inputLine;
				}
				in.close();	//关闭字符输入流
		}         */	                                                   //不需要返回数据~。~
			connection.disconnect();	//断开连接
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
/*本地测试用*/	
	public static String sendGet(String address){
		HttpURLConnection connection=null;
		String result="";
		try{
			URL url=new URL(address);   //TODO怎么访问待商榷
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
	
	public static String sendPostret(String address,String param){
		String str="";
		try{
			URL url=new URL(address+"?"+param); 
			HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true); // 向连接中写入数据
			connection.setDoOutput(true); // 从连接中读取数据
			connection.setUseCaches(false); // 禁止缓存
			connection.setInstanceFollowRedirects(true);	//自动执行HTTP重定向
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded"); // 设置内容类型
			DataOutputStream out = new DataOutputStream(
					connection.getOutputStream()); // 获取输出流
			/*String param = "phone="
					+ URLEncoder.encode(x, "utf-8")
					+ "&password="
					+ URLEncoder.encode(y, "utf-8");	//连接要提交的数据
*/			out.writeBytes(param);//将要传递的数据写入数据输出流
			out.flush();	//输出缓存
			out.close();	//关闭数据输出流
			// 判断是否响应成功
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				
			}
				InputStreamReader in = new InputStreamReader(
						connection.getInputStream()); // 获得读取的内容
				BufferedReader buffer = new BufferedReader(in); // 获取输入流对象
				String inputLine = null;
				while ((inputLine = buffer.readLine()) != null) {
					 str = str+inputLine;
				}
				in.close();	//关闭字符输入流
				connection.disconnect();	//断开连接
		}         	                                                   //不需要返回数据~。~
		catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
public static String parseJSON(String jsonData,String str){
String parsString="";
try {
	JSONObject jsonObj=new JSONObject(jsonData);
	/*String phone=jsonObj.getString("phone");
	String password=jsonObj.getString("password");*/
	parsString=jsonObj.getString(str);
	//Toast.makeText(UserActivity.this, "账号"+str, Toast.LENGTH_LONG).show();	
} catch (JSONException e1) {	
	e1.printStackTrace();
}
return parsString;
}


//TODO 格式需要再修改
public static String parseJSONArray(String jsonData,String armData){
	String arm_data ="";
	try {
		JSONObject jsonObj=new JSONObject(jsonData);
		JSONArray jsonArray=jsonObj.getJSONArray("name");

		for (int i = 0; i < jsonArray.length(); i++) {
			//JSONObject jsonObject = jsonArray.getJSONObject(i);
			JSONObject jsonObject =(JSONObject)jsonArray.opt(i);
			arm_data =arm_data+","+ jsonObject.getString(armData);			
			// JSONObject jsonObjectSon= (JSONObject)jsonArray.opt(i); 
			//  str[i]=jsonObjectSon.getString("zhengshu")+"年份："+jsonObjectSon.getString("date");
		}
	} catch (JSONException e1) {	
		e1.printStackTrace();
	}
	return arm_data;
}

}
