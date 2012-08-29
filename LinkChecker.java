package links;

/************************************************************************************8
 * Link Checker assignment for Mark Humphrys - CA651 
 * 
 * James O' Donoghue   
 * 25.26/04/2012
 * 
 * 	-Uses the JSoup library 
 * 	-Checks response code and message of links with my own class RequestCode.java
 * 	-Checks hyper-links, relative links, image links, imported scripts/css and form action links
 ************************************************************************************/


import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LinkChecker 
{
	//Declare static variable for the brokenRelativeLink method
	static boolean dontExist;
	
	public static void main(String[]args) throws IOException
	{
		//Declaring strings to check against
		//return code/msg of the links
		String okay = "OK";
		String okCode = "200";
		//String for new line char
		String nl = System.getProperty("line.separator");
		String nlx2 = nl+nl;
		
		String url;
		int count = 0;
		int countBroken = 0;
		int countBrokenRelative = 0;

		//Creating an instance of printstream to
		//output to file
		java.io.PrintStream ps = new PrintStream("output.html");
		ps.println("<html><head></head><body><pre>");

		//Creates an instance of my Request Code class
		RequestCode rc = new RequestCode();
		
		//Checks if a url was given on the command line
		//If it wasn't -asks you to enter one
		if(args.length == 1)
		{
			url = args[0];
			System.out.println("Fetching..."+url);
		}
		else
		{
			System.out.print("You have not entered a URL please do so now > ");
			Scanner sc = new Scanner(System.in);
			url = sc.nextLine();
			System.out.println("Fetching..."+url);
		}
		
		try
		{	//Uses JSoup to connect the URL given
			Document doc = Jsoup.connect(url).get();
			
			System.out.println(nl+"Connected");
			
			//Gets a list of links with the tag before the square brackets
			//and puts what is in the brackets equals in the list
			Elements links = doc.select("a[href]");
			Elements media = doc.select("[src]");
			Elements imports= doc.select("link[href]");
			Elements actions= doc.select("form[action]");
			
			ps.println("Results for: "+url+nl);
			
			//Prints to the command line and file the 
			//amount of different links there are
			System.out.println(nl+"Links: "+links.size());
			ps.println("Hyper and Relative Links: "+links.size());
			System.out.println("Media: "+media.size());
			ps.println("Media: "+media.size());
			System.out.println("Imports: "+imports.size());
			ps.println("Imports: "+imports.size());
			System.out.println("Form Links: "+actions.size());
			ps.println("Form Links: "+actions.size());
			
			System.out.print(nlx2+"Checking links");
			
			//Iterates through the links elements list
			for(Element link : links)
			{
				
				//Defines the link to check
				//Prefixes the URL of the page to what href equals ("abs:")
				String url2 = link.attr("abs:href");				
				
				//Checks if the link is relative by seeing if it contains the # symbol
				if(url2.contains("#"))
				{	
					//Calls on the brokenRelativeLink method(at end of page)
					//Returns true if broken
					if(brokenRelativeLink(url2))
					{
						ps.println(nl+"<a href=\""+url2+"\">"+link.text()+"</a>"+nl+"Does not exist or has moved"+nl);
						countBrokenRelative++;
					}
				}
				//If not relative link
				else
				{	
					//Fetches the return code and message of URL
					String retCode = rc.getCode(url2);
					String retMsg = rc.getMsg(url2);			
					
					//If the link does not contain google, return code != 200 and return message != OK 
					if((!url2.contains("google")) && (!retCode.equals(okCode)) && (!retMsg.equalsIgnoreCase(okay)))
					{
						//Prints out the link and the return code and message 
						ps.println("<a href=\""+url2+"\">"+link.text()+"</a>"+nl+"Code is: "+retCode+nl+"Msg is: "+retMsg+nl);
						//Increments count broken if link is broken
						countBroken++;				
					}
					count++;
				
					if (count%2 == 0)
					{
						System.out.print(".");
					}
				}
				//Flushes all the print stream print lines to file
				 ps.flush();
			}
			
			//Media, imports and actions follow the same logic as the links
			//i.e. if the response code and message do not equal 200 and OK
			//respectively it will determine the link as broken
			for(Element src : media)
			{
				String url3 = src.attr("abs:src");
				String retCode2 = rc.getCode(url3);
				String retMsg2 = rc.getMsg(url3);
				
				
				if((!retCode2.equals(okCode)) && (!retMsg2.equalsIgnoreCase(okay)))
				{
					if(src.tagName().equals("img"))
					{
						ps.println(src.tagName()+": "+"<a href=\""+url3+"\">"+src.text()+"</a>"+nl
														+"Code is: "+retCode2+nl+"Msg is: "+retMsg2+nlx2);
					}
					else
					{
						ps.println(src.tagName()+": "+"<a href=\""+url3+"\">"+src.text()+"</a>"+nl
														+"Code is: "+retCode2+nl+"Msg is: "+retMsg2+nlx2);
					}
					countBroken++;
				}
				 ps.flush();
			}
			
			for(Element linkIn : imports)
			{
				String url4 = linkIn.attr("abs:href");
				
				String retCode3 = rc.getCode(url4);
				String retMsg3 = rc.getMsg(url4);
				
				if(!retCode3.equals(okCode) && (!retCode3.equals(okay)))
				{
					ps.println(nl+linkIn.attr("rel")+" "+"<a href=\""+url4+"\">"+linkIn.text()+"</a>"+nl
															+"Code is: "+retCode3+nl+"Msg is: "+retMsg3+nlx2);
					countBroken++;
				}
				 ps.flush();
			}
			

			for(Element actionLink : actions)
			{
				String url5 = actionLink.attr("abs:action");
				
				String retCode4 = rc.getCode(url5);
				String retMsg4 = rc.getMsg(url5);
				
				if(!retCode4.equals(okCode) && (!retCode4.equals(okay)))
				{
					ps.println(nl+"<a href=\""+url5+"\">"+actionLink.text()+"</a>"+nl
																	+"Code is: "+retCode4+nl+"Msg is: "+retMsg4+nlx2);
					countBroken++;
				}
				 ps.flush();
			}
		}
		catch (Exception e)
		{
			//Print out error message if URL does not exist or is missing
			System.out.println("ERROR:"+nl+url+" does not exist or is missing \"http://\"");
		}
		
		//Outputs overall results to command line
		System.out.println(nlx2+"Check Complete"+nl+"There are "+countBroken
										   +" broken links and "+countBrokenRelative+" broken relative links");
		ps.println(nlx2+"Done"+nl+"There are: "+nl+countBroken
											+" broken links"+nl+countBrokenRelative+" broken relative links");
		ps.println("See output.html for details");
		ps.println("</pre></body></html>");
		ps.close();
	}
	
	//Static method to check if the relative link is broken
	//Returns boolean -true if it does not exist
	public static boolean brokenRelativeLink(String href)
	{
		
		try
		{
			Elements names;
		
			Elements id;
				boolean doesntExist;
		
			String relUrl = href;
			String regex = "#";
	
			
			//Splits the string in two from the #
			String [] split = relUrl.split(regex);
			
			//Split[0] = url of the page
			//Split[1] = name/id of the element
			String pageUrl = split[0];
			String nameId = split[1];
			//Connects to URL
			Document doc = Jsoup.connect(pageUrl).get();
			
			//If there is an element on the page with the name/id equal to that
			//in the relative link it will fetch it, else it will return null
			names = doc.getElementsByAttributeValue("name",nameId);
			id = doc.getElementsByAttributeValue("id", nameId);
		
			//returns the boolean
			if(names.isEmpty() && id.isEmpty())
			{	
				//Sets exists to true if there is no 
				//element with the same name as the relative link
				doesntExist = true;
			}
			else
			{   //If there is an element on the page 
				doesntExist =false;
			}
			dontExist = doesntExist;
		}
		catch(Exception e)
		{
			
		}
		return dontExist;
	}
}
