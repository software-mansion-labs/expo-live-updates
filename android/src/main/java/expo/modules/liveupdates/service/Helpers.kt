import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

suspend fun resolveImage(context: Context, string: String): String = withContext(Dispatchers.IO) {
    try {
        val url = URL(string)
        if (url.protocol.startsWith("http")) {
            // Shared container (analogicznie do App Group w iOS, np. folder cache)
            val container = context.getSharedPreferences("sharedData", Context.MODE_PRIVATE).let {
                context.cacheDir // lub context.filesDir
            }

            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext string
            }

            val data = connection.inputStream.readBytes()
            val filename = "${UUID.randomUUID()}.png"
            val file = File(container, filename)
            file.writeBytes(data)

            return@withContext file.name
        } else {
            return@withContext string
        }
    } catch (e: Exception) {
        return@withContext string
    }
}