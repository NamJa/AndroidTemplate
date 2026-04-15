package com.template.core.network.api

import com.template.core.network.model.ItemResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemApi {
    @GET("items")
    suspend fun getItems(): List<ItemResponse>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: Long): ItemResponse
}
