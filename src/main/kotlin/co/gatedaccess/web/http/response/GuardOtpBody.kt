package co.gatedaccess.web.http.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

class GuardOtpBody(var otp: String?, @field:JsonProperty("expire_at") var expiryAt: Date?)
