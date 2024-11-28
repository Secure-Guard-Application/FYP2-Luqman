package com.example.secureguardapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanHistoryAdapter(
    private var scanHistoryList: MutableList<ScanHistory>,
    private val onDeleteClick: (ScanHistory) -> Unit // Callback for delete action
) : RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val urlText: TextView = view.findViewById(R.id.urlText)
        val statusText: TextView = view.findViewById(R.id.statusText)
        val dateText: TextView = view.findViewById(R.id.dateText)
        val categoryText: TextView = view.findViewById(R.id.categoryText)
        val messageText: TextView = view.findViewById(R.id.messageText)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.scan_history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = scanHistoryList[position]
        Log.d("ScanHistoryAdapter", "Binding item: $item") // Debug log to check the data being bound

        // Inside onBindViewHolder
        val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(item.scanDate))

        holder.dateText.text = formattedDate


        holder.urlText.text = item.url

        // Set the status text and color
        if (item.isMalicious) {
            holder.statusText.text = "Malicious"
            holder.statusText.setTextColor(holder.itemView.context.getColor(R.color.red)) // Set text color to red
        } else {
            holder.statusText.text = "Safe"
            holder.statusText.setTextColor(holder.itemView.context.getColor(R.color.green)) // Set text color to green
        }

        // Show or hide the category text
        if (item.isMalicious) {
            holder.categoryText.visibility = View.VISIBLE // Show category for malicious links
            holder.categoryText.text = item.category
        } else {
            holder.categoryText.visibility = View.GONE // Hide category for safe links
        }


        holder.messageText.text = item.message

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = scanHistoryList.size

    // Method to update the list after deletion
    fun removeItem(item: ScanHistory) {
        val position = scanHistoryList.indexOf(item)
        if (position != -1) {
            scanHistoryList.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
