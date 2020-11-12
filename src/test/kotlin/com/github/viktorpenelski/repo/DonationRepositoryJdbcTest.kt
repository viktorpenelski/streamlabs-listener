package com.github.viktorpenelski.repo

import com.github.viktorpenelski.Donation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DonationRepositoryJdbcTest {

    private val tmpDir = System.getProperty("java.io.tmpdir")
    private val repo = DonationRepositoryJdbc(JdbcConfig("jdbc:sqlite:${tmpDir}test-tmp.db"))

    @BeforeEach
    fun setup() {
        repo.recreateSchema()
    }

    @Test
    fun getById() {
        println("tmp dir = $tmpDir")
        val donation = Donation(
            "some ext id",
            15.5,
            "BGN",
            "digggi",
            "Some big important message"
        )
        repo.save(donation)
        val actual = repo.getById(1)
        Assertions.assertNotNull(actual)
        Assertions.assertEquals(donation.copy(id = 1), actual)
    }

    @Test
    fun save() {

    }

}