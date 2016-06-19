package info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocatorThread extends Thread {
	private String lat, longt;
	private String[] resp;

	public LocatorThread(String lat2, String longt2, String[] ref) {
		this.lat = lat2;
		this.longt = longt2;
		this.resp = ref;
	}

	public void start() {
		StringBuffer response = new StringBuffer();
		for (int i = 0; i < 2; i ++){
			BufferedReader in = null;
			try {
				String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=" + 
			lat + "," + longt;

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");
				con.setConnectTimeout(1000);
				con.setReadTimeout(4000);
				//TODO: read response code
				int responseCode = con.getResponseCode();

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
										response.append(chars[++j]);
									}
									break;
								} else {
									j++;
								}
							} else {
								j++;
							}
						}
						if (response.length() > 0) break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				response = new StringBuffer("Google api error");
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
		if (response.length() == 0) {
			response = new StringBuffer("no address found.");
		}
		this.resp[1] = response.toString();
	}
	
	public void interrupt() {
		this.resp[1] = "Locator result timed out";
		super.interrupt();
	}
	public static void main(String[] args) {
		try {
				String[] a = new String[2];
				Thread t = new LocatorThread("37.76893497", "-122.42284884", a);
				t.start();
				t.join();
				System.out.println(a[1]);
				return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}
}
