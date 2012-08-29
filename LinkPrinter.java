package links;
/****************************************************************************************8
 * Class that takes a url as input and prints out all the links on the page using JSoup
 * 
 * James O' Donoghue   
 * 25.26/04/2012
 * 
 * Customized from:
 * http://jsoup.org/cookbook/extracting-data/example-list-links 
 ******************************************************************************************/
import java.io.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class LinkPrinter 
{
	public static void main(String[]args) throws IOException
	{
		String url;
		
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
		{
			Document doc = Jsoup.connect(url).get();
			
			//This extracts all the relative and hyperlinks
			Elements links = doc.select("a[href]");
			//This extracts everything (eg pics and scripts that use the 'src' tag
			Elements media = doc.select("[src]");
			//This extracts the stylesheet
			Elements imports= doc.select("link[href]");

			
			
			
			System.out.println("\n\n\nLinks: "+links.size());
			for(Element link : links)
			{
				System.out.println(link.attr("abs:href")+" <"+link.text()+">");
			}

			System.out.println("\n\n\nMedia: "+media.size());	
			for(Element src : media)
			{
				if(src.tagName().equals("img"))
				{
					System.out.println(src.tagName()+" "+src.attr("abs:src")+" "+src.attr("width")+" "+src.attr("height")+" "+src.attr("alt"));
					
				}
				else
				{
					System.out.println(src.tagName()+" "+src.attr("abs:src"));
				}
			}
			
			System.out.println("\n\n\nImports: "+imports.size());
			for(Element link : imports)
			{
				System.out.println(link.tagName()+" "+link.attr("abs:href")+" "+link.attr("rel"));
			}

			
		}
		catch (Exception e)
		{
			System.out.println("ERROR:\n"+ url+" does not exist or is missing \"http://\"");
		}
		
		
	}
}
