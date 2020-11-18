package com.github.viktorpenelski.repo

import com.github.viktorpenelski.Donation
import com.github.viktorpenelski.DonationRepository
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

const val donationsTableName = "DONATIONS"

data class JdbcConfig(
    val connectionString: String = System.getenv("STREAMLABS_JDBC_CONNECTION"),
    val props: Properties = Properties()
) {
    fun getConnection(): Connection {
        return DriverManager.getConnection(connectionString, props)
    }
}

class DonationRepositoryJdbc(private val config: JdbcConfig) : DonationRepository {

    override fun getById(id: Long): Donation? {
        config.getConnection().use { conn ->
            conn.prepareStatement(
                """
                    SELECT _id, amount, currency, sender, message, id, tag
                    FROM $donationsTableName
                    WHERE id = ?
            """.trimIndent()
            ).use { preparedStatement ->
                preparedStatement.setLong(1, id)
                val rs = preparedStatement.executeQuery()
                return if (rs.next()) {
                    Donation(
                        rs.getString("_id"),
                        rs.getFloat("amount").toDouble(),
                        rs.getString("currency"),
                        rs.getString("sender"),
                        rs.getString("message"),
                        rs.getLong("id"),
                        rs.getString("tag")
                    )
                } else {
                    null
                }
            }
        }
    }

    override fun save(donation: Donation) {
        config.getConnection().use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO $donationsTableName(_id, amount, currency, sender, message, tag)
                    VALUES (?,?,?,?,?,?)
            """.trimIndent()
            ).use { preparedStatement ->
                preparedStatement.setString(1, donation._id)
                preparedStatement.setFloat(2, donation.amount.toFloat())
                preparedStatement.setString(3, donation.currency)
                preparedStatement.setString(4, donation.sender)
                preparedStatement.setString(5, donation.message)
                preparedStatement.setString(6, donation.tag)

                preparedStatement.execute()
            }
        }
    }
}

