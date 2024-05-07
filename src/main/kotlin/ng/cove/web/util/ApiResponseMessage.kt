package ng.cove.web.util

object ApiResponseMessage {
    const val ACCESS_CODE_INVALID= "Access code is invalid"
    const val MEMBER_IN_EXISTING_COMMUNITY = "User already part of a community"
    const val REQUEST_CANT_BE_FOUND: String = "Request does not exist"
    const val REQUEST_ALREADY_ACCEPTED: String = "Request has already been accepted"
    const val USER_NOT_SUPER_ADMIN: String = "This user is not a super admin of this community"
    const val PHOTO_IS_REQUIRED: String = "Upload profile photo before joining a community"
}
