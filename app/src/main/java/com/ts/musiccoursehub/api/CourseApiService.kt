package com.ts.musiccoursehub.api


import com.ts.musiccoursehub.data.CourseResponse
import retrofit2.Call
import retrofit2.http.GET

interface CourseApiService {
    @GET("3u3sDEM")
    fun getCourses(): Call<CourseResponse>
}
