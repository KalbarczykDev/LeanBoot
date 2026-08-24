package lib.web;

import lib.util.StringBuilder;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpServer {

    private final int port;
    private boolean running;

    private static final String RESPONSE_BODY =
            """
                    HTTP/1.1 200 OK\r
                    Content-Type: text/plain\r
                    Content-Length: 12\r
                    Connection: close\r
                    \r
                    Hello World!""";


    public HttpServer(int port) {
        this.running = false;
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            running = true;
            while (running) {
                Socket clientSocket = serverSocket.accept();
                Thread.startVirtualThread(
                        () -> handleClient(clientSocket)
                );
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()
        ) {
            String request = readRequest(inputStream);
            String response = handleRequest(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String readRequest(InputStream inputStream) throws IOException {
        StringBuilder input = new StringBuilder();
        int headerEndState = 0;
        int currentByte;
        while ((currentByte = inputStream.read()) != -1) {
            char currentChar = (char) currentByte;
            input.append(currentChar);

            if (headerEndState == 0) {
                headerEndState = currentChar == '\r' ? 1 : 0;
                continue;
            }
            if (headerEndState == 1) {
                headerEndState = currentChar == '\n' ? 2
                        : currentChar == '\r' ? 1
                        : 0;
                continue;
            }
            if (headerEndState == 2) {
                headerEndState = currentChar == '\r' ? 3 : 0;
                continue;
            }
            if (currentChar == '\n') {
                break;
            }

            headerEndState = currentChar == '\r' ? 1 : 0;
        }
        return input.toString();
    }

    private String handleRequest(String request) {
        return RESPONSE_BODY;
    }
}


