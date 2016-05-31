package connection;

import java.net.*;

import util.SyncArrayList;

import java.io.*;

public class ConnectionThread extends Thread {
    private Socket socket = null;
	private SyncArrayList<Thread> ts;

    public ConnectionThread(Socket socket, SyncArrayList<Thread> ts) {
        super("MultiServerThread");
        this.socket = socket;
        this.ts = ts;
    }
    
    public void run() {
        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        )
        {
            String inputLine;
            inputLine = in.readLine();
            //get lat and long
            out.write(inputLine + "\n\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
				socket.close();
				ts.remove(Thread.currentThread());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
}
