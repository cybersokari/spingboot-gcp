package ng.cove.web.data.model

import java.util.*

interface User {
    var id: String?
    var firstName: String?
    var lastName: String?
    var phone: String?
    var communityId: String?
    var photoUrl: String?
    var phoneVerifiedAt: Date?
    var createdAt: Date?
    var deviceName: String?
    var fcmToken: String?
    var lastLoginAt: Date?
    var lastModifiedAt: Date?
    var testOtp: String?
}