package com.example.fitforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CyclingLogsAdapter(private val logs: MutableList<CyclingLog>): RecyclerView.Adapter<CyclingLogsAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvcyclingRoute: TextView = view.findViewById(R.id.tvCyclingRouteName)
        val tvcyclingDistance: TextView = view.findViewById(R.id.tvCyclingTotalDistance)
        val tvcyclingSpeed: TextView = view.findViewById(R.id.tvCyclingSpeed)
        val tvcyclingCalories: TextView = view.findViewById(R.id.tvCyclingCalorieBurn)
        val tvcyclingDate: TextView = view.findViewById(R.id.tvCyclingWorkoutDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cyclinglog_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]

        holder.tvcyclingRoute.text = log.cycling_routeName
        holder.tvcyclingDistance.text = String.format("%.2f", log.cycling_distance)
        holder.tvcyclingSpeed.text = String.format("%.1f", log.cycling_speed)
        holder.tvcyclingCalories.text = log.cycling_calories.toString()
        holder.tvcyclingDate.text = log.cdate
    }

    override fun getItemCount(): Int = logs.size
}