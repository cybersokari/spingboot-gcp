package co.gatedaccess.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import jakarta.validation.constraints.NotNull

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class LoginBody(@NotNull val otp: String, @NotNull val ref: String, @NotNull val deviceId: String, @NotNull val deviceName: String)