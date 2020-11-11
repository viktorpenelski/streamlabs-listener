package com.github.viktorpenelski

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class DonationMappingServiceTest {

    @Test
    fun `two hashtags first is found`() {
        val firstHashtag = getFirstHastTag("Това е съобщение с два #hashtag-a #second")
        Assertions.assertEquals("#hashtag-a", firstHashtag)

    }

    @Test
    fun `zero hashtags returns null`() {
        val firstHashtag = getFirstHastTag("Това е съобщение няма hashtag-ове")
        Assertions.assertNull(firstHashtag)

    }
}