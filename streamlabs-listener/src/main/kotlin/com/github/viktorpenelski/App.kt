package com.github.viktorpenelski

import com.github.viktorpenelski.arduino.Arduino
import com.github.viktorpenelski.arduino.RGBAnimations
import com.github.viktorpenelski.repo.DonationRepositoryJdbc
import com.github.viktorpenelski.repo.JdbcConfig
import com.github.viktorpenelski.streamlabs.*
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import java.util.*

@ExperimentalCoroutinesApi
fun main() = runBlocking {
//    val channel = BroadcastChannel<DonationMessage>(9999)
//    val client = StreamlabsClient(channel, StreamlabsConfig())

    val channel = BroadcastChannel<StreamDonation>(9999)
    val client = StreamElementsClient(channel, StreamElementsConfig())
    val dotenv = initializeDotEnv()
    val donationRepository = DonationRepositoryJdbc(initializeJdbcConfig(dotenv))
    val arduino = Arduino()

    client.connect()
    launch {
        logDonations(channel.openSubscription())
    }
    withContext(Dispatchers.Default) {
        launch {
            sendDonationsToMapper(channel.openSubscription(), donationRepository, ::processTip)
        }
        launch {
            controlRGB(arduino, channel.openSubscription())
        }
    }

    println()
}

fun initializeDotEnv(): Dotenv {
    return dotenv {
        filename = System.getenv("ENV_FILE")
    }
}

fun initializeJdbcConfig(dotenv: Dotenv) =
    JdbcConfig("jdbc:postgresql://localhost:5432/${dotenv["POSTGRES_DB"]}",
        Properties().apply {
            setProperty("user", dotenv["POSTGRES_USER"])
            setProperty("password", dotenv["POSTGRES_PASSWORD"])
        })

suspend fun sendDonationsToMapper(
    channel: ReceiveChannel<StreamDonation>,
    repo: DonationRepository,
    service: (StreamDonation, DonationRepository) -> Unit
) = coroutineScope {
    launch {
        channel.consumeEach {
            service.invoke(it, repo)
        }
    }
}

suspend fun logDonations(channel: ReceiveChannel<StreamDonation>) = coroutineScope {
    launch {
        channel.consumeEach {
            println(it)
        }
    }
}

suspend fun controlRGB(arduino: Arduino, channel: ReceiveChannel<StreamDonation>) = coroutineScope {
    launch {
        channel.consumeEach {
            println("attempting to send to arduino")
            arduino.send(RGBAnimations.THEATER_RAINBOW_CHASE)
            launch {
                val delay = 5000L
                println("delaying for $delay")
                delay(delay)
                arduino.send(RGBAnimations.RAINBOW_CYCLE)
            }
        }
    }
}