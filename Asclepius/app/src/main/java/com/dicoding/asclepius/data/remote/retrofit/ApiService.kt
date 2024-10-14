package com.dicoding.asclepius.data.remote.retrofit

import retrofit2.http.GET

interface ApiService {
    @GET("")
    fun getCancerNews()
}