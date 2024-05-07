package ng.cove.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotBlank

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class LoginBody {
    @field:NotBlank(message = "Otp cannot be blank") lateinit var otp: String
    @field:NotBlank(message = "ref cannot be blank") lateinit var ref: String
    @field:NotBlank(message = "deviceId cannot be blank") lateinit var deviceId: String
    @field:NotBlank(message = "deviceName cannot be blank") lateinit var deviceName: String
}