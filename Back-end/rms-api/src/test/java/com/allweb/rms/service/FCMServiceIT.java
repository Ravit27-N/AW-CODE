package com.allweb.rms.service;

import com.allweb.rms.config.FirebaseConfig;
import com.allweb.rms.core.firebase.FCMUtils;
import com.allweb.rms.core.firebase.WebPushMessageConfigurer;
import com.allweb.rms.core.firebase.WebPushMulticastMessageConfigurer;
import com.google.firebase.ErrorCode;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest(classes = {FirebaseConfig.class, FCMService.class})
@TestPropertySource("classpath:application.properties")
class FCMServiceIT {

    @Autowired
    private FCMService fcmService;

    @Test
    void testWebPushMessageWithASpecifiedTopic() throws FirebaseMessagingException {
        WebPushMessageConfigurer webPushMessageConfigurer = new WebPushMessageConfigurer();
        // Set notification title
        webPushMessageConfigurer.setTitle("title");
        // Set notification message body
        webPushMessageConfigurer.setBody("body");
        // Set target client device by specifying the client device token.
        // All clients subscribed to this topic will receive the notification.
        webPushMessageConfigurer.setTopic("testTopic");
        // Put some data into the data section of the message.
        webPushMessageConfigurer.putCustomNotificationData("key", "value");
        // Set the expire time or time to live of this message.
        // Set to 0L to send the message immediately (fire and forget).
        webPushMessageConfigurer.setExpiredTimeInSeconds(0L);
        // Create a new web push message that can be sent to a specific topic using the
        // above configuration.
        Message message = FCMUtils.createMessage(webPushMessageConfigurer);

        // The success response message id pattern:
        // "projects/${RMSFirebaseProjectId}/messages/id"
        String messageId = fcmService.sendMessage(message);
        assertThat(messageId, not(emptyOrNullString()));
        assertThat(messageId, matchesRegex("^projects/.*/messages/.*$"));
    }

    @Test
    void testWebPushMessageWithInvalidToken() {
        WebPushMessageConfigurer webPushMessageConfigurer = new WebPushMessageConfigurer();
        // Set notification title
        webPushMessageConfigurer.setTitle("title");
        // Set notification message body
        webPushMessageConfigurer.setBody("body");
        // Set target client device by specifying the client device token.
        // This token is invalid.
        // This will throw an FirebaseMessagingException with ErrorCode.INVALID_ARGUMENT
        // when Firebase try to send message to the client of this token.
        webPushMessageConfigurer.setDeviceToken("invalidToken");
        // Put some data into the data section of the message.
        webPushMessageConfigurer.putCustomNotificationData("key", "value");
        // Set the expire time or time to live of this message.
        // Set to 0L to send the message immediately (fire and forget).
        webPushMessageConfigurer.setExpiredTimeInSeconds(0L);
        // Create a new web push message that can be sent to a specific client using the
        // above configuration.
        Message message = FCMUtils.createMessage(webPushMessageConfigurer);

        // ---
        FirebaseMessagingException exception = null;
        try {
            // The exception will be thrown because of the invalid token.
            fcmService.sendMessage(message);
        } catch (FirebaseMessagingException e) {
            exception = e;
        }
        assertThat(exception, is(not(equalTo(nullValue()))));
        assert exception != null;
        ErrorCode errorCode = exception.getErrorCode();
        assertThat(errorCode, equalTo(ErrorCode.INVALID_ARGUMENT));
    }

    @Test
    void testWebPushMulticastMessageWithInvalidUserDeviceToken()
            throws FirebaseMessagingException {
        WebPushMulticastMessageConfigurer configurer = new WebPushMulticastMessageConfigurer();
        configurer.addDeviceToken("aabbcc");
        // Set notification title
        configurer.setTitle("title");
        // Set notification message body
        configurer.setBody("body");
        // Put some data into the data section of the message.
        configurer.putCustomNotificationData("key", "value");
        // Set the expire time or time to live of this message.
        // Set to 0L to send the message immediately (fire and forget).
        configurer.setExpiredTimeInSeconds(0L);
        // Create a new web push message that can be sent to multiple clients using the
        // above configuration.
        MulticastMessage message = FCMUtils.createMulticastMessage(configurer);
        // Send and wait for the response.
        BatchResponse response = fcmService.sendMulticastMessage(message);

        assertThat(response.getResponses().get(0).getException().getErrorCode(), equalTo(ErrorCode.INVALID_ARGUMENT));
    }
}
