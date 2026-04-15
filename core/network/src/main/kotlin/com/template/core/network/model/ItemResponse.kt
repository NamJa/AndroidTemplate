package com.template.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemResponse(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("image_url") val imageUrl: String,
)
