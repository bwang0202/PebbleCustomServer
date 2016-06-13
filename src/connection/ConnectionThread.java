package connection;

import java.net.*;

import javax.xml.ws.spi.http.HttpExchange;

import info.LocatorThread;
import info.TemperatureThread;
import util.SyncArrayList;

import java.io.*;

public class ConnectionThread extends Thread {
	private SyncArrayList<Thread> ts;
	HttpExchange httpExchange;

    public ConnectionThread(HttpExchange httpExchange, SyncArrayList<Thread> ts) {
        super("MultiServerThread");
        this.httpExchange = httpExchange;
        this.ts = ts;
    }
    
    public void run() {
    	//Get lat longt from httpExchange
    	long lat = 0, longt = 0;
    	String[] locatorResult = null, tempResult = null;
    	Thread t1 = new LocatorThread(lat, longt, locatorResult);
    	Thread t2 = new TemperatureThread(lat, longt, tempResult);
		t1.start();
		t2.start();
    	//
    	try {
    		t1.join(5000);
    		t2.join(5000);
    	} catch (InterruptedException e) {
    		//write something to httpExchange
    	} finally {
    		if (t1.isAlive()) {
    			t1.interrupt();
    		}
    		if (t2.isAlive()) {
    			t2.interrupt();
    		}
        	// Write result to httpExchange
        	
    		ts.remove(this);
    	}
    }
}
