package ng.cove.web.data.model

import com.mongodb.lang.NonNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.util.Date

@Document("bill_payments")
class BillPayment {
    @Id
    var id: String? = null
    @field:NonNull
    @Field("bill_id")
    var billId: String? = null
    @field:NonNull
    @Field("member_id")
    var memberId: String? = null
    @field:NonNull
    @Field("due_date")
    var dueDate: Date? = null
}