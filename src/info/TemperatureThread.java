package info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class TemperatureThread extends Thread {
	private String apiKey = "1fde8343f147f3f67f3a14d56b7d0799";
	private double lat, longt;
	private String[] resp;

	public TemperatureThread(double lat, double longt, String[] ref) {
		this.lat = lat;
		this.longt = longt;
		this.resp = ref;
	}

	public void start() {
		String result = "";
		for (int i = 0; i < 5; i ++){
			BufferedReader in = null;
			try {
				String url = "http://api.openweathermap.org/data/2.5/weather?lat=" +
						lat + "&lon=" + longt + "&appid=" + apiKey;

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
				StringBuffer sb = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					sb.append(inputLine);
				}
				//parse sb to json
				result = parse(sb.toString());
				break;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		// result is result
		this.resp[0] = result;
	}

	public void interrupt() {
		this.resp[0] = "Temperature result timed out";
		super.interrupt();
	}
	
	private String parse(String string) {
		JsonElement jelement = new JsonParser().parse(string);
		String cond = jelement.getAsJsonObject().getAsJsonArray("weather").get(0).getAsJsonObject().get("Main").toString();
		String temp = jelement.getAsJsonObject().get("Main").getAsJsonObject().get("temp").toString();
		return cond + ", " + temp;
	}
}
