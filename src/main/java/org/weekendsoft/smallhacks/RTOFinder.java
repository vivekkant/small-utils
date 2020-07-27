package org.weekendsoft.smallhacks;

public class RTOFinder {

	public static void main(String[] args) {
		
		String vehicleNo = null;
		
		if (args.length < 1 && args[0].length() > 2) {
			System.out.println("Please pass the correct vehicle registration number");
			System.exit(-1);
		}
		
		vehicleNo = args[0].toUpperCase();
		
		String rto = findRTO(vehicleNo);
		
		System.out.println("Vehicle No given is : " + vehicleNo);
		System.out.println("RTO found is : " + rto);

	}
	
	public static String findRTO(String vehicleNo) {
		
		StringBuilder rto = new StringBuilder();
		rto.append(vehicleNo.substring(0, 2));
		
		for (int i = 2; i < vehicleNo.length(); i++) {
			char c = vehicleNo.charAt(i);
			if ("0123456789".indexOf(c) == -1) {
				break;
			}
			rto.append(c);
		}
		
		return rto.toString();
	}

}
