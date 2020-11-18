package com.github.viktorpenelski

interface DonationRepository {
    fun getById(id: Long): Donation?
    fun save(donation: Donation)
}

data class Donation(
    val _id: String,
    val amount: Double,
    val currency: String,
    val sender: String,
    val message: String,
    val id: Long? = null,
    val tag: String? = null
)