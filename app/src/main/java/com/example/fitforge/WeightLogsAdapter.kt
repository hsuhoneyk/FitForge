package com.example.fitforge

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class WeightLogsAdapter(private val logs: MutableList<WeightLog>) :
    RecyclerView.Adapter<WeightLogsAdapter.LogViewHolder>() {

    class LogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvExercise: TextView = view.findViewById(R.id.tvexercise)
        val tvWeight: TextView = view.findViewById(R.id.tvweight)
        val tvReps: TextView = view.findViewById(R.id.tvreps)
        val tvSets: TextView = view.findViewById(R.id.tvsets)
        val tvVolume: TextView = view.findViewById(R.id.tvvolume)
        val tvCalories: TextView = view.findViewById(R.id.tvcalories)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weightlog_item, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val log = logs[position]

        holder.tvExercise.text = log.exercise
        holder.tvWeight.text = String.format("%.1f", log.weight)
        holder.tvReps.text = log.reps.toString()
        holder.tvSets.text = log.sets.toString()
        holder.tvVolume.text = String.format("%.1f", log.volume)
        holder.tvCalories.text = String.format("%.1f", log.calories)
        holder.tvDate.text = log.date
    }

    override fun getItemCount(): Int = logs.size
}