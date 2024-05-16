package ng.cove.web.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class NotificationService {

    @Async
    fun notifyUserDeviceWhenPossible(title: String?, message: String?, token: String, data: Map<String, String>) {
        //Check if user has granted notification permission on this device

        val notification = Notification.builder()
            .setTitle(title)
            .setBody(message).build()
        val fcmMessage = Message.builder()
            .putAllData(data)
            .setToken(token)
            .setNotification(notification).build()

        FirebaseMessaging.getInstance().send(fcmMessage)
    }
}
