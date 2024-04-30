package co.gatedaccess.web.http.body

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

data class OtpRefBody(var ref: String, var phone: String, @JsonProperty("expire_at") var expireAt: Date)