package com.github.viktorpenelski.streamlabs

import com.github.viktorpenelski.Donation
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import java.time.LocalDateTime

data class StreamElementsConfig(val key: String = System.getenv("STREAMELEMENTS_JWT"))

class StreamElementsClient @ExperimentalCoroutinesApi constructor(
    private val channel: BroadcastChannel<StreamDonation>?,
    private val config: StreamElementsConfig
) {

    private val gson = Gson()
    private val opts = IO.Options().run {
        this.transports = arrayOf("websocket")
        this
    }
    private val socket: Socket = IO.socket("https://realtime.streamelements.com", opts)

    @ExperimentalCoroutinesApi
    suspend fun connect(): Unit = coroutineScope {
        val mapToken = mapOf(
            "method" to "jwt",
            "token" to config.key
        )
        socket.on(Socket.EVENT_CONNECT) {
            println("[connected] wohooo great success")
            socket.emit("authenticate", mapToken)
            println("just sent JWT")
//   //socket.emit('authenticate', {method: 'jwt', token: jwt});
        }

        socket.on("authenticated") {
            println("[authenticated] Successfully connected to ${it[0]}")
        }
        socket.on("unauthorized") {
            println("[unauthorized] reason: ${it[0]}}")
        }

        socket.on("event") {
            println("[event]: ${it[0]}")
            val event = gson.fromJson(it[0].toString(), StreamElementsEvent::class.java)
            when (event.type) {
                "tip" -> {
                    GlobalScope.launch {
                        val tip = gson.fromJson(event.data, StreamElementsTip::class.java)
                        channel?.send(tip)
                    }
                }
                else -> { // NOOP }
                }
            }
        }
        socket.on("event:update") {
            println("[event:update] ${it[0]}")
        }
        socket.on("event:reset") {
            println("[event:reset] ${it[0]}")
        }
        socket.on("event:test") {
            println("[event:test] ${it[0]}")
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            println("[event:disconnect] :(")
        }
        socket.connect()
    }
}

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val config = StreamElementsConfig()
    val client = StreamElementsClient(null, config)
    client.connect()
}

data class StreamElementsEvent(
    val createdAt: String,
    val updatedAt: String,
    val activityId: String,
    val data: JsonObject,
    val provider: String, // twitch
    val channel: String,
    val _id: String,
    val type: String // tip
)

//data={
// tipId=61c8cabd5f964d5e0b5cdf60,
// amount=321.0, currency=USD,
// avatar=https://cdn.streamelements.com/static/default-avatar.png,
// message=asdw,
// username=asd }
data class StreamElementsTip(
    val tipId: String,
    val amount: Double,
    val currency: String,
    val avatar: String,
    val message: String,
    val username: String
): StreamDonation {

    override fun message() = this.message
    override fun toDomain(tag: String?): Donation {
        return Donation(
            _id = this.tipId,
            amount = this.amount,
            currency = this.currency,
            sender = this.username,
            message = this.message,
            tag = tag,
            date_created = LocalDateTime.now()
        )
    }
}
