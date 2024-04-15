package co.gatedaccess.web.http;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenBody {
    String token;
    @JsonProperty("error")
    String errorMessage;

    public TokenBody() {
    }

    public TokenBody(String token) {
        this.token = token;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getToken() {
        return token;
    }

    public TokenBody setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public TokenBody setToken(String token) {
        this.token = token;
        return this;
    }
}
