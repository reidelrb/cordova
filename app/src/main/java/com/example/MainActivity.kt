package com.example.miniserver

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fi.iki.elonen.NanoHTTPD
import java.io.InputStream

class MainActivity : AppCompatActivity() {
    private val PORT = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        object : NanoHTTPD("127.0.0.1", PORT) {
            override fun serve(session: IHTTPSession): Response {
                val path = if (session.uri == "/") "/index.html" else session.uri
                return try {
                    val inputStream = assets.open("web\$path")
                    newFixedLengthResponse(
                        Response.Status.OK,
                        getMimeType(path),
                        inputStream,
                        inputStream.available().toLong()
                    )
                } catch (e: Exception) {
                    newFixedLengthResponse("404 Not Found")
                }
            }
        }.start()

        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://127.0.0.1:\$PORT")))
    }

    private fun getMimeType(path: String): String = when {
        path.endsWith(".html") -> "text/html"
        path.endsWith(".js") -> "application/javascript"
        path.endsWith(".css") -> "text/css"
        path.endsWith(".png") -> "image/png"
        else -> "text/plain"
    }
}
