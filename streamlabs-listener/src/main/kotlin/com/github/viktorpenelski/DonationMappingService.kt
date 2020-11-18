package com.github.viktorpenelski

import com.github.viktorpenelski.streamlabs.DonationMessage

fun processDonation(donationMessage: DonationMessage, repo: DonationRepository) {
    val tag = getFirstHastTag(donationMessage.message)
    val donation = donationMessage.toDomain(tag)
    repo.save(donation)
    println("saving message with tag: $tag")
}

fun getFirstHastTag(msg: String): String? {
    return "#\\S*".toRegex()
        .find(msg)?.value
}

