package connection;

import java.util.Map;

import info.LocatorThread;
import info.TemperatureThread;
import util.SyncArrayList;
import util.URLParsing;

import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;

public class ConnectionThread extends Thread {
	private SyncArrayList<Thread> ts;
	HttpExchange httpExchange;

    public ConnectionThread(HttpExchange httpExchange, SyncArrayList<Thread> ts) {
        super("MultiServerThread");
        this.httpExchange = httpExchange;
        this.ts = ts;
    }
    
    public void run() {
    	Thread t1 = null, t2 = null;
    	String[] a = new String[2];
        StringBuilder response = new StringBuilder();
    	try {
            Map <String,String>parms = URLParsing.queryToMap(httpExchange.getRequestURI().getQuery());
        	//Get lat longt from httpExchange
        	String lat = parms.get("lat");
        	String longt = parms.get("lon");
        	t1 = new LocatorThread(lat, longt, a);
        	t2 = new TemperatureThread(lat, longt, a);
    		t1.start();
    		t2.start();
			//TODO: timeout observer
    		t1.join(3000);
    		t2.join(3000);
    	} catch (InterruptedException e) {
    		//write something to httpExchange
    		response.append("interrupted error");
    	} catch (Exception e1) {
    		
    	} finally {
    		if (t1 != null && t1.isAlive()) {
    			t1.interrupt();
    		}
    		if (t2 != null && t2.isAlive()) {
    			t2.interrupt();
    		}
        	// Write result to httpExchange 
            // response.append("<html><body>");
            // response.append("A : " + a[0] + "<br/>");
            // response.append("B : " + a[1] + "<br/>");
            // response.append("</body></html>");
            response.append(a[0] + ", " + a[1]);
            try {
	            httpExchange.sendResponseHeaders(200, response.length());
	            OutputStream os = httpExchange.getResponseBody();
	            os.write(response.toString().getBytes());
	            os.close();
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	ts.remove(this);
            }
    	}
    }
	public static void main(String[] args) {
		try {
				String[] a = new String[2];
				Thread t = new TemperatureThread("37.76893497", "-122.42284884", a);
				Thread t1 = new LocatorThread("37.76893497", "-122.42284884", a);
				t.start();
				t1.start();
				try {
					//DELETEME: just for testing
		    		t1.join(3000);
		    		t.join(3000);
		    	} catch (InterruptedException e) {
		    		//write something to httpExchange
		    	} finally {
		    		if (t1.isAlive()) {
		    			t1.interrupt();
		    		}
		    		if (t.isAlive()) {
		    			t.interrupt();
		    		}
		    	}
				System.out.println(a[0] + ", " + a[1]);
				return;
		} catch (Throwable t) {
			t.printStackTrace();
		}
		System.exit(1);
	}
}
