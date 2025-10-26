package com.example.app.data.repository

import com.example.app.data.Movie
import com.example.app.data.api.KinopoiskApi
import com.example.app.data.mapper.MovieMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val api: KinopoiskApi
) {
    
    private val apiKey = "QXR09VY-4SKMXXZ-MBXEQXM-XEA04QH"
    
    // Простое кэширование в памяти
    private var cachedMovies: List<Movie>? = null
    private var cachedMovieDetails: MutableMap<Int, Movie> = mutableMapOf()
    
    suspend fun getMovies(page: Int = 1, limit: Int = 25): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getMovies(
                    apiKey = apiKey,
                    page = page,
                    limit = limit
                )
                val movies = response.docs.map { MovieMapper.mapToDomain(it) }
                cachedMovies = movies
                Result.success(movies)
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка загрузки фильмов: ${e.message}"))
            }
        }
    }
    
    fun getMoviesFlow(page: Int = 1, limit: Int = 10): Flow<Result<List<Movie>>> = flow {
        emit(Result.success(cachedMovies ?: emptyList()))
        
        try {
            val response = api.getMovies(
                apiKey = apiKey,
                page = page,
                limit = limit
            )
            val movies = response.docs.map { MovieMapper.mapToDomain(it) }
            cachedMovies = movies
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Ошибка загрузки фильмов: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun getMovieById(id: Int): Result<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                // Проверяем кэш
                cachedMovieDetails[id]?.let { cachedMovie ->
                    return@withContext Result.success(cachedMovie)
                }
                
                val response = api.getMovieById(
                    apiKey = apiKey,
                    id = id
                )
                val movie = MovieMapper.mapToDomain(response)
                cachedMovieDetails[id] = movie
                Result.success(movie)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    fun getMovieByIdFlow(id: Int): Flow<Result<Movie>> = flow {
        // Сначала эмитим кэшированное значение, если есть
        cachedMovieDetails[id]?.let { cachedMovie ->
            emit(Result.success(cachedMovie))
        }
        
        try {
            val response = api.getMovieById(
                apiKey = apiKey,
                id = id
            )
            val movie = MovieMapper.mapToDomain(response)
            cachedMovieDetails[id] = movie
            emit(Result.success(movie))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    suspend fun searchMovies(query: String, page: Int = 1, limit: Int = 10): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.searchMovies(
                    apiKey = apiKey,
                    query = query,
                    page = page,
                    limit = limit
                )
                val movies = response.docs.map { MovieMapper.mapToDomain(it) }
                Result.success(movies)
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка поиска: ${e.message}"))
            }
        }
    }
    
    fun searchMoviesFlow(query: String, page: Int = 1, limit: Int = 10): Flow<Result<List<Movie>>> = flow {
        try {
            val response = api.searchMovies(
                apiKey = apiKey,
                query = query,
                page = page,
                limit = limit
            )
            val movies = response.docs.map { MovieMapper.mapToDomain(it) }
            emit(Result.success(movies))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Ошибка поиска: ${e.message}")))
        }
    }.flowOn(Dispatchers.IO)
}
