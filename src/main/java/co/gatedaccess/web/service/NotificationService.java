package co.gatedaccess.web.service;

import co.gatedaccess.web.data.model.Device;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyUserDeviceWhenPossible(String title, String message, Device device) {
        //Check if user has granted notification permission on this device
        if (device.getFcmToken() == null) return;

        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(message).build();
        Message fcmMessage = Message.builder()
                .setToken(device.getFcmToken())
                .setNotification(notification).build();

        FirebaseMessaging.getInstance().sendAsync(fcmMessage);
    }

}
