package com.github.viktorpenelski

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
    val donationRepository = DonationRepositoryJdbc()

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