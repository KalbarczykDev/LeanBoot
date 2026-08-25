package lib.web;


import lib.util.List;

public record HttpRequest(
        HttpMethod method,
        String path,
        HttpVersion version,
        List<HttpHeader> headers,
        HttpBody body
) {
}