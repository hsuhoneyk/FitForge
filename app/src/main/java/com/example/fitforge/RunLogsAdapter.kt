package com.example.fitforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RunLogsAdapter(private val logs: MutableList<RunLog>): RecyclerView.Adapter<RunLogsAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoute: TextView = view.findViewById(R.id.tvRouteName)

        val tvDistance: TextView = view.findViewById(R.id.tvTotalDistance)
        val tvSpeed: TextView = view.findViewById(R.id.tvSpeed)
        val tvCalories: TextView = view.findViewById(R.id.tvCalorieBurn)
        val tvDate: TextView = view.findViewById(R.id.tvWorkoutDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.runlog_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]

        holder.tvRoute.text = log.routeName
        holder.tvDistance.text = String.format("%.2f", log.distance)
        holder.tvSpeed.text = String.format("%.1f", log.speed)
        holder.tvCalories.text = log.calories.toString()
        holder.tvDate.text = log.date
    }

    override fun getItemCount(): Int = logs.size
}