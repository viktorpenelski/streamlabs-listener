package com.github.viktorpenelski

fun processDonation(donationMessage: DonationMessage, repo: DonationRepository) {
    val tag = getFirstHastTag(donationMessage.message)
    repo.save(donationMessage, tag)
    println("saving message with tag: $tag")
}

fun getFirstHastTag(msg: String): String? {
    return "#\\S*".toRegex()
        .find(msg)?.value
}

