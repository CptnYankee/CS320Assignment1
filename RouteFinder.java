package assignmentOne;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;

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


	boolean routeDetails(String subRoute) {
		if(!subConnection(subRoute)){
			return false;
		}
		String[] stopNumA;
		String[] stopAddrA;
		
		String[] stopNumB;
		String[] stopAddrB;
		
		String routePattern = "<h2>Weekday<small>(.*?)</small></h2>.*?<tr>(.*?)</tr>";
		
		return true;
	}
}


