package com.example.findly.model

import com.google.gson.annotations.SerializedName

// --- User & Auth ---

data class AuthResponse(
    val login: String,
    val token: String,
    val userId: String // Guid мапимо в String
)

data class LoginRequest(
    val login: String,
    val password: String,
    val deviceToken: String
)

data class RegisterRequest(
    val login: String,
    val password: String,
    val deviceToken: String
)

data class ChangePasswordRequest(
    val newPassword: String,
    val oldPassword: String
)

// --- Books ---

data class Book(
    val id: String,
    val title: String,
    val imageUrl: String?, // Може бути null
    val authors: List<String>,
    val publisher: String,
    val cover: String,
    val minPrice: Double?, // Decimal -> Double
    val maxPrice: Double?,
    val isAvailable: Boolean
)

data class Publisher(
    val id: String,
    val title: String
)

data class Cover(
    val id: String,
    val name: String
)

// --- Offers & Favorites ---

data class Offer(
    val id: String,
    val price: Double,
    val isAvailable: Boolean,
    val link: String,
    val shopName: String,
    val shopLogoUrl: String?,
    val isLiked: Boolean,
    val isPriceSet: Boolean
)

data class LikedOffer(
    val offerId: String,
    val bookTitle: String,
    val bookImageUrl: String?,
    val authors: List<String>,
    val shopName: String,
    val link: String,
    val currentPrice: Double,
    val isAvailable: Boolean,
    val isNotifySet: Boolean
)

data class AddPriceRequest(
    val offerId: String,
    val price: Double
)

data class MessageResponse(
    val message: String
)