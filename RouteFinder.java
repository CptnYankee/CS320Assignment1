package assignmentOne;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
/************************************************************************************************
 * @author Joshua Standerfer																	*
 * This method creates a connection to https://www.communitytransit.org/busservice/schedules/	*
 * and stores the page's source code as a string. It then performs regex operations in order to *
 * retrieve useful information about bus routes and their stops.								*
 **********************************************************************************************	*/
public class RouteFinder{
    String schedules;
    String routeSchedule;
	String url = "https://www.communitytransit.org/busservice/schedules";
	
	RouteFinder(){}
	
	void initialConnection(){

		URLConnection bc = null;
		try {
			bc = new URL(url).openConnection();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bc.setRequestProperty(
				"user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11"
						+ " (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader in = null;
		try {
		in = new BufferedReader(new InputStreamReader(bc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String inputLine = "";
		try {
			while ((inputLine = in.readLine()) != null) {
				schedules += inputLine + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	boolean subConnection(String subRoute) {
		URLConnection bc = null;
		boolean valid = false;
		try {
			//System.out.println(url + "/route/" + subRoute );
			//all /'s in the route names are replaced with -
			subRoute = subRoute.replaceAll("/","-");
			bc = new URL(url + "/route/" + subRoute).openConnection();
			valid = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bc.setRequestProperty(
				"user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11"
						+ " (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		BufferedReader in = null;
		try {
		in = new BufferedReader(new InputStreamReader(bc.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String inputLine = "";
		try {
			while ((inputLine = in.readLine()) != null) {
				routeSchedule += inputLine + "\n";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//I have no idea why this prints the URL out twice...
		System.out.println(url + "/route/" + subRoute);
		return valid;
	}
	//Patterns:
	//https://www.communitytransit.org/busservice/schedules
	//<h3>Edmonds</h3>...<hr id
	//regex: <h3>(.*?)</h3>(.*?)<hr id
	//<strong><a href="/schedules/route/101">101</a></strong>
	//We want the change to the URL, and the number of the route
	//regex:<a href=\"/schedules(.*?)\"(>|\\sclass=&quot;text-success&quot;>)(.*?)</a>"
	//The last entry in each destination is different.
	//previously had a group (subMatcher group 1) for each URL, but the format allows us to append the route# to the end
	
	//This is the method which finds the list of potential destinations based on user input.
	//It will print a list of locations and the lines that go to them
	boolean destinationSearch(String s, HashMap<String, BusRoute> routes) {
		
		String subStr;
		String destination;
		String destP = "<h3>(" + s + ".*?)</h3>(.*?)<hr id";
		String routeP = "<a href=\"/schedules(.*?)\"(>|\\sclass=&quot;text-success&quot;>)(.*?)</a>";
		String routeID;
		
		boolean found = false;
		
		Pattern pattern = Pattern.compile(destP, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Pattern subPattern = Pattern.compile(routeP, Pattern.DOTALL | Pattern.MULTILINE);
		
		Matcher matcher = pattern.matcher(schedules);
		
		while(matcher.find()) {
			destination = matcher.group(1);
			BusRoute newRoute = new BusRoute(destination);
			subStr = matcher.group(2);
			
			Matcher subMatcher = subPattern.matcher(subStr);
			found = true;
			
			while(subMatcher.find()) {
				routeID = subMatcher.group(3);
				//need to get rid of that pesky * after some routes
				routeID = routeID.split(" ")[0];
				newRoute.addRoute(routeID);
			}
			routes.put(destination, newRoute);
		}
		return found;
	}

	//Patterns:
	//<h2>Weekday<small>To Arlington</small></h2>...</thead>
	//regex:<h2>Weekday<small>(.*?)</small></h2>.*?<tr>(.*?)</tr>
	//<strong class="fa fa-stack-1x">C</strong>
	//<p>Boeing Gate E68</p>

	//This is the method which returns details to the user about any route they enter.
	//Will not accept invalid routes.
	boolean routeDetails(String subRoute)throws Exception {
		if(!subConnection(subRoute)){
			return false;
		}
		//TO A
		int alength = 0;
		String toA;
		String[] stopNumA = new String[20];
		String[] stopAddrA = new String[20];
		//TO B -- these are usually just the stops in reverse order.
		int blength = 0;
		String toB;
		String[] stopNumB = new String[20];
		String[] stopAddrB = new String[20];
		
		String routeP = "<h2>Weekday<small>(.*?)</small></h2>.*?<tr>(.*?)</tr>";
		String stopNumP = "<strong class=\"fa fa-stack-1x\">(.*?)</strong>.*?<p>(.*?)</p>.*?</th>";
		//String stopAddrP = "<p>(.*?)</p>";
		
		Pattern pattern = Pattern.compile(routeP, Pattern.DOTALL);
		Pattern numPattern = Pattern.compile(stopNumP, Pattern.DOTALL| Pattern.MULTILINE);
		//Pattern addrPattern = Pattern.compile(stopAddrP, Pattern.DOTALL | Pattern.MULTILINE);

		Matcher matcher = pattern.matcher(routeSchedule);
		//Weekday To A
		matcher.find();
		toA = matcher.group(1);
		int index = 0;
		Matcher stopMatcher = numPattern.matcher(matcher.group(2));
		
		while(stopMatcher.find()) {
			stopNumA[index] = stopMatcher.group(1);
			stopAddrA[index] = stopMatcher.group(2);
			index++;
		}
		alength = index;
		//Weekday to B
		matcher.find();
		toB = matcher.group(1);
		index = 0;
		stopMatcher = numPattern.matcher(matcher.group(2));
		
		while(stopMatcher.find()) {
			stopNumB[index] = stopMatcher.group(1);
			stopAddrB[index] = stopMatcher.group(2);
			index++;
		}
		blength = index;
		System.out.println(toA);
		System.out.println("++++++++++++++++++++++++++++");
		for(int i = 0; i < alength; i++) {
			System.out.println(stopNumA[i] + ": " + stopAddrA[i]);
		}
		System.out.println("+++++++++++++++++++++++++++++");
		for(int i = 0; i < blength; i++) {
			System.out.println(stopNumB[i] + ": " + stopAddrB[i]);
		}
		return true;
	}
}


