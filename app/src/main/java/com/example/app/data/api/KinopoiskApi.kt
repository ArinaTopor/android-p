package com.example.app.data.api

import com.example.app.data.api.dto.MovieResponseDto
import com.example.app.data.api.dto.MoviesListResponseDto
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface KinopoiskApi {
    
    @GET("v1.4/movie")
    suspend fun getMovies(
        @Header("X-API-KEY") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 15,
        @Query("lists") top250: String = "top250"
    ): MoviesListResponseDto
    
    @GET("v1.4/movie/{id}")
    suspend fun getMovieById(
        @Header("X-API-KEY") apiKey: String,
        @Path("id") id: Int
    ): MovieResponseDto
    
    @GET("v1.4/movie/search")
    suspend fun searchMovies(
        @Header("X-API-KEY") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): MoviesListResponseDto
}