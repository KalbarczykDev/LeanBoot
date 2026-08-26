package lib.web;


import lib.util.List;
import lib.util.Map;

public record HttpRequest(
        HttpMethod method,
        String path,
        HttpVersion version,
        Map<String, String> queryParameters,
        List<HttpHeader> headers,
        HttpBody body
) {
    public String getQueryParameter(String name) {
        return queryParameters.get(name);
    }
}