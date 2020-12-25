package com.github.viktorpenelski.integrationtests

import com.github.viktorpenelski.Donation
import com.github.viktorpenelski.repo.DonationRepositoryJdbc
import com.github.viktorpenelski.repo.JdbcConfig
import com.github.viktorpenelski.repo.donationsTableName
import com.github.viktorpenelski.repo.recreateSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.DriverManager
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
internal class DonationRepositoryPostgreSQLTest {

    @Container
    private val container = PostgreSQLContainer<Nothing>("postgres:13.1-alpine").apply {
        withUsername("postgres")
        withPassword("postgres")
        withDatabaseName("streamlabs")
        start()
        println("Started PostgreSQL container at [$jdbcUrl]")
    }

    private val config = JdbcConfig(container.jdbcUrl, Properties().apply {
        setProperty("user", container.username)
        setProperty("password", container.password)
    })
    private val repo = DonationRepositoryJdbc(config)

    @BeforeEach
    fun setup() {
        recreateSchema(config)
    }

    @Test
    fun getById() {
        val donation = Donation(
            "some ext id",
            15.5,
            "BGN",
            "digggi",
            "Some big important message",
            date_created = LocalDateTime.now()
        )
        repo.save(donation)
        val actual = repo.getById(1)
        Assertions.assertNotNull(actual)

        // compare date_created separately as it loses some precision
        Assertions.assertEquals(donation.copy(id = 1, date_created = actual!!.date_created), actual)
        Assertions.assertEquals(
            donation.date_created.truncatedTo(ChronoUnit.MILLIS),
            actual.date_created.truncatedTo(ChronoUnit.MILLIS)
        )
    }

    @Test
    fun foo() {

        val donation = Donation(
            "some ext id",
            15.5,
            "BGN",
            "digggi",
            "Some big important message",
            date_created = LocalDateTime.now()
        )
        repo.save(donation)
        repo.save(donation.copy(_id = "some external id 2"))
        repo.save(donation.copy(_id = "some external id 3"))

        DriverManager.getConnection(container.jdbcUrl, container.username, container.password).use { conn ->
            conn.createStatement().use { stmt ->
                stmt.executeQuery("SELECT count(*) as donations from $donationsTableName").use { result ->
                    result.next()
                    Assertions.assertEquals(3, result.getInt("donations"))
                }
            }
        }
    }

}