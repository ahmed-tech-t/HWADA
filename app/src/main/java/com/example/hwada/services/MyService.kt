package com.example.hwada.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.example.hwada.Model.Chat
import com.example.hwada.Model.User
import com.example.hwada.R
import com.example.hwada.application.App
import com.example.hwada.repository.NotificationRepo
import com.example.hwada.ui.ChatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MyService : Service(){
    val ioScope = CoroutineScope(Dispatchers.IO)

    private val TAG = "MyService"
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    var first = true

    @Inject
    lateinit var repo : NotificationRepo;



    @Inject
   lateinit var  application:App

    private val binder = MyBinder()

    fun observe(user:User) = serviceScope.launch {
        Log.d(TAG, "observe: Starting service")
        repo.chatListener(user.uId)

       repo.chatListener(user.uId).collect{
          if(application.checkNotificationPermissions()){
              if(application.currentChatId != it.receiver.uId ){
                  if(!it.lastMessage.isSeen && user.uId != it.lastMessage.senderId) showNotification(it,user)
              }
          }
       }
    }

    private fun showNotification(chat: Chat ,user: User) = ioScope.launch {
        val intent = Intent(this@MyService,ChatActivity::class.java)
        intent.putExtra(getString(R.string.userVal), user)
        intent.putExtra(getString(R.string.chatVal), chat)
        val pendingIntent = PendingIntent.getActivity(
            this@MyService,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        val bitmap = Glide.with(this@MyService).asBitmap().load( chat.receiver.image).submit().get();
        val notification = NotificationCompat.Builder(applicationContext, "direct_message")
            .setSmallIcon(R.drawable.app_icon_img)
            .setLargeIcon(bitmap)
            .setContentTitle(chat.receiver.username)
            .setContentText(chat.lastMessage.body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder;
    }

    inner class MyBinder : Binder() {
        fun getService(): MyService = this@MyService
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY_COMPATIBILITY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d(TAG, "onTaskRemoved: remove service")
        super.onTaskRemoved(rootIntent)
    }
}