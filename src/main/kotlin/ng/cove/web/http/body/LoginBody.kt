package ng.cove.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ng.cove.web.data.model.UserRole

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class LoginBody {
    @field:NotBlank(message = "Otp cannot be blank") lateinit var otp: String
    @field:NotBlank(message = "ref cannot be blank") lateinit var ref: String
    @field:NotNull lateinit var role: UserRole
    @field:NotBlank(message = "deviceName cannot be blank") lateinit var deviceName: String
    @Schema(example = "false")
    var returnIdToken : Boolean = false
}