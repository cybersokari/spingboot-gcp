package ng.cove.web.http.body

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.util.Date


data class OtpRefBody(var ref: String, var phone: String, @field:JsonProperty("expire_at") var expireAt: Date, @field:JsonProperty("daily_trial_left") var dailyTrialLeft: Int?)