package com.raida.intermediatesubmission

import com.raida.intermediatesubmission.model.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val story = ListStoryItem(
                "https://story-api.dicoding.dev/images/stories/photos-1683156953339_VwI5mJqN.jpg",
                "2023-05-03T23:35:53.340Z",
                "Abdan Zaki Alifian",
                "Halooo",
                0.0f,
                "story-S60OZqYL04DTnVFw",
                0.0f
            )
            items.add(story)
        }
        return items
    }

    fun dummyToken(): String = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLUt3aEpDRTRpMFNadUg3YlciLCJpYXQiOjE2ODMxNTc4Njd9.h77O5yhaTOczhwMLeg4TxexTgsDvUT0Et9xPw3TZ468"

}