package lib.web;

import lib.util.*;
import lib.util.StringBuilder;

import java.io.IOException;
import java.io.InputStream;

public class HttpRequestParser {

    private static final Character CR = '\r';
    private static final Character LF = '\n';

    private final InputStream inputStream;

    public HttpRequestParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpRequest parse() throws IOException {

        String inputLine = readLine();

        int firstSpace = inputLine.indexOf(" ");
        int secondSpace = inputLine.indexOf(" ", firstSpace + 1);

        if (firstSpace == -1 || secondSpace == -1) {
            throw new IOException("Invalid HTTP request line: " + inputLine);
        }

        String methodString =
                inputLine.substring(0, firstSpace);

        String requestTarget =
                inputLine.substring(firstSpace + 1, secondSpace);

        String versionString =
                inputLine.substring(secondSpace + 1);


        int questionMarkIndex =
                requestTarget.indexOf("?");

        String path;
        String queryString;

        if (questionMarkIndex == -1) {
            path = requestTarget;
            queryString = "";
        } else {
            path = requestTarget.substring(
                    0,
                    questionMarkIndex
            );

            queryString = requestTarget.substring(
                    questionMarkIndex + 1
            );
        }


        HttpMethod method = parseMethod(methodString);
        HttpVersion version = parseVersion(versionString);
        Map<String, String> queryParams = parseQueryParameters(queryString);
        List<HttpHeader> headers = new LinkedList<>();

        while (true) {
            String headerLine = readLine();
            if (headerLine.isEmpty()) {
                break;
            }
            HttpHeader header = parseHeader(headerLine);
            headers.add(header);
        }


        HttpBody body = new HttpBody(new LinkedList<>());


        return new HttpRequest(
                method,
                path,
                version,
                queryParams,
                headers,
                body
        );
    }

    private String readLine() throws IOException {
        StringBuilder builder = new StringBuilder();

        int currentByte;

        while ((currentByte = inputStream.read()) != -1) {
            char currentChar = (char) currentByte;

            if (currentChar == CR) {
                int nextByte = inputStream.read();

                if (nextByte != LF) {
                    throw new IOException("Invalid line ending");
                }

                return builder.toString();
            }
            builder.append(currentChar);
        }

        return builder.toString();
    }

    private Map<String, String> parseQueryParameters(
            String queryString
    ) throws IOException {
        Map<String, String> parameters = new HashMap<>();

        if (queryString.isEmpty()) {
            return parameters;
        }

        int parameterStart = 0;

        for (int i = 0; i <= queryString.length(); i++) {
            boolean parameterEnded =
                    i == queryString.length()
                            || queryString.charAt(i) == '&';

            if (!parameterEnded) {
                continue;
            }

            String parameter =
                    queryString.substring(parameterStart, i);

            parseQueryParameter(parameter, parameters);

            parameterStart = i + 1;
        }

        return parameters;
    }

    private void parseQueryParameter(
            String parameter,
            Map<String, String> parameters
    ) throws IOException {
        int equalsIndex = parameter.indexOf("=");

        if (equalsIndex == -1) {
            throw new IOException(
                    "Invalid query parameter: " + parameter
            );
        }

        String name =
                parameter.substring(0, equalsIndex);

        String value =
                parameter.substring(equalsIndex + 1);

        if (name.isEmpty()) {
            throw new IOException(
                    "Query parameter name cannot be empty"
            );
        }

        parameters.put(name, value);
    }

    private HttpMethod parseMethod(String value) {
        return switch (value) {
            case "GET" -> HttpMethod.GET;
            case "POST" -> HttpMethod.POST;
            case "PUT" -> HttpMethod.PUT;
            case "DELETE" -> HttpMethod.DELETE;
            default -> HttpMethod.INVALID;
        };
    }


    private HttpVersion parseVersion(String value) {
        if (value.equals("HTTP/1.0")) {
            return HttpVersion.HTTP_1_0;
        }

        if (value.equals("HTTP/1.1")) {
            return HttpVersion.HTTP_1_1;
        }

        return HttpVersion.INVALID;
    }

    private HttpHeader parseHeader(String line) throws IOException {
        int colonIndex = line.indexOf(':');
        if (colonIndex == -1) {
            throw new IOException("Invalid HTTP header line: " + line);
        }

        String key = line.substring(0, colonIndex);
        String value =
                line.substring(colonIndex + 1).trim();

        return new HttpHeader(key, value);
    }

}
