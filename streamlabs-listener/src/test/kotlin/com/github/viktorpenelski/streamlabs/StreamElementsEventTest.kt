package com.github.viktorpenelski.streamlabs

import com.google.gson.Gson
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class StreamElementsEventTest {

    @Test
    fun `test event deserialization`() {
        val raw = """
            {
                "createdAt":"2021-12-26T20:13:46.944Z",
                "activityId":"61c8ccfb80c10b5b0e253ba8",
                "data":{
                    "tipId":"61c8ccfa41cbd861807063b6",
                    "amount": 1.23,
                    "currency":"EUR",
                    "avatar":"https://cdn.streamelements.com/static/default-avatar.png",
                    "message":"foo #bar",
                    "username":"digggi"
                },
                "provider":"twitch",
                "channel":"5e9c8fa761d9e043bf9527cb",
                "_id":"61c8ccfb80c10b5b0e253ba8",
                "type":"tip",
                "updatedAt":"2021-12-26T20:13:46.944Z"
            }
        """.trimIndent()
        val event = Gson().fromJson(raw, StreamElementsEvent::class.java)
        val donation = Gson().fromJson(event.data, StreamElementsTip::class.java)
        assertEquals("tip", event.type)
        assertEquals(1.23, donation.amount)
        assertEquals("EUR", donation.currency)
        assertEquals("foo #bar", donation.message)
        assertEquals("digggi", donation.username)
    }
}