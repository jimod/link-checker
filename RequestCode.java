package links;

/*******************************************************************************************
 * Class to get the request code and status message from a webpage
 * 
 * James O' Donoghue   
 * 25.26/04/2012
 * 
 * -Class to fetch the request code and message from a HTTP header
 * 
 ********************************************************************************************/

import java.net.*;
import java.io.*;


public class RequestCode 
{
	private String hostName = "";
	
	public String getCode(String hName) throws IOException
	{
		String responseCode = "";
		 
		
		try
		{	
			hostName = hName;
			
			//Creates an instance of URL class
			//on the URL being searched
			URL url = new URL(hostName);
			
			//Using the Java.net library openConnection method to 
			URLConnection con = url.openConnection();
			
			//Iterates through the header field and fieldkeys
			//And assigns them to name and value
			for (int i=0; ;i++)
			{
				String name = con.getHeaderFieldKey(i);
				String value = con.getHeaderField(i);
				
				//If name is null ie if it is first line of header
				if(name == null)
				{   
					//Splits value at indexes 8 and 12 
					//to get substring containing response code
					responseCode = value.substring(8, 12);
					//Trims the whitespace from around the substring
					responseCode = responseCode.trim();
					break;
				}
			}
		}
		catch(Exception e)
		{	//If the connection times out
			responseCode = "N/A";
		}
		return responseCode;
		
	}
	
	//Method to get the response message
	public String getMsg(String hName) throws IOException
	{
		String responseMsg = "";
		
		try
		{	
			hostName = hName;

			
			URL url = new URL(hostName);
			
			URLConnection con = url.openConnection();
			
			for (int i=0; ;i++)
			{
				String name = con.getHeaderFieldKey(i);
				String value = con.getHeaderField(i);
				
				if(name == null)
				{					
					responseMsg = value.substring(12);
					responseMsg = responseMsg.trim();
					break;
				}
			}
		}
		catch(Exception e)
		{	//If the connection times out
			responseMsg = "Connection Timed out";
		}
		return responseMsg;
		
	}

}
