package unpsjb.ing.tntpm2024.turnos

import android.content.Context
import android.util.Log
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object FcmSender {

    private const val PROJECT_ID = "appencuestas-4d4b0"
    private const val FCM_URL = "https://fcm.googleapis.com/v1/projects/$PROJECT_ID/messages:send"
    private const val SCOPE = "https://www.googleapis.com/auth/firebase.messaging"

    suspend fun enviarNotificacion(
        context: Context,
        targetToken: String,
        titulo: String,
        cuerpo: String
    ): Boolean = withContext(Dispatchers.IO) {
        if (targetToken.isBlank()) {
            Log.w("FcmSender", "El token del voluntario está vacío.")
            return@withContext false
        }

        try {
            // 1. Lee el service-account.json de assets y genera el token OAuth2 dinámicamente
            val assetStream = context.assets.open("service-account.json")
            val googleCredentials = GoogleCredentials.fromStream(assetStream)
                .createScoped(listOf(SCOPE))
            googleCredentials.refreshIfExpired()
            val accessToken = googleCredentials.accessToken.tokenValue

            // 2. Payload HTTP v1
            val jsonBody = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", targetToken)
                    put("notification", JSONObject().apply {
                        put("title", titulo)
                        put("body", cuerpo)
                    })
                    put("data", JSONObject().apply {
                        put("title", titulo)
                        put("body", cuerpo)
                    })
                })
            }

            // 3. Petición POST con Authorization: Bearer <accessToken>
            val url = URL(FCM_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Authorization", "Bearer $accessToken")
                setRequestProperty("Content-Type", "application/json; UTF-8")
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            val responseStream = if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream
            } else {
                connection.errorStream
            }
            val responseText = responseStream.bufferedReader().use { it.readText() }
            connection.disconnect()

            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.i("FcmSender", "Notificación v1 enviada con éxito: $responseText")
                true
            } else {
                Log.e("FcmSender", "Error al enviar notificación ($responseCode): $responseText")
                false
            }
        } catch (e: Exception) {
            Log.e("FcmSender", "Excepción al enviar FCM", e)
            false
        }
    }
}