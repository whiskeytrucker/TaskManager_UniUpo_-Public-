package it.polettomatteo.taskmanager_uniupo.interfaces

import it.polettomatteo.taskmanager_uniupo.dataclass.Notification
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface NotificationAPIService {
    @POST("Test Notifica")
    fun sendNotification(@Header("Authorization") token: String, @Body notificationData: Notification): Call<ResponseBody>

    @POST("Test Notifica")
    fun sendNotification(@Body notificationData: Notification): Call<ResponseBody>
}