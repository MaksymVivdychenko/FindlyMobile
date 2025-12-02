package com.example.findly.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.findly.MainActivity
import com.example.findly.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Цей метод викликається, коли додаток відкритий (Foreground),
    // або коли приходить Data Message.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "From: ${remoteMessage.from}")

        // 1. Перевірка, чи містить повідомлення payload з даними (Data)
        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FCM", "Message data payload: ${remoteMessage.data}")
            // Тут можна обробити дані для фонової роботи
        }

        // 2. Перевірка, чи містить повідомлення payload сповіщення (Notification)
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
            // Вручну показуємо сповіщення, оскільки Android не робить цього автоматично,
            // коли додаток відкритий на екрані.
            sendNotification(it.title, it.body)
        }
    }

    // Викликається, коли токен оновлюється.
    // Важливо відправити цей токен на ваш C# бекенд.
    override fun onNewToken(token: String) {
        Log.d("FCM", "Refreshed token: $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // TODO: Реалізуйте тут логіку відправки токена на ваш API
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        // 1. Змінюємо ID каналу, щоб Android створив його заново з новими налаштуваннями
        val channelId = "findly_high_priority_channel"
        val notificationId = Random.nextInt()

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Налаштування повідомлення
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher) // Ваша іконка
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            // ВАЖЛИВО ДЛЯ СПЛИВАННЯ:
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Вмикає звук і вібрацію (необхідно для Heads-up)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // Щоб показувати на заблокованому екрані

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 3. Створення каналу з ВИСОКОЮ важливістю
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Важливі сповіщення Findly", // Назва, яку бачить юзер
                NotificationManager.IMPORTANCE_HIGH // ВАЖЛИВО! Це змушує повідомлення "вистрибувати"
            ).apply {
                description = "Сповіщення, які спливають поверх екрану"
                enableVibration(true) // Вібрація теж допомагає привернути увагу
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}