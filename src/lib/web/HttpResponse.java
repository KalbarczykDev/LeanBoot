package lib.web;

public record HttpResponse(
        HttpStatus status,
        String body
) {
    @Override
    public String toString() {
        return status
                + "Content-Type: text/plain\r\n"
                + "Content-Length: "
                + body.getBytes().length
                + "\r\n"
                + "Connection: close\r\n"
                + "\r\n"
                + body;
    }
}


