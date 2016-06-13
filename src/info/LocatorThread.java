package info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocatorThread extends Thread {
	private double lat, longt;
	private String[] resp;

	public LocatorThread(double lat, double longt, String[] ref) {
		this.lat = lat;
		this.longt = longt;
		this.resp = ref;
	}

	public void start() {
		StringBuffer response = new StringBuffer();
		for (int i = 0; i < 5; i ++){
			BufferedReader in = null;
			try {
				String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + 
			lat + "," + longt;

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");
				
				int responseCode = con.getResponseCode();
				System.out.println("\nSending 'GET' request to URL : " + url);
				System.out.println("Response Code : " + responseCode);

				in = new BufferedReader(
						new InputStreamReader(con.getInputStream()));
				String inputLine;

				while ((inputLine = in.readLine()) != null) {
					if (inputLine.contains("formatted_address")) {
						//Get the string after the third " character and before the fourth " character
						char[] chars = inputLine.toCharArray();
						response = new StringBuffer();
						int count = 0;
						int j = 0;
						while (j < chars.length) {
							if (chars[j] == '"') {
								count++;
								if (count == 3) {
									while (j + 1 < chars.length && chars[j + 1] != '"') {
										response.append(chars[j]);
									}
									break;
								} else {
									j++;
								}
							} else {
								j++;
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// response.toString() is result
		this.resp[0] = response.toString();
	}
	

	public void interrupt() {
		this.resp[0] = "Locator result timed out";
		super.interrupt();
	}
}
