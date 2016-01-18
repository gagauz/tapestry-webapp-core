package org.gagauz.tapestry.web.services;

public class CustomHttpResponse {

    private int code;
    private String url;
    private String message = "";

    public CustomHttpResponse(int code, String url) {
        this.code = code;
        this.url = url;
    }

    public CustomHttpResponse(int code, String url, String message) {
        this(code, url);
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    public String getMessage() {
        return message;
    }
}
