package com.github.viktorpenelski.streamlabs

import com.github.viktorpenelski.Donation

interface StreamDonation {

    fun message(): String
    fun toDomain(tag: String?): Donation
}