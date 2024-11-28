package com.example.secureguardapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    // Method to navigate to URL Scanner
    fun openURLscanner(view: View) {
        val intent = Intent(this, UrlScanner::class.java)
        startActivity(intent)
    }

    // Method to navigate to QR Scanner
    fun openQRscanner(view: View) {
        val intent = Intent(this, QRScanner::class.java)
        startActivity(intent)
    }

    // Method to navigate to Browser History
    fun openLogHistory(view: View) {
        val intent = Intent(this, Log_history::class.java)
        startActivity(intent)
    }

    // Method to navigate to Malware Scanner
    fun openMalwareScanner(view: View) {
        val intent = Intent(this, Malware_Scanner::class.java)
        startActivity(intent)
    }

}