package ng.cove.web.service

import ng.cove.web.data.model.Device
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class NotificationService {

    @Async
    fun notifyUserDeviceWhenPossible(title: String?, message: String?, device: Device, data: Map<String,String>) {
        //Check if user has granted notification permission on this device
        if (device.fcmToken == null) return

        val notification = Notification.builder()
            .setTitle(title)
            .setBody(message).build()
        val fcmMessage = Message.builder()
            .putAllData(data)
            .setToken(device.fcmToken)
            .setNotification(notification).build()

        FirebaseMessaging.getInstance().send(fcmMessage)
    }
}
