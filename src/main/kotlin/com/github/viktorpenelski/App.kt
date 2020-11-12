package com.github.viktorpenelski

import com.github.viktorpenelski.repo.DonationRepositoryJdbc
import com.github.viktorpenelski.repo.JdbcConfig
import com.github.viktorpenelski.streamlabs.DonationMessage
import com.github.viktorpenelski.streamlabs.StreamlabsClient
import com.github.viktorpenelski.streamlabs.StreamlabsConfig
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@ExperimentalCoroutinesApi
fun main() = runBlocking {
    val channel = BroadcastChannel<DonationMessage>(9999)
    val client = StreamlabsClient(channel, StreamlabsConfig())
    val donationRepository = DonationRepositoryJdbc(JdbcConfig("jdbc:sqlite:sample.db"))

    client.connect()
    sendDonationsToMapper(channel.openSubscription(), donationRepository, ::processDonation)
    logDonations(channel.openSubscription())

    println()
}

suspend fun sendDonationsToMapper(channel: ReceiveChannel<DonationMessage>,
                                  repo: DonationRepository,
                                  service: (DonationMessage, DonationRepository) -> Unit) = coroutineScope {
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