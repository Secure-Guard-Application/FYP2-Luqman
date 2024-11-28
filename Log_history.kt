package com.example.secureguardapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Log_history : AppCompatActivity() {

    private lateinit var adapter: ScanHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_history)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val scanHistoryList = SecureGuardDatabase.getDatabase(applicationContext).scanHistoryDao().getAllScans()
            Log.d("Log_history", "Retrieved scan history list: $scanHistoryList") // Debug log

            adapter = ScanHistoryAdapter(scanHistoryList.toMutableList()) { scanHistory ->
                //Delete action
                lifecycleScope.launch {
                    SecureGuardDatabase.getDatabase(applicationContext).scanHistoryDao().deleteScan(scanHistory)
                    withContext(Dispatchers.Main) {
                        adapter.removeItem(scanHistory)
                    }
                }
            }
            recyclerView.adapter = adapter
        }

    }
}