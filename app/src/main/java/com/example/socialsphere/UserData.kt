package com.example.socialsphere

data class UserData(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val password: String? = null,
    val latitude: Double = 0.0, // Valoare implicită pentru latitudine
    val longitude: Double = 0.0 // Valoare implicită pentru longitudine
)
