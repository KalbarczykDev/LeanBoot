package lib.web;

public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

    private final String statusText;
    private final int statusCode;

    HttpStatus(int statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public String getStatusText() {
        return statusText;
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 "
                + statusCode
                + " "
                + statusText
                + "\r\n";
    }
}