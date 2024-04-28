package co.gatedaccess.web.http.body

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
class OtpRefBody(var ref: String?, var phone: String?, var expireAt: Date)