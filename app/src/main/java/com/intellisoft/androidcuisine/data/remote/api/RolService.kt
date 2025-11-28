package com.intellisoft.androidcuisine.data.remote.api

import com.intellisoft.androidcuisine.data.remote.dto.ApiResponse
import com.intellisoft.androidcuisine.data.remote.dto.RolDto
import retrofit2.Response
import retrofit2.http.GET

interface RolService {
    @GET("roles")
    suspend fun getRoles(): Response<ApiResponse<List<RolDto>>>
}