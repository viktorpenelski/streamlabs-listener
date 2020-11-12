package com.github.viktorpenelski.repo

import com.github.viktorpenelski.Donation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class DonationRepositoryJdbcTest {

    // some platforms do not end with trailing slash - https://www.rgagnon.com/javadetails/java-0484.html
    private val tmpDir = System.getProperty("java.io.tmpdir").let {
        if (it.endsWith(File.separator)) {
            it
        } else {
            it + File.separator
        }
    }
    private val repo = DonationRepositoryJdbc(JdbcConfig("jdbc:sqlite:${tmpDir}test-tmp.db"))

    @BeforeEach
    fun setup() {
        repo.recreateSchema()
    }

    @Test
    fun getById() {
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

}