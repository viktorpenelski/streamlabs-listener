package com.github.viktorpenelski

import java.sql.Connection
import java.sql.DriverManager


data class DonationEntity(
    val _id: String,
    val amount: Double,
    val currency: String,
    val sender: String,
    val message: String,
    val id: Long? = null,
    val tag: String? = null
)

interface DonationRepository {
    fun save(donation: DonationMessage, tag: String?) //TODO add domain entity with tag
}

private fun DonationMessage.toEntity(tag: String?): DonationEntity {
    return DonationEntity(
        this._id,
        this.amount,
        this.currency,
        this.from, //TODO check if this is the sender
        this.message,
        tag = tag
    )
}

class DonationRepositoryJdbc : DonationRepository {

    override fun save(donationMessage: DonationMessage, tag: String?) {
        val donation = donationMessage.toEntity(tag)
        getConnection().use {
            val statement = it.prepareStatement("""
                INSERT INTO DONATIONS(_id, amount, currency, sender, message, tag)
                    VALUES (?,?,?,?,?,?)
            """.trimIndent())
            statement.setString(1, donation._id)
            statement.setFloat(2, donation.amount.toFloat())
            statement.setString(3, donation.currency)
            statement.setString(4, donation.sender)
            statement.setString(5, donation.message)
            statement.setString(6, donation.tag)

            statement.execute()
        }
    }

    fun recreateSchema() {
        getConnection().use {
            val statement = it.createStatement()
            statement.queryTimeout = 30 // set timeout to 30 sec.
            statement.executeUpdate("drop table if exists DONATIONS")
            statement.executeUpdate(
                """create table DONATIONS (
                    id integer PRIMARY KEY NOT NULL, 
                    _id string NOT NULL, 
                    amount float NOT NULL, 
                    currency string NOT NULL,
                    sender string NOT NULL, 
                    message string NOT NULL, 
                    tag string
            )
        """.trimIndent()
            )
        }


    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:sqlite:sample.db")
    }
}

fun main() {
    val donationRepositoryJdbc = DonationRepositoryJdbc()
    donationRepositoryJdbc.recreateSchema()
}