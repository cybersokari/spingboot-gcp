package ng.cove.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class GuardInfoBody (val firstName: String, val lastName: String, val phone: String)