package com.github.viktorpenelski

import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket

data class StreamlabsConfig(val key: String = System.getenv("STREAMLABS_SOCKET_KEY"))

class StreamlabsClient(private val config: StreamlabsConfig) {

    init {
        val gson = Gson()
        val socket = IO.socket("https://sockets.streamlabs.com?token=${config.key}")
        socket.on(Socket.EVENT_CONNECT) {
            println("connected, wohooo great success!!")
        }
        socket.on("event") {
            println(it[0])
            val event = gson.fromJson(it[0].toString(), StreamlabsTypedEvent::class.java)
            when (event.type) {
                "alertPlaying" -> {
                }
                "donation" -> {
                    val streamlabsEvent = gson.fromJson(it[0].toString(), StreamlabsEvent::class.java)
                    println(gson.fromJson(streamlabsEvent.message[0], DonationMessage::class.java))
                }
                else -> (println("event: ${it[0]}"))
            }

        }
        socket.on(Socket.EVENT_DISCONNECT) {
            println("disconnected : (")
        }
        socket.connect()
    }

}

data class StreamlabsTypedEvent(
    val type: String
)

data class StreamlabsEvent(
    val event_id: String,
    val `for`: String,
    val type: String,
    val message: List<JsonObject>
)

data class DonationMessage(
    val amount: Double,
    val isTest: Boolean,
    val name: String,
    val currency: String,
    val from: String,
    val to: JsonObject,
    val _id: String,
    val message: String,
    val formatted_amount: String
)


fun main() {
    val client = StreamlabsClient(StreamlabsConfig())
}
