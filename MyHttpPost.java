package com.example.getloc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.message.BasicNameValuePair;

import android.content.MutableContextWrapper;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

public class MyHttpPost {
	private URL murl;	
	private HttpURLConnection murlconn;
	private String para,resp;
	int max_attempts=5;
	boolean success=false;
   
	public String exec(String url,List<NameValuePair> headers,List<NameValuePair> parameters)
	{
		//create url object
		
		try {	
			murl=new URL(url);			
			} 
		catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				Log.d("MalformerdURLException","MalformerdURLException-message-"+e.toString());
				e.printStackTrace();
			}
		catch(Exception e)
			{
				e.printStackTrace();
			}
		
		for(int retry=0;retry<max_attempts;retry++)
		{
			
			try {

				//open connection
				murlconn = (HttpURLConnection) murl.openConnection();		
				
				//Set connection timeout
				murlconn.setConnectTimeout(10*1000);
				
				//Set true to write to connection
				murlconn.setDoOutput(true);	
				
				//Set Method
				murlconn.setRequestMethod("POST");
				
				//set Headers
				for(NameValuePair obj:headers)
				{
					murlconn.setRequestProperty(obj.getName().toString(),obj.getValue().toString());	
					//Log.d("Headers",obj.getName().toString()+"-"+obj.getValue().toString());
				}
				
				//Create parameter string to write
				para=createparastr(parameters);		
				
				//Set for better performance
				murlconn.setFixedLengthStreamingMode(para.getBytes().length);
							
				//write to stream
				writeRequest(murlconn,para);												
				
				switch(murlconn.getResponseCode())
				{
				case HttpStatus.SC_OK:
										Log.d("Response-code", "Http-ok");
										success=true;
										break;
										
				case HttpStatus.SC_BAD_REQUEST:
										Log.d("Response-code","Bad request. Correct request");
										//Retry after correcting request
										//Stop retry
										retry=max_attempts;
										break;
										
				default:
							Log.d("Response-code", "Unknown response-code"+murlconn.getResponseCode());
							break;
				}
			} 					
			catch(ClientProtocolException e)
				{
					Log.d("ClientProtocolException","ClientProtocolException-message-"+e.toString());
					e.printStackTrace();
					continue;
				}
			catch (IOException e) 
				{
					// TODO Auto-generated catch block
					//Input stream read error
					Log.d("IOException","IOException-message-"+e.toString());			
					e.printStackTrace();
					continue;
				}	
			catch (Exception e)
				{
				Log.d("Exception", "Exception-message"+e.getMessage());
				e.printStackTrace();
				continue;
				}	

			//break for loop on successful try									
				if(success)
					break;
			    Log.d("max-attempts","max-attempts-"+retry);
		}
		
		if(murlconn!=null)
			murlconn.disconnect();
		
		return resp;
		
	}
	
	//Create parameter string
	public String createparastr(List<NameValuePair> parameters)
	{
		boolean first=true;
		String para="";
		
		//Create parameter list
		for(NameValuePair obj:parameters)
		{
			
			{
				if(first==true)
					{
						first=false;
					}
				else
						para+="&";	
			
				para+=obj.getName();
				para+="=";
				para+=obj.getValue();
				
			}
		
		}
		
		return para;
	}
	
	
	// Writes request to  connection
		private static boolean writeRequest(HttpURLConnection connection, String body) {
			try {
										
					OutputStream wr = connection.getOutputStream();
					wr.write(body.getBytes());					
					wr.close();
						
					return true;
				}
			catch (IOException e)
				{	
					Log.d("IOException","IOException-message:"+e.getMessage());
					e.printStackTrace();
					return false;
				}
		
		}
			
			
		// Reads response from connection and return as  string.
		private static String readResponse(HttpURLConnection connection) 
		{
			try {
					StringBuilder str = new StringBuilder();
						
					BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line = "";
					while((line = br.readLine()) != null) {
						str.append(line + System.getProperty("line.separator"));
					}
					return str.toString();
				}
			catch (IOException e) { 
					Log.d("IOException","IOException-message:"+e.getMessage());
					e.printStackTrace();
					return new String(); 
				}
		}
		
}

