package com.github.viktorpenelski.repo

import com.github.viktorpenelski.Donation
import com.github.viktorpenelski.DonationRepository
import org.sqlite.SQLiteConfig
import java.sql.Connection
import java.sql.DriverManager

data class JdbcConfig(
    val connectionString: String = System.getenv("STREAMLABS_JDBC_CONNECTION"),
    val sqLiteConfig: SQLiteConfig = SQLiteConfig()
) {
    fun getConnection(): Connection {
        return DriverManager.getConnection(connectionString, sqLiteConfig.toProperties())
    }
}

class DonationRepositoryJdbc(private val config: JdbcConfig) : DonationRepository {

    private val dbName = "DONATIONS"

    override fun getById(id: Long): Donation? {
        getConnection().use { conn ->
            conn.prepareStatement(
                """
                    SELECT _id, amount, currency, sender, message, id, tag
                    FROM $dbName
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
                        rs.getString("tag"))
                } else {
                    null
                }
            }
        }
    }

    override fun save(donation: Donation) {
        getConnection().use { conn ->
            conn.prepareStatement(
                """
                INSERT INTO $dbName(_id, amount, currency, sender, message, tag)
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

    fun recreateSchema() {
        getConnection().use { conn ->
            conn.createStatement().use { statement ->
                statement.queryTimeout = 30 // set timeout to 30 sec.
                statement.executeUpdate("drop table if exists $dbName")
                statement.executeUpdate(
                    """create table $dbName (
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

    }

    private fun getConnection(): Connection {
        return DriverManager.getConnection(config.connectionString, config.sqLiteConfig.toProperties())
    }
}

fun main() {
    val donationRepositoryJdbc = DonationRepositoryJdbc(JdbcConfig("jdbc:sqlite:test-tmp.db"))
    donationRepositoryJdbc.recreateSchema()
    donationRepositoryJdbc.save(Donation("some ext id",
        15.5,
        "BGN",
        "digggi",
        "Some big important message"
    ))
    println(donationRepositoryJdbc.getById(1))
}