package lib.web;

import lib.logging.Logger;
import lib.logging.LoggerFactory;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final int port;
    private final RequestDispatcher dispatcher;

    private boolean running;

    public HttpServer(int port, RequestDispatcher dispatcher) {
        this.running = false;
        this.dispatcher = dispatcher;
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("HTTP Server listening on port: " + port);
            running = true;
            while (running) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.debug("Accepted connection from: " + clientSocket.getInetAddress());
                Thread.startVirtualThread(
                        () -> handleClient(clientSocket)
                );
            }
        } catch (Exception e) {
            LOGGER.error("HTTP server failed on port: " + port, e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                Socket socket = clientSocket;
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream()
        ) {
            HttpRequestParser parser = new HttpRequestParser(inputStream);
            HttpRequest request = parser.parse();
            HttpResponse response = dispatcher.dispatch(request);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            LOGGER.error("Could not handle client connection: ", e);
        }
    }
}


