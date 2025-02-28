# Music Course Hub Android App

## Overview

This Android application is designed to be a central hub for browsing and viewing music courses. It fetches course data from a remote API and displays it in a user-friendly interface.  Users can view all available courses, filter them by category (like Owned, Guitar Lab, Favorites, Recently Watched), and search for specific courses by title.

## Features

*   **View All Courses:** Displays a comprehensive list of all available music courses fetched from the API.
*   **Category Filtering:**
    *   **Owned Courses:** Shows courses marked as "owned" by the user.
    *   **Guitar Lab Courses:** Filters courses related to "Guitar Lab" (currently based on titles containing "guitar").
    *   **Favorites:**  Displays courses marked as "favorites" (based on SmartCollection data from the API).
    *   **Recently Watched:**  *(Placeholder - currently displays no courses, implementation needed)*
    *   **All Courses:**  Displays all courses without category filtering.
*   **Search Functionality:** Allows users to search for courses by title. Search is applied after category filtering.
*   **Filtering Options:**
    *   **Owned Filter:** Filter courses to show only owned or not owned.
    *   **Skill, Curriculum, Style, Educator, Series Filters:**  Filter courses based on these attributes. Filter options are dynamically populated from the displayed courses.
    *   **Clear Filters:** Resets all applied filters and search queries.
*   **Responsive Grid Layout:** Courses are displayed in a grid layout, optimized for Android devices.
*   **Toolbar with Back Navigation:**  Provides a standard Android toolbar with a back button to easily navigate back to the previous screen.
*   **"No Courses Found" Message:**  Displays a user-friendly message when no courses match the selected filters or search criteria.
*   **Error Handling:** Displays Toast messages for API fetch errors (network issues, unsuccessful responses).
*   **Logging:** Includes Logcat logging for debugging purposes, especially for category filtering and search functionality.

## API Integration

This app utilizes Retrofit 2 to interact with a backend API to fetch course data.

*   **Retrofit Client:**  `RetrofitClient.instance` in `com.ts.musiccoursehub.api.RetrofitClient.kt` is used to handle API calls.
*   **API Interface:** `ApiService` interface (likely in `com.ts.musiccoursehub.api.ApiService.kt` - *note: file name might vary*) defines the API endpoints.
*   **Data Models:** Data classes in `com.ts.musiccoursehub.data` (e.g., `CourseResponse.kt`, `Course.kt`, `SmartCollection.kt`) are used to parse the JSON responses from the API using Gson.
*   **API Base URL:**  The API base URL is configured in `RetrofitClient.kt`.  **Please ensure this URL is correctly set to point to your backend API.**  *(Note:  Example base URL placeholder might be present in the code)*

    ```kotlin
    // Example (check your RetrofitClient.kt for the actual URL)
    private const val BASE_URL = "YOUR_API_BASE_URL_HERE"
    ```

## Setup Instructions

1.  **Clone the Repository (if applicable):**
    ```bash
    git clone [repository_url]
    cd [repository_directory]
    ```
2.  **Open in Android Studio:**
    *   Start Android Studio.
    *   Select "Open an existing project" and navigate to the cloned repository directory.
3.  **API Base URL Configuration:**
    *   Open `com.ts.musiccoursehub.api.RetrofitClient.kt`.
    *   Locate the `BASE_URL` constant.
    *   **Replace `"YOUR_API_BASE_URL_HERE"` with the actual base URL of your music course API.**

4.  **Build the Project:**
    *   In Android Studio, go to `Build` > `Make Project` or `Build` > `Rebuild Project`.
    *   Resolve any dependencies or build errors that may arise.

## Run Instructions

1.  **Connect a Device or Emulator:**
    *   Connect a physical Android device to your computer or start an Android emulator in Android Studio (AVD Manager).
2.  **Run the App:**
    *   In Android Studio, select `Run` > `Run 'app'` (or press Shift+F10).
    *   Choose your connected device or emulator as the deployment target.
    *   The application will be built and installed on your device/emulator and then launched.
3.  **Navigate and Test:**
    *   Once the app is running, you can navigate through the `MainActivity` and use the "View All" links to access `ViewAllActivity`.
    *   Test the category filters, search functionality, and observe the course listings.
    *   Check Logcat in Android Studio for any debug logs or errors (filter by "CategoryFilterDebug", "SearchViewDebug", "UIUpdateDebug", "FilterDebug", "FilterSheetDebug", "SeriesDebug" to see specific logs related to filtering and search).

## Testing

*   **Category Filters:** Verify that clicking on "View All" links for "Owned", "Guitar Lab", "Favorites", and "Recently Watched" in `MainActivity` correctly displays the corresponding courses in `ViewAllActivity`.
*   **Search:** Test the search bar in `ViewAllActivity` by entering course titles or keywords. Ensure that the course list updates to show only matching courses after applying the search.
*   **Filter Buttons:** Test each filter button (Owned, Skill, Curriculum, Style, Educator, Series).  Confirm that selecting filter options correctly filters the course list and that the filter button UI updates to reflect the selected filters.
*   **Clear Filters:** Verify that the "Clear Filters" button correctly resets all filters and the search query.
*   **Error Handling:** Intentionally simulate network errors (e.g., by disabling internet connection on the device/emulator) and check if the app displays the error Toast messages appropriately.

## Potential Future Enhancements

*   **User Authentication and Accounts:** Implement user login and registration to personalize the experience (e.g., truly save favorites, track watched history).
*   **Real Favorites Implementation:** Replace the placeholder "Favorites" with a persistent favorites system (e.g., using a local database or user preferences) to allow users to actually mark courses as favorites.
*   **Recently Watched Functionality:** Implement the "Recently Watched" category to track and display courses the user has recently viewed.
*   **Course Detail Screen:** Create a dedicated activity to display detailed information about each course when a user taps on a course item in the list.
*   **Video Playback Integration:** Integrate a video player to allow users to watch course videos directly within the app.
*   **Improved UI/UX:** Enhance the user interface and user experience with more refined layouts, animations, and user-friendly interactions.
*   **Offline Support:** Implement caching and offline data persistence to allow users to browse courses even without an active internet connection.
*   **Pagination/Infinite Scrolling:** Implement pagination or infinite scrolling for very large course lists to improve performance and user experience when loading many courses.

## Dependencies

*   **Kotlin:** Programming language.
*   **Android SDK:** For Android app development.
*   **Retrofit 2:** For networking and API communication.
*   **Gson:** For JSON serialization/deserialization.
*   **RecyclerView:** For efficient display of course lists.
*   **SearchView:** For search functionality.
*   **Material Design Components:** For BottomSheetDialog and other UI elements.
*   **AppCompat:** For AppCompatActivity and Toolbar.
*   **AndroidX Core and UI Libraries:** (e.g., `androidx.core`, `androidx.appcompat`, `androidx.recyclerview`, `androidx.constraintlayout`, `androidx.swiperefreshlayout`)
