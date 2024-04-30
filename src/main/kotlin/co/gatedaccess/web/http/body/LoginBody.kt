package co.gatedaccess.web.http.body

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

//@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class) Does not work in data class
data class LoginBody(
    @field:NotBlank val otp: String,
    @field:NotBlank val ref: String,
    @field:NotBlank @JsonProperty("device_id") val deviceId: String,
    @field:NotBlank @JsonProperty("device_name")  val deviceName: String
)