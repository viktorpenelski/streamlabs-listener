package com.github.viktorpenelski

import com.github.viktorpenelski.streamlabs.DonationMessage
import com.github.viktorpenelski.streamlabs.StreamDonation
import com.github.viktorpenelski.streamlabs.StreamElementsTip

fun processDonation(donationMessage: DonationMessage, repo: DonationRepository) {
    val tag = getFirstHastTag(donationMessage.message)
    val donation = donationMessage.toDomain(tag)
    repo.save(donation)
    println("saving message with tag: $tag")
}

fun processTip(tip: StreamDonation, repo: DonationRepository) {
    val tag = getFirstHastTag(tip.message())
    val donation = tip.toDomain(tag)
    repo.save(donation)
    println("saving $donation")
}

fun getFirstHastTag(msg: String): String? {
    return "#\\S*".toRegex()
        .find(msg)?.value
}

