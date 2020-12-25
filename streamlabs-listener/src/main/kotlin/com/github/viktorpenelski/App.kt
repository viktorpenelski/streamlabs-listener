package com.github.viktorpenelski

import com.github.viktorpenelski.arduino.Arduino
import com.github.viktorpenelski.arduino.RGBAnimations
import com.github.viktorpenelski.repo.DonationRepositoryJdbc
import com.github.viktorpenelski.repo.JdbcConfig
import com.github.viktorpenelski.streamlabs.DonationMessage
import com.github.viktorpenelski.streamlabs.StreamlabsClient
import com.github.viktorpenelski.streamlabs.StreamlabsConfig
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import java.util.*

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val channel = BroadcastChannel<DonationMessage>(9999)
    val client = StreamlabsClient(channel, StreamlabsConfig())
    val dotenv = initializeDotEnv()
    val donationRepository = DonationRepositoryJdbc(initializeJdbcConfig(dotenv))
    val arduino = Arduino()

    client.connect()
    launch {
        logDonations(channel.openSubscription())
    }
    withContext(Dispatchers.Default) {
        launch {
            sendDonationsToMapper(channel.openSubscription(), donationRepository, ::processDonation)
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
    channel: ReceiveChannel<DonationMessage>,
    repo: DonationRepository,
    service: (DonationMessage, DonationRepository) -> Unit
) = coroutineScope {
    launch {
        channel.consumeEach {
            service.invoke(it, repo)
        }
    }
}

suspend fun logDonations(channel: ReceiveChannel<DonationMessage>) = coroutineScope {
    launch {
        channel.consumeEach {
            println(it)
        }
    }
}

suspend fun controlRGB(arduino: Arduino, channel: ReceiveChannel<DonationMessage>) = coroutineScope {
    launch {
        channel.consumeEach {
            println("attempting to send")
            arduino.send(RGBAnimations.THEATER_RAINBOW_CHASE)
            launch {
                delay(2000L)
                println("after delay")
                arduino.send(RGBAnimations.RAINBOW_CYCLE)
            }
        }
    }
}