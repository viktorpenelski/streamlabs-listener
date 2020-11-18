package com.github.viktorpenelski

import com.github.viktorpenelski.repo.DonationRepositoryJdbc
import com.github.viktorpenelski.repo.JdbcConfig
import com.github.viktorpenelski.streamlabs.DonationMessage
import com.github.viktorpenelski.streamlabs.StreamlabsClient
import com.github.viktorpenelski.streamlabs.StreamlabsConfig
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val channel = BroadcastChannel<DonationMessage>(9999)
    val client = StreamlabsClient(channel, StreamlabsConfig())
    val dotenv = initializeDotEnv()
    val donationRepository = DonationRepositoryJdbc(initializeJdbcConfig(dotenv))

    client.connect()
    sendDonationsToMapper(channel.openSubscription(), donationRepository, ::processDonation)
    logDonations(channel.openSubscription())

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