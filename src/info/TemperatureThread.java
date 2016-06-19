package info;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.*;

public class TemperatureThread extends Thread {
	private String apiKey = "1fde8343f147f3f67f3a14d56b7d0799";
	private String lat, longt;
	private String[] resp;

	public TemperatureThread(String lat2, String longt2, String[] ref) {
		this.lat = lat2;
		this.longt = longt2;
		this.resp = ref;
	}

	public void start() {
		String result = "";
		for (int i = 0; i < 2; i ++){
			BufferedReader in = null;
			try {
				String url = "http://api.openweathermap.org/data/2.5/weather?lat=" +
						lat + "&lon=" + longt + "&appid=" + apiKey;

				URL obj = new URL(url);
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");
				con.setConnectTimeout(1000);
				con.setReadTimeout(4000);

				int responseCode = con.getResponseCode();

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
			} catch (Exception e) {
				result = "Weather server error";
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
		String cond = jelement.getAsJsonObject()
				.getAsJsonArray("weather")
				.get(0)
				.getAsJsonObject()
				.get("main")
				.toString();
		String temp = jelement.getAsJsonObject().get("main").getAsJsonObject().get("temp").toString();
		float tf = (float) (Float.parseFloat(temp) - 273.0);
		return cond + ", " + tf;
	}
	
	public static void main(String[] args) {
		try {
				String[] a = new String[2];
				Thread t = new TemperatureThread("37.76893497", "-122.42284884", a);
				t.start();
				t.join();
				System.out.println(a[0]);
				return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}
}
