package assignmentOne;

import java.util.ArrayList;

public class BusRoute implements Comparable<BusRoute> {
	String destination;
	ArrayList<String> routeID; //This is a number like 111, 412, or 201/202
	
	BusRoute(){
		this.routeID = new ArrayList<String>();
	}
	
	BusRoute(String destination){
		this.destination = destination;
		this.routeID = new ArrayList<String>();
	}
	
	String getDestination() {
		return destination;
	}
	
	void addRoute(String id) {
		routeID.add(id);
	}
	
	//Overrides
	@Override
	public int compareTo(BusRoute other) {
		String thisStr = destination;
		String othStr = other.getDestination();
		
		return thisStr.compareTo(othStr);	
	}
	@Override
	public boolean equals(Object obj) {
		BusRoute other = (BusRoute) obj;
		if(this.destination.equals(other.getDestination()))
			return true;
		return false;
	}
	@Override
	public int hashCode() {
		return destination.hashCode();
	}
	@Override
	public String toString() {
		String sOut = destination + ": ";
		routeID.trimToSize();
		String[] temp = routeID.toArray(new String[routeID.size()]);
		for(int i = 0; i < temp.length; i++) {
			sOut +="\n" + temp[i];
		}
		sOut += "\n+++++++++++++++";
		return sOut;
	}
}
