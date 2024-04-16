package co.gatedaccess.web.http;

public class TokenBody {
    String token;

    public TokenBody() {
    }

    public TokenBody(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public TokenBody setToken(String token) {
        this.token = token;
        return this;
    }
}
