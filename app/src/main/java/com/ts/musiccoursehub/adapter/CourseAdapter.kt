package com.ts.musiccoursehub.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.ts.musiccoursehub.R
import com.ts.musiccoursehub.data.Course


class CourseAdapter(private var courses: List<Course>) :
    RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseImageView: ImageView = itemView.findViewById(R.id.courseImageView)
        val courseNameTextView: TextView = itemView.findViewById(R.id.courseNameTextView)
        val educatorNameTextView: TextView = itemView.findViewById(R.id.educatorNameTextView)
        val ownedBadgeTextView: TextView = itemView.findViewById(R.id.ownedBadgeTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val currentCourse = courses[position]
        holder.courseNameTextView.text = currentCourse.title
        holder.educatorNameTextView.text = currentCourse.educator ?: ""
        if (currentCourse.educator?.isNullOrEmpty() == true) {
            holder.educatorNameTextView.visibility = View.GONE
        } else {
            holder.educatorNameTextView.visibility = View.VISIBLE
        }

        val imageUrl = "https://d2xkd1fof6iiv9.cloudfront.net/images/courses/${currentCourse.id}/169_820.jpg"

        holder.courseImageView.load(imageUrl) {
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_launcher_background)
            crossfade(true)
            listener(
                onSuccess = { request, result ->
                    println("Coil Image loaded successfully for course ID: ${currentCourse.id}")
                },
                onError = { request, throwable ->
                    println("Coil Image load failed for course ID: ${currentCourse.id}, URL: $imageUrl, Error: ${throwable.throwable.message}")
                }
            )
        }

        // Control visibility of the LinearLayout 'ownedBadgeLayout'
        if (currentCourse.owned != null && currentCourse.owned == 1) {
            holder.ownedBadgeTextView.visibility = View.VISIBLE // Show the LinearLayout
        } else {
            holder.ownedBadgeTextView.visibility = View.GONE // Hide the LinearLayout
        }
    }

    override fun getItemCount() = courses.size

    // In CourseAdapter.kt (inside updateData() function)
    fun updateData(newCourses: List<Course>) {
        Log.d("AdapterDebug", "CourseAdapter.updateData: Received new course list of size: ${newCourses.size}") // Add this log
        courses = newCourses
        notifyDataSetChanged()
    }
}