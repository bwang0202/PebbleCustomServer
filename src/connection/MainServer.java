package connection;
import java.net.*;
import java.util.ArrayList;

import util.SyncArrayList;

import java.io.*;
 
public class MainServer {
    public static void main(String[] args) throws IOException {
    	SyncArrayList<Thread> ts = new SyncArrayList<Thread>();
    	//shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	ts.setLocked();
            	ArrayList<Thread> al = ts.getLockedArray();
            	try {
            		for ( Thread t : al ) {
            			t.join();
            		}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
        });
    	int portNumber;
        if (args.length != 1) {
            portNumber = 4443;
        } else {
        	portNumber = Integer.parseInt(args[0]);
        }
        try (ServerSocket serverSocket =
                new ServerSocket(portNumber);){
        		while (true) {
                    Socket clientSocket = serverSocket.accept();
                    Thread tt=new ConnectionThread(clientSocket, ts);
                    System.out.println("starting new connection");
                    // Once ts is locked, this new connection is gonna timeout
                    ts.add(tt);
                    tt.start();
        		}

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}