package com.example.secureguardapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QRScanner : AppCompatActivity() {

    lateinit var qrScanLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscanner)

        Log.d("QRScanner", "onCreate: Activity started")

        // Set up the QR scanner launcher using ScanContract
        qrScanLauncher = registerForActivityResult(ScanContract()) { result ->
            if (result.contents != null) {
                val scannedUrl = result.contents
                Log.d("QRScanner", "Scanned URL: $scannedUrl")
                checkUrlSafety(scannedUrl)
            } else {
                Log.d("QRScanner", "No QR Code detected")
                Toast.makeText(this, "No QR Code detected", Toast.LENGTH_LONG).show()
            }
        }

        val openQRScannerButton = findViewById<Button>(R.id.openQRScannerButton)
        openQRScannerButton.setOnClickListener {
            Log.d("QRScanner", "Button Clicked: Starting QR Scanner")
            startQRScanner()
        }
    }

    private fun startQRScanner() {
        Log.d("QRScanner", "startQRScanner: Initializing scan")
        val options = ScanOptions().apply {
            setPrompt("Scan a QR code")
            setBeepEnabled(true)
            setOrientationLocked(false)
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        }
        qrScanLauncher.launch(options)
    }

    private fun saveScanToDatabase(url: String, isMalicious: Boolean, category: String, message: String) {
        val scanHistory = ScanHistory(
            url = url,
            isMalicious = isMalicious,
            scanDate = System.currentTimeMillis(),
            category = category,
            message = message
        )

        CoroutineScope(Dispatchers.IO).launch {
            SecureGuardDatabase.getDatabase(applicationContext).scanHistoryDao().insert(scanHistory)
        }
    }

    private fun startQrResultActivity(scannedUrl: String, isMalicious: Boolean) {
        val intent = Intent(this, QrResult::class.java).apply {
            putExtra("SCANNED_URL", scannedUrl)
            putExtra("IS_MALICIOUS", isMalicious)
        }
        startActivity(intent)
    }

    private fun checkUrlSafety(url: String) {
        Log.d("QRScanner", "checkUrlSafety: Checking URL safety for $url")

        val retrofit = Retrofit.Builder()
            .baseUrl("https://malicious-scanner.p.rapidapi.com/rapid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(UrQrInter::class.java)

        val call = api.scanDomain(url)
        call.enqueue(object : Callback<UrQrApi?> {
            override fun onResponse(call: Call<UrQrApi?>, response: Response<UrQrApi?>) {
                Log.d("QRScanner", "onResponse: Received API response")
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null) {
                        val domain = if (!apiResponse.data.domain.isNullOrEmpty()) {
                            apiResponse.data.domain
                        } else if (!apiResponse.data.url.isNullOrEmpty()) {
                            apiResponse.data.url
                        } else {
                            url // Fallback to the originally scanned URL
                        }



                        if (domain.isNotBlank()) {
                            val status = apiResponse.data.status?.trim()
                            val isMalicious = status.equals("Malicious", ignoreCase = true)
                            val category = if (isMalicious) "Malicious" else "Legitimate"
                            val message = if (isMalicious) "Harmful URL detected" else "Safe URL detected"

                            Log.d("QRScanner", "Domain: $domain, Status: $status, isMalicious: $isMalicious")
                            saveScanToDatabase(domain, isMalicious, category, message)
                            startQrResultActivity(domain, isMalicious) // Navigate to result activity
                        } else {
                            Log.e("QRScanner", "API returned a null or blank domain/URL")
                        }
                    } else {
                        Log.e("QRScanner", "apiResponse.data is null")
                    }
                } else {
                    Log.e("QRScanner", "API response unsuccessful: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<UrQrApi?>, t: Throwable) {
                Log.e("QRScanner", "API call failed: ${t.message}")
            }
        })
    }

}
