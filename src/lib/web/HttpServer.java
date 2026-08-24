package lib.web;

import java.net.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private static final int THREAD_POOL_SIZE = 10;

    private final int port;

    private boolean running;

    String testBody = """
            <html>
                    <head>
                        <title>LeanBoot</title>
                    </head>
                    <body>
                        <h1>LeanBoot</h1>
                        <p>Welcome</p>
                        <ul>
                            <li>
                                <p>You</p>
                            </li>
                            <li>
                                <p>only</p>
                            </li>
                            <li>
                                <p>need</p>
                            </li>
                            <li>
                                <p>java</p>
                            </li>
                        </ul>
                     </body>
                 </html>
            """;

    public HttpServer(int port) {
        this.running = false;
        this.port = port;
    }

    public void start() {

        try (ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
             ServerSocket serverSocket = new ServerSocket(port)) {
            running = true;
            while (running) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (clientSocket; BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             BufferedWriter out = new BufferedWriter(
                     new OutputStreamWriter(clientSocket.getOutputStream())
             )
        ) {
            try {
                String clientInputLine;
                while ((clientInputLine = in.readLine()) != null) {
                    if (clientInputLine.isEmpty()) {
                        break;
                    }
                }
                LocalDateTime now = LocalDateTime.now();

                out.write("HTTP/1.0 200 OK\r\n");
                out.write("Date: " + now + "\r\n");
                out.write("Server: Custom Server\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("Content-Length: " + testBody.length() + "\r\n");
                out.write("\r\n");
                out.write(testBody);
            } catch (Exception e) {
                //
            }
        } catch (IOException e) {
            //
        }
    }
}


