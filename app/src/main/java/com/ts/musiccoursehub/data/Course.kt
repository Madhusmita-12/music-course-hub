package com.ts.musiccoursehub.data

import com.google.gson.annotations.SerializedName

data class CourseResponse(
    val result: ResultResponse
)

data class ResultResponse(
    val index: List<Course>,
    val collections: CollectionsResponse
)

data class CollectionsResponse(
    val smart: List<SmartCollection>,
    val user: List<UserCollection>,
    val curated: List<Any>
)

data class SmartCollection(
    val id: String,
    val label: String,
    val smart: String,
    val courses: List<Int>,
    @SerializedName("is_default")
    val isDefault: Int,
    @SerializedName("is_archive")
    val isArchive: Int,
    val description: String
)

data class UserCollection(
    val id: Int,
    val label: String,
    @SerializedName("is_default")
    val isDefault: Int,
    @SerializedName("is_archive")
    val isArchive: Int,
    val description: String,
    val courses: List<Int>
)


data class Course(
    @SerializedName("downloadid")
    val downloadId: Int,
    @SerializedName("cd_downloads")
    val cdDownloads: Int,
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String, // Changed from 'name' to 'title' to match JSON
    val status: Int,
    @SerializedName("release_date")
    val releaseDate: String?, // Can be String or parse to Date later
    @SerializedName("authorid")
    val authorId: Int,
    @SerializedName("video_count")
    val videoCount: Int,
    @SerializedName("style_tags")
    val style: List<String>?, // Keep 'style' to match existing filter logic, but using 'styleTags' from JSON
    @SerializedName("skill_tags")
    val skill: List<String>?, // Keep 'skill' to match existing filter logic, but using 'skillTags' from JSON
    @SerializedName("series_tags")
    val series: List<String>?, // Keep 'series' to match existing filter logic, but using 'seriesTags' from JSON
    @SerializedName("curriculum_tags")
    val curriculum: List<String>?, // Keep 'curriculum' to match existing filter logic, but using 'curriculumTags' from JSON
    val educator: String?, // Keep 'educator'
    val owned: Int?, // Keep 'owned' as Int? and handle 1/0 to Boolean conversion in Adapter/Activity
    val sale: Int?,
    @SerializedName("purchase_order")
    val purchaseOrder: Any?, // Keep 'purchaseOrder' as Any? for now as type is inconsistent
    val watched: Int?,
    @SerializedName("progress_tracking")
    val progressTracking: Double?
)