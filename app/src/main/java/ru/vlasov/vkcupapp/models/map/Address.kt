package ru.vlasov.vkcupapp.models.map


data class Address (
        val houseNumber: String,
        val road: String,
        val cityDistrict: String,
        val city: String,
        val county: String,
        val state: String,
        val region: String,
        val postcode: String,
        val country: String,
        val countryCode: String
        )