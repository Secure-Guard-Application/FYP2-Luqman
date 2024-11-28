package com.example.secureguardapplication

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class QrResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_result)

        // Find views in the layout
        val urlTextView = findViewById<TextView>(R.id.scannedUrlTextView)
        val statusIcon = findViewById<ImageView>(R.id.statusIcon)
        val statusText = findViewById<TextView>(R.id.statusText)
        val openButton = findViewById<Button>(R.id.openButton)
        val copyDataButton = findViewById<Button>(R.id.copyDataButton)
        val scanAnotherTextView = findViewById<TextView>(R.id.scanAnotherTextView)

        // Get data from Intent
        val scannedUrl = intent.getStringExtra("SCANNED_URL")
        val isMalicious = intent.getBooleanExtra("IS_MALICIOUS", false)


        if (isMalicious){
            statusIcon.setImageResource(R.drawable.ic_harmful)
            statusText.text = "Malicious"
            statusText.setTextColor(getColor(R.color.red))

            // Disable and hide the buttons for malicious URLs
            openButton.isEnabled = false
            openButton.visibility = View.GONE // or View.INVISIBLE if you want to reserve space
            copyDataButton.isEnabled = false
            copyDataButton.visibility = View.GONE // or View.INVISIBLE if you want to reserve space
        }else{
            statusIcon.setImageResource(R.drawable.ic_legitimate)
            statusText.text = "safe"
            statusText.setTextColor(getColor(R.color.green))
        }

        urlTextView.text = scannedUrl


        openButton.setOnClickListener{
            if (!scannedUrl.isNullOrEmpty()) {
                val validUrl = if (scannedUrl.startsWith("http://") || scannedUrl.startsWith("https://")) {
                    scannedUrl
                } else {
                    "http://$scannedUrl"
                }
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
                startActivity(browserIntent)
            } else {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
            }
        }

        copyDataButton.setOnClickListener {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Scanned URL", scannedUrl)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "URL copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        scanAnotherTextView.setOnClickListener {
            finish() // Finish this activity to go back to QR scanner screen
        }

    }
}