package assignmentOne;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.*;
/************************************************************************
 * @author Joshua														*
 * This is the main application class for a program which				*
 * finds and lists the potential destinations using						*
 * https://www.communitytransit.org/busservice/schedules/				*
 * Will accept a character, then an integer representing a bus line.	*
 * The bus line does not need to be one shown to the user to be valid.	*
 ***********************************************************************/
public class BusScheduleApp {

	public static void main(String[] args) throws Exception {
		
		HashMap<String, BusRoute> routes = new HashMap<>();
		
		Scanner sysin = new Scanner(System.in);
		String entry;
		
		RouteFinder rf = new RouteFinder();
		rf.initialConnection();
		
		System.out.println("Please Enter the first letter of your destination.");
		entry = sysin.next(Pattern.compile("[a-zA-Z]"));
		
		if(rf.destinationSearch(entry,routes)) {
			System.out.print(routes.values());
		}else {
			System.out.println("There are no destinations that begin with that letter");
		}
		
		System.out.println("\nWhich route would you like to view?");
		entry = sysin.next();
		
		if(rf.subConnection(entry)) {
			rf.routeDetails(entry);
			//System.out.println("Success! Probably");
		}else {
			System.out.println("Invalid");
		}
		
		sysin.close();

	}

}
