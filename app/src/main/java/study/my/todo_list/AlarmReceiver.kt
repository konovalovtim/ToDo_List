package study.my.todo_list

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import study.my.todo_list.database.ToDoListDatabase
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    private val GROUP_MESSAGE: String = "TODOLIST"
    private var toDoListDatabase: ToDoListDatabase? = null

    @SuppressLint("WrongConstant")
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { initiateDatabase(it) }
        val notificationManager: NotificationManager =
            context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        var isShow = intent?.getIntExtra("isShow", 0) ?: 0
        val dbId = intent?.getLongExtra("id", -1) ?: -1
        val title = intent?.getStringExtra("title") ?: ""
        val time = intent?.getStringExtra("date")?:""
        Log.d("Alarm Title", "title : $title")
        val icon = R.drawable.ic_launcher_background
        val notificationChannel =
            NotificationChannel("Remainder", "My Notifications", NotificationManager.IMPORTANCE_MAX)
        // Настраиваем канал уведомлений
        notificationChannel.description = "Sample Channel description"
        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(context, "Remainder")
            .setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(NotificationCompat.VISIBILITY_PUBLIC)
            .setColor(Color.RED)
            .setGroup(GROUP_MESSAGE)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .build()

        notificationManager.notify(getNumber(), notification)
        toDoListDatabase?.toDoListDao()?.isShownUpdate(id = dbId, isShow = 1)
        val list = toDoListDatabase?.toDoListDao()?.get(dbId)
        Log.d("IsRead","isRead "+list?.isShow)

    }

    private fun initiateDatabase(context: Context) {
        if (toDoListDatabase == null)
            toDoListDatabase = ToDoListDatabase.getInstance(context)
    }

    private fun getNumber(): Int = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
}