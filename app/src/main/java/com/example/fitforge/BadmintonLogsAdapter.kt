package com.example.fitforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class BadmintonLogsAdapter(private val logs: MutableList<BadmintonLog>) :
    RecyclerView.Adapter<BadmintonLogsAdapter.LogViewHolder>() {

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    private val outputDateTimeFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSessionIcon: TextView = view.findViewById(R.id.tvSessionIcon)
        val tvSessionType: TextView = view.findViewById(R.id.tvSessionType)
        val tvDateTime: TextView = view.findViewById(R.id.tvDateTime)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvCalories: TextView = view.findViewById(R.id.tvCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.badmintonlog_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]

        val icon = when (log.sessionType) {
            "Single" -> "🏸"
            "Double" -> "🏸"
            "Drills" -> "🎯"
            else -> "🏸"
        }
        holder.tvSessionIcon.text = icon

        // Display text
        val sessionTypeText = when (log.sessionType) {
            "Single" -> "Single Match"
            "Double" -> "Double Match"
            "Drills" -> "Training Drills"
            else -> log.sessionType
        }
        holder.tvSessionType.text = sessionTypeText
        holder.tvSessionType.visibility = View.VISIBLE

        val formattedDateTime = formatDateTime(log.workoutDate)
        holder.tvDateTime.text = formattedDateTime

        holder.tvDuration.text = "${log.durationMinutes} min"
        holder.tvCalories.text = "${log.calorieBurn} kcal"
    }

    override fun getItemCount(): Int = logs.size

    private fun formatDateTime(dateString: String): String {
        return try {
            val date = inputDateFormat.parse(dateString)
            outputDateTimeFormat.format(date ?: Date())
        } catch (e: Exception) {
            dateString
        }
    }
}