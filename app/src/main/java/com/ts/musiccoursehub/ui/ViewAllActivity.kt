package com.ts.musiccoursehub.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ts.musiccoursehub.R
import com.ts.musiccoursehub.adapter.CourseAdapter
import com.ts.musiccoursehub.api.RetrofitClient
import com.ts.musiccoursehub.data.Course
import com.ts.musiccoursehub.data.CourseResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ViewAllActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var filterBar: LinearLayout
    private lateinit var ownedFilterButton: Button
    private lateinit var skillFilterButton: Button
    private lateinit var curriculumFilterButton: Button
    private lateinit var styleFilterButton: Button
    private lateinit var educatorFilterButton: Button
    private lateinit var seriesFilterButton: Button
    private lateinit var clearFilterButton: Button
    private lateinit var viewAllCoursesRecyclerView: RecyclerView
    private lateinit var courseAdapter: CourseAdapter
    private lateinit var noCoursesTextView: TextView
    private lateinit var viewAllTitleTextView: TextView

    private var allCourses: List<Course> = emptyList()
    private var filteredCourses: List<Course> = emptyList()
    private val selectedFilters: MutableMap<String, MutableSet<String>> = mutableMapOf()
    private var ownedFilterSelected: Boolean? = null

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val filterOptionsMap: MutableMap<String, List<String>> = mutableMapOf()

    private var currentCourseResponse: CourseResponse? = null

    companion object {
        const val VIEW_ALL_TITLE_KEY = "viewAllTitle"
        const val VIEW_ALL_CATEGORY_KEY = "viewAllCategory"
        const val CATEGORY_ALL = "all"
        const val CATEGORY_OWNED = "owned"
        const val CATEGORY_GUITAR_LAB = "guitar_lab"
        const val CATEGORY_FAVORITES = "favorites"
        const val CATEGORY_RECENTLY_WATCHED = "recentlyWatched"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_all)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        viewAllTitleTextView = findViewById(R.id.viewAllTitleTextView)

        // Retrieve category and title from Intent extras
        val category = intent.getStringExtra(VIEW_ALL_CATEGORY_KEY) ?: CATEGORY_ALL // Default to "all" if no category passed
        val titleFromIntent = intent.getStringExtra(VIEW_ALL_TITLE_KEY) ?: "All Courses"
        viewAllTitleTextView.text = titleFromIntent

        searchView = findViewById(R.id.searchView)
        filterBar = findViewById(R.id.filterBar)
        ownedFilterButton = findViewById(R.id.ownedFilterButton)
        skillFilterButton = findViewById(R.id.skillFilterButton)
        curriculumFilterButton = findViewById(R.id.curriculumFilterButton)
        styleFilterButton = findViewById(R.id.styleFilterButton)
        educatorFilterButton = findViewById(R.id.educatorFilterButton)
        seriesFilterButton = findViewById(R.id.seriesFilterButton)
        clearFilterButton = findViewById(R.id.clearFilterButton)
        viewAllCoursesRecyclerView = findViewById(R.id.viewAllCoursesRecyclerView)
        noCoursesTextView = findViewById(R.id.noCoursesTextView)

        viewAllCoursesRecyclerView.layoutManager = GridLayoutManager(this, 2)
        courseAdapter = CourseAdapter(emptyList())
        viewAllCoursesRecyclerView.adapter = courseAdapter

        bottomSheetDialog = BottomSheetDialog(this)

        fetchCoursesData(category) // Fetch data - pass category to fetchCoursesData
        setupSearchView()
        setupFilterButtons()
        setupClearFilterButton()
    }

    private fun fetchCoursesData(category: String) {
        val call: Call<CourseResponse> = RetrofitClient.instance.getCourses()
        call.enqueue(object : Callback<CourseResponse> {
            override fun onResponse(call: Call<CourseResponse>, response: Response<CourseResponse>) {
                if (response.isSuccessful) {
                    val courseResponse = response.body()
                    currentCourseResponse = courseResponse // Store CourseResponse in class-level variable
                    allCourses = courseResponse?.result?.index ?: emptyList()

                    filteredCourses = when (category) {
                        CATEGORY_OWNED -> {
                            val ownedCourses = allCourses.filter { it.owned == 1 }
                            Log.d("CategoryFilterDebug", "Category: $category, Count: ${ownedCourses.size}")
                            ownedCourses
                        }
                        CATEGORY_GUITAR_LAB -> {
                            val guitarLabCourses = allCourses.filter { course ->
                                val courseTitleLower = course.title.lowercase()
                                courseTitleLower.contains("guitar")
                            }
                            Log.d("CategoryFilterDebug", "Category: $category, Count: ${guitarLabCourses.size} (Title contains 'Guitar')")
                            guitarLabCourses
                        }
                        CATEGORY_FAVORITES -> {
                            val favoriteCollection = currentCourseResponse?.result?.collections?.smart?.find {
                                it.label.equals("Favorites", ignoreCase = true)
                            }
                            val favoriteCourseIds = favoriteCollection?.courses ?: emptyList()
                            val favoritesCourses = allCourses.filter { course ->
                                favoriteCourseIds.contains(course.id)
                            }
                            Log.d("CategoryFilterDebug", "Category: $category, Count: ${favoritesCourses.size} (SmartCollection Favorites)")
                            favoritesCourses
                        }
                        CATEGORY_RECENTLY_WATCHED -> {
                            val recentlyWatchedCourses = emptyList<Course>()
                            Log.d("CategoryFilterDebug", "Category: $category, Count: ${recentlyWatchedCourses.size}")
                            recentlyWatchedCourses
                        }
                        CATEGORY_ALL -> {
                            Log.d("CategoryFilterDebug", "Category: $category, Count: ${allCourses.size}")
                            allCourses
                        }
                        else -> {
                            Log.d("CategoryFilterDebug", "Category: $category (ELSE), Count: ${allCourses.size}")
                            allCourses
                        }
                    }

                    updateCourseList()
                    populateFilterOptions()
                } else {
                    Toast.makeText(this@ViewAllActivity, "Error fetching courses", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CourseResponse>, t: Throwable) {
                Toast.makeText(this@ViewAllActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCourseList() {
        Log.d("UIUpdateDebug", "updateCourseList: Start - filteredCourses size: ${filteredCourses.size}")
        if (filteredCourses.isEmpty()) {
            noCoursesTextView.visibility = View.VISIBLE
            viewAllCoursesRecyclerView.visibility = View.GONE
        } else {
            noCoursesTextView.visibility = View.GONE
            viewAllCoursesRecyclerView.visibility = View.VISIBLE
            courseAdapter.updateData(filteredCourses)
        }
        Log.d("UIUpdateDebug", "updateCourseList: End")
    }

    private fun populateFilterOptions() {
        filterOptionsMap["skill"] = filteredCourses.flatMap { it.skill ?: emptyList() }.distinct().sorted()
        filterOptionsMap["curriculum"] = filteredCourses.flatMap { it.curriculum ?: emptyList() }.distinct().sorted()
        filterOptionsMap["style"] = filteredCourses.flatMap { it.style ?: emptyList() }.distinct().sorted()
        filterOptionsMap["educator"] = filteredCourses.mapNotNull { it.educator }.distinct().sorted()
        filterOptionsMap["series"] = filteredCourses.flatMap { it.series ?: emptyList() }.distinct().sorted()
    }

    private fun setupFilterButtons() {
        ownedFilterButton.setOnClickListener { showOwnedFilterBottomSheet() }
        skillFilterButton.setOnClickListener { showFilterBottomSheet("skill", skillFilterButton) }
        curriculumFilterButton.setOnClickListener { showFilterBottomSheet("curriculum", curriculumFilterButton) }
        styleFilterButton.setOnClickListener { showFilterBottomSheet("style", styleFilterButton) }
        educatorFilterButton.setOnClickListener { showFilterBottomSheet("educator", educatorFilterButton) }
        seriesFilterButton.setOnClickListener { showFilterBottomSheet("series", seriesFilterButton) }
    }

    private fun showOwnedFilterBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.filter_bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val filterTitleTextView = bottomSheetView.findViewById<TextView>(R.id.filterTitleTextView)
        val filterOptionsRadioGroup = bottomSheetView.findViewById<RadioGroup>(R.id.filterOptionsRadioGroup)

        filterTitleTextView.text = "Owned Filter"

        val yesRadioButton = RadioButton(this)
        yesRadioButton.text = "Yes"
        filterOptionsRadioGroup.addView(yesRadioButton)

        val noRadioButton = RadioButton(this)
        noRadioButton.text = "No"
        filterOptionsRadioGroup.addView(noRadioButton)

        filterOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                yesRadioButton.id -> ownedFilterSelected = true
                noRadioButton.id -> ownedFilterSelected = false
            }
            applyFilters()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

    private fun showFilterBottomSheet(filterType: String, filterButton: Button) {
        val bottomSheetView = layoutInflater.inflate(R.layout.filter_bottom_sheet_dialog, null)
        bottomSheetDialog.setContentView(bottomSheetView)

        val filterTitleTextView = bottomSheetView.findViewById<TextView>(R.id.filterTitleTextView)
        val filterOptionsRadioGroup = bottomSheetView.findViewById<RadioGroup>(R.id.filterOptionsRadioGroup)
        filterTitleTextView.text = "${filterType.capitalize()} Filter"

        val options = filterOptionsMap[filterType] ?: emptyList()
        Log.d("FilterSheetDebug", "Filter Type: $filterType, Options: $options")

        options.forEach { option ->
            val radioButton = RadioButton(this)
            radioButton.text = option
            filterOptionsRadioGroup.addView(radioButton)
        }

        filterOptionsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadioButton = bottomSheetView.findViewById<RadioButton>(checkedId)
            val selectedOption = selectedRadioButton.text.toString()

            selectedFilters[filterType] = mutableSetOf(selectedOption)
            applyFilters()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }
    private fun applyFilters() {
        Log.d("FilterDebug", "applyFilters: Start - filteredCourses size before filter: ${filteredCourses.size}")

        var tempCourses = filteredCourses

        tempCourses = tempCourses.filter { course ->
            var matchesAllFilters = true

            if (ownedFilterSelected != null) {
                val courseOwnedBoolean = course.owned == 1
                if (courseOwnedBoolean != ownedFilterSelected) {
                    matchesAllFilters = false
                    return@filter false
                }
            }

            selectedFilters.forEach { (filterType, selectedOptions) ->
                val courseOptionsList: List<String>? = when (filterType) {
                    "skill" -> course.skill
                    "curriculum" -> course.curriculum
                    "style" -> course.style
                    "educator" -> course.educator?.let { listOf(it) }
                    "series" -> course.series
                    else -> null
                }

                if (!courseOptionsList.isNullOrEmpty()) {
                    val matchesCurrentFilter = selectedOptions.any { selectedOption ->
                        courseOptionsList.contains(selectedOption)
                    }
                    if (!matchesCurrentFilter) {
                        matchesAllFilters = false
                        return@filter false
                    }
                } else if (selectedOptions.isNotEmpty()) {
                    matchesAllFilters = false
                    return@filter false
                }
            }
            matchesAllFilters
        }
        filteredCourses = tempCourses
        Log.d("FilterDebug", "applyFilters: End - filteredCourses size after filter: ${filteredCourses.size}")
        updateCourseList()
        updateFilterButtonUI()
    }

    private fun updateFilterButtonUI() {
        updateFilterButtonHighlight(ownedFilterButton, ownedFilterSelected != null)
        updateFilterButtonHighlight(skillFilterButton, selectedFilters.containsKey("skill"))
        updateFilterButtonHighlight(curriculumFilterButton, selectedFilters.containsKey("curriculum"))
        updateFilterButtonHighlight(styleFilterButton, selectedFilters.containsKey("style"))
        updateFilterButtonHighlight(educatorFilterButton, selectedFilters.containsKey("educator"))
        updateFilterButtonHighlight(seriesFilterButton, selectedFilters.containsKey("series"))

        updateFilterButtonText(skillFilterButton, "Skill", selectedFilters["skill"])
        updateFilterButtonText(curriculumFilterButton, "Curriculum", selectedFilters["curriculum"])
        updateFilterButtonText(styleFilterButton, "Style", selectedFilters["style"])
        updateFilterButtonText(educatorFilterButton, "Educator", selectedFilters["educator"])
        updateFilterButtonText(seriesFilterButton, "Series", selectedFilters["series"])

        if (ownedFilterSelected != null) {
            val ownedText = if (ownedFilterSelected == true) "Owned: Yes" else "Owned: No"
            ownedFilterButton.text = ownedText
        } else {
            ownedFilterButton.text = "Owned"
        }
    }

    private fun updateFilterButtonText(button: Button, filterName: String, selectedValues: Set<String>?) {
        if (selectedValues != null && selectedValues.isNotEmpty()) {
            button.text = "$filterName: ${selectedValues.joinToString(", ")}"
        } else {
            button.text = filterName
        }
    }

    private fun updateFilterButtonHighlight(button: Button, isHighlighted: Boolean) {
        if (isHighlighted) {
            button.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_200))
            button.setTextColor(Color.WHITE)
        } else {
            button.setBackgroundColor(ContextCompat.getColor(this, android.R.color.darker_gray))
            button.setTextColor(Color.WHITE)
        }
    }

    private fun setupClearFilterButton() {
        clearFilterButton.setOnClickListener { clearFilters() }
    }

    private fun clearFilters() {
        selectedFilters.clear()
        ownedFilterSelected = null
        filteredCourses = allCourses
        updateCourseList()
        updateFilterButtonUI()
        searchView.setQuery("", false) // Clear search query
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("SearchViewDebug", "onQueryTextChange: Text changed to: $newText") // Add this Log
                filterBySearch(newText.orEmpty())
                return true
            }
        })
    }
    private fun filterBySearch(query: String) {
        Log.d("SearchViewDebug", "filterBySearch: Query received: $query")

        val searchText = query.lowercase()
        val category = intent.getStringExtra(VIEW_ALL_CATEGORY_KEY) ?: CATEGORY_ALL
        var categoryFilteredCourses: List<Course> = allCourses


        categoryFilteredCourses = when (category) {
            CATEGORY_OWNED -> allCourses.filter { it.owned == 1 }
            CATEGORY_GUITAR_LAB -> {
                val guitarLabCourses = allCourses.filter { course ->
                    val courseTitleLower = course.title.lowercase()
                    courseTitleLower.contains("guitar")
                }
                Log.d("CategoryFilterDebug", "Category: $category, Count: ${guitarLabCourses.size} (Title contains 'Guitar')")
                guitarLabCourses
            }
            CATEGORY_FAVORITES -> {

                val favoriteCollection = currentCourseResponse?.result?.collections?.smart?.find {
                    it.label.equals("Favorites", ignoreCase = true)
                }
                val favoriteCourseIds = favoriteCollection?.courses ?: emptyList()

                val favoritesCourses = allCourses.filter { course ->
                    favoriteCourseIds.contains(course.id)
                }
                Log.d("CategoryFilterDebug", "Category: $category, Count: ${favoritesCourses.size} (SmartCollection Favorites)")
                favoritesCourses
            }
            CATEGORY_RECENTLY_WATCHED -> {
                emptyList()
            }
            CATEGORY_ALL -> allCourses
            else -> allCourses
        }


        filteredCourses = if (searchText.isNotEmpty()) {
            categoryFilteredCourses.filter { course ->
                course.title.lowercase().contains(searchText)
            }
        } else {
            categoryFilteredCourses
        }

        Log.d("SearchViewDebug", "filterBySearch: Filtered course count: ${filteredCourses.size}")
        updateCourseList()
        updateFilterButtonUI()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}