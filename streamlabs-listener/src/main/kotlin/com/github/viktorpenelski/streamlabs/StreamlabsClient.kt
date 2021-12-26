package com.github.viktorpenelski.streamlabs

import com.github.viktorpenelski.Donation
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class StreamlabsConfig(val key: String = System.getenv("STREAMLABS_SOCKET_KEY"))

class StreamlabsClient @ExperimentalCoroutinesApi constructor(
    private val channel: BroadcastChannel<DonationMessage>,
    private val config: StreamlabsConfig
) {

    private val gson = Gson()
    private val socket: Socket = IO.socket("https://sockets.streamlabs.com?token=${config.key}")

    @ExperimentalCoroutinesApi
    suspend fun connect(): Unit = coroutineScope {
        socket.on(Socket.EVENT_CONNECT) {
            println("connected, wohooo great success!!")
        }

        socket.on("event") {
            val event = gson.fromJson(it[0].toString(), StreamlabsTypedEvent::class.java)
            when (event.type) {
                //TODO - figure out a nicer way to bridge socket.on callbacks with coroutines
                "donation" -> GlobalScope.launch {
                    donation(it[0].toString())
                }
                "follow" -> println(it[0].toString())
                else -> {
                }
            }
        }
        socket.on(Socket.EVENT_DISCONNECT) {
            println("disconnected : (")
        }
        socket.connect()
    }

    @ExperimentalCoroutinesApi
    private suspend fun donation(msg: String) {
        val streamlabsEvent = gson.fromJson(msg, StreamlabsEvent::class.java)
        val donationMessage = gson.fromJson(streamlabsEvent.message[0], DonationMessage::class.java)
        channel.send(donationMessage)
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
    val amount: Double, //TODO this should probably not be double, find a better way to test the socket API
    val isTest: Boolean,
    val name: String,
    val currency: String,
    val from: String,
    val to: JsonObject,
    val _id: String,
    val message: String,
    val formatted_amount: String
): StreamDonation {

    override fun message() = this.message

    override fun toDomain(tag: String?): Donation {
        return Donation(
            this._id,
            this.amount,
            this.currency,
            this.from, //TODO check if this is the sender
            this.message,
            tag = tag,
            date_created = LocalDateTime.now()
        )
    }

}


