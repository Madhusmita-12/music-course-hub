package com.ts.musiccoursehub.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ts.musiccoursehub.R
import com.ts.musiccoursehub.adapter.CourseAdapter
import com.ts.musiccoursehub.api.RetrofitClient
import com.ts.musiccoursehub.data.Course
import com.ts.musiccoursehub.data.CourseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var ownedCoursesRecyclerView: RecyclerView
    private lateinit var guitarLabCoursesRecyclerView: RecyclerView
    private lateinit var recentlyWatchedCoursesRecyclerView: RecyclerView
    private lateinit var favoritesCoursesRecyclerView: RecyclerView

    private lateinit var ownedCourseAdapter: CourseAdapter
    private lateinit var guitarLabCourseAdapter: CourseAdapter
    private lateinit var recentlyWatchedCourseAdapter: CourseAdapter
    private lateinit var favoritesCourseAdapter: CourseAdapter

    private lateinit var viewAllOwnedTextView: TextView
    private lateinit var viewAllGuitarLabTextView: TextView
    private lateinit var viewAllRecentlyWatchedTextView: TextView
    private lateinit var viewAllFavoritesTextView: TextView

    private var allCourses: List<Course> = emptyList() // Store all courses fetched from API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize RecyclerViews
        ownedCoursesRecyclerView = findViewById(R.id.ownedCoursesRecyclerView)
        guitarLabCoursesRecyclerView = findViewById(R.id.guitarLabCoursesRecyclerView)
        recentlyWatchedCoursesRecyclerView = findViewById(R.id.recentlyWatchedCoursesRecyclerView)
        favoritesCoursesRecyclerView = findViewById(R.id.favoritesCoursesRecyclerView)

        // Initialize "View All" TextViews
        viewAllOwnedTextView = findViewById(R.id.viewAllOwnedTextView)
        viewAllGuitarLabTextView = findViewById(R.id.viewAllGuitarLabTextView)
        viewAllRecentlyWatchedTextView = findViewById(R.id.viewAllRecentlyWatchedTextView)
        viewAllFavoritesTextView = findViewById(R.id.viewAllFavoritesTextView)

        // Set up LayoutManagers (Horizontal LinearLayoutManager for all sections)
        ownedCoursesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        guitarLabCoursesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        recentlyWatchedCoursesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        favoritesCoursesRecyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        // Initialize Adapters (Initially with empty lists)
        ownedCourseAdapter = CourseAdapter(emptyList())
        guitarLabCourseAdapter = CourseAdapter(emptyList())
        recentlyWatchedCourseAdapter = CourseAdapter(emptyList())
        favoritesCourseAdapter = CourseAdapter(emptyList())

        // Set Adapters to RecyclerViews
        ownedCoursesRecyclerView.adapter = ownedCourseAdapter
        guitarLabCoursesRecyclerView.adapter = guitarLabCourseAdapter
        recentlyWatchedCoursesRecyclerView.adapter = recentlyWatchedCourseAdapter
        favoritesCoursesRecyclerView.adapter = favoritesCourseAdapter

        // Set up "View All" Click Listeners
        setupViewAllClickListeners()

        // Fetch course data from API
        fetchCourses()
    }

    private fun setupViewAllClickListeners() {
        viewAllOwnedTextView.setOnClickListener {
            startViewAllActivity(ViewAllActivity.CATEGORY_OWNED, "Owned Courses")
        }
        viewAllGuitarLabTextView.setOnClickListener {
            startViewAllActivity(ViewAllActivity.CATEGORY_GUITAR_LAB, "Guitar Lab Courses")
        }
        viewAllRecentlyWatchedTextView.setOnClickListener {
            startViewAllActivity(ViewAllActivity.CATEGORY_ALL, "Recently Watched")
        }
        viewAllFavoritesTextView.setOnClickListener {
            startViewAllActivity(ViewAllActivity.CATEGORY_FAVORITES, "Favorites")
        }
    }


    private fun startViewAllActivity(category: String, viewAllTitle: String) {
        val intent = Intent(this, ViewAllActivity::class.java)
        intent.putExtra(ViewAllActivity.VIEW_ALL_CATEGORY_KEY, category) // Pass category using constant
        intent.putExtra(ViewAllActivity.VIEW_ALL_TITLE_KEY, viewAllTitle) // Pass the section title
        startActivity(intent)
    }


    private fun fetchCourses() {
        val call: Call<CourseResponse> = RetrofitClient.instance.getCourses()
        call.enqueue(object : Callback<CourseResponse> {
            override fun onResponse(call: Call<CourseResponse>, response: Response<CourseResponse>) {
                if (response.isSuccessful) {
                    val courseResponse = response.body()
                    allCourses = courseResponse?.result?.index ?: emptyList() // Get all courses


                    val ownedCourses = allCourses.filterIndexed { index, _ -> index < 5 }
                        .map { it.copy(owned = 1) }
                    val guitarLabCourses = allCourses.filterIndexed { index, _ -> index in 5..9 }
                    val recentlyWatchedCourses = allCourses.filterIndexed { index, _ -> index in 10..12 }
                    val favoritesCourses = allCourses.filterIndexed { index, _ -> index in 13..15 }

                    // Update Adapters with data for each section
                    ownedCourseAdapter.updateData(ownedCourses)
                    guitarLabCourseAdapter.updateData(guitarLabCourses)
                    recentlyWatchedCourseAdapter.updateData(recentlyWatchedCourses)
                    favoritesCourseAdapter.updateData(favoritesCourses)


                } else {
                    Log.e("MainActivity", "Error fetching courses: ${response.code()}")
                    // Handle error case (e.g., show error message to user)
                }
            }

            override fun onFailure(call: Call<CourseResponse>, t: Throwable) {
                Log.e("MainActivity", "Network error: ${t.message}", t)
                // Handle network error (e.g., show no internet message)
            }
        })
    }
}