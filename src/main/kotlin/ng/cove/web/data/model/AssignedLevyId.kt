package ng.cove.web.data.model

import org.springframework.data.mongodb.core.mapping.Field

class AssignedLevyId (
    @Field("levy_id") val levyId: String,
    @Field("member_id") val memberId: String
)