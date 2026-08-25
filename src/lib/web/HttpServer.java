package lib.web;

import lib.logging.Logger;
import lib.logging.LoggerFactory;
import lib.util.Optional;

import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);

    private final int port;
    private final WebRouter router;

    private boolean running;

    public HttpServer(int port, WebRouter router) {
        this.running = false;
        this.router = router;
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
            HttpResponse response = handleRequest(request);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            LOGGER.error("Could not handle client connection: ", e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        LOGGER.debug("Received request: " + request);

        Optional<Route> routeOpt = router.findRoute(
                request.method(),
                request.path()
        );

        if (routeOpt.isEmpty()) {
            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.NOT_FOUND,
                    "Route not found"
            );
        }

        Route route = routeOpt.get();

        try {
            Object result = route.handlerMethod().invoke(
                    route.controller()
            );

            if (!(result instanceof String body)) {
                return HttpResponseFactory.createHttpResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Controller method must return String"
                );
            }

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.OK,
                    body
            );
        } catch (InvocationTargetException exception) {
            LOGGER.error(
                    "Controller method failed: "
                            + route.handlerMethod().getName(),
                    exception.getCause()
            );

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error"
            );
        } catch (IllegalAccessException exception) {
            LOGGER.error(
                    "Cannot access controller method: "
                            + route.handlerMethod().getName(),
                    exception
            );

            return HttpResponseFactory.createHttpResponse(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error"
            );
        }
    }
}


