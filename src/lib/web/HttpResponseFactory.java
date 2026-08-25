package lib.web;

public class HttpResponseFactory {

    private HttpResponseFactory() {
    }

    public static HttpResponse createHttpResponse(HttpStatus statusCode, String body) {
        return new HttpResponse(statusCode, body);
    }
}
