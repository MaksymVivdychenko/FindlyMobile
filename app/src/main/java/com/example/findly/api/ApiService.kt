package com.example.findly.api

import com.example.findly.model.*
import retrofit2.http.*

interface ApiService {

    // --- User Controller ---

    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): MessageResponse

    // --- Catalog Controller ---

    @GET("catalog/books")
    suspend fun getBooks(
        @Query("Title") title: String? = null,
        @Query("Author") author: String? = null,
        @Query("PublisherId") publisherId: String? = null,
        @Query("CoverId") coverId: String? = null,
        @Query("PageNumber") pageNumber: Int = 1,
        @Query("PageSize") pageSize: Int = 10,
        @Query("IsAvailable") isAvailable: Boolean = false
    ): List<Book>

    @GET("catalog/covers")
    suspend fun getCovers(): List<Cover>

    @GET("catalog/publishers")
    suspend fun getPublishers(): List<Publisher>

    // --- Offer Controller ---

    // api/books/{bookId}/offers
    @GET("books/{bookId}/offers")
    suspend fun getOffersByBookId(@Path("bookId") bookId: String): List<Offer>

    // --- Favorites Controller ---

    @GET("favorites")
    suspend fun getFavorites(): List<LikedOffer>

    @POST("favorites/{offerId}")
    suspend fun addToFavorites(@Path("offerId") offerId: String): MessageResponse

    @DELETE("favorites/{offerId}")
    suspend fun removeFromFavorites(@Path("offerId") offerId: String): MessageResponse

    @PATCH("favorites/add-price")
    suspend fun addPriceAlert(@Body request: AddPriceRequest): MessageResponse

    @PATCH("favorites/remove-price/{offerId}")
    suspend fun removePriceAlert(@Path("offerId") offerId: String): MessageResponse
}