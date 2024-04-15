package co.gatedaccess.web.http;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class GuardOtpBody {
    public GuardOtpBody(String otp, Date expiryAt) {
        this.otp = otp;
        this.expiryAt = expiryAt;
    }

    public String getOtp() {
        return otp;
    }

    public Date getExpiryAt() {
        return expiryAt;
    }

    String otp;
    @JsonProperty("expire_at")
    Date expiryAt;
}
