package com.github.viktorpenelski

fun processDonation(donationMessage: DonationMessage) {
    println(getFirstHastTag(donationMessage.message))
}

fun getFirstHastTag(msg: String): String? {
    return "#\\S*".toRegex()
        .find(msg)?.value
}

