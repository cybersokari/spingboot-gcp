package co.gatedaccess.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class GuardInputBody (val firstName: String, val lastName: String, val phone: String)