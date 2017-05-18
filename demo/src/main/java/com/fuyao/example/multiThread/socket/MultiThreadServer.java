package com.fuyao.example.multiThread.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {

	private int port = 10086;
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private final int POOL_SIZE = 10;
    
    private List<PrintStream> printStreamList = new ArrayList<PrintStream>();
    
    public MultiThreadServer() throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * POOL_SIZE);
        System.out.println("服务已启动");
    }
    
    public void service() {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                //executorService.execute(new Handler(socket));
                Handler h = new Handler(socket);
                printStreamList.add(new PrintStream(socket.getOutputStream()));
                new Thread(h).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new MultiThreadServer().service();
    }

    class Handler implements Runnable {
        
        public static final String CHARCODE = "utf-8";
        
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private PrintStream getWriter(Socket socket) throws IOException {
        	PrintStream pw = new PrintStream(socket.getOutputStream());
            return pw;
        }

        private BufferedReader getReader(Socket socket) throws IOException {
            InputStream socketIn = socket.getInputStream();
            return new BufferedReader(new InputStreamReader(socketIn));
        }

        public void run() {
            BufferedReader br = null;
            //PrintStream out = null;
            try {
                br = getReader(socket);

                //out = getWriter(socket);
                String msg = null;
                while ((msg = br.readLine()) != null) {
                    String res = msg;
                    //res = Util.encode(res.getBytes(CHARCODE));
                    System.out.println(msg);
                    for(PrintStream out : printStreamList){
                    	System.out.println(out);
                    	out.println(msg);
                        out.flush();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (br != null)
                        br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               /* if (out != null) {
                    out.close();
                }*/
            }
        }
    }

}
