package com.example.secureguardapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UrlScanner : AppCompatActivity() {

    lateinit var urlEditText: EditText
    lateinit var checkUrlButton: Button
    lateinit var resultCard: LinearLayout
    lateinit var urlStatus: TextView
    lateinit var urlStatusIcon: ImageView
    lateinit var urlDetails: TextView
    lateinit var urlMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_scanner)

        urlEditText = findViewById(R.id.urlEditText)
        checkUrlButton = findViewById(R.id.checkUrlButton)
        resultCard = findViewById(R.id.resultCard)
        urlStatus = findViewById(R.id.urlStatus)
        urlStatusIcon = findViewById(R.id.urlStatusIcon)
        urlDetails = findViewById(R.id.urlDetails)
        urlMessage = findViewById(R.id.urlMessage)

        // Initially hide the result card
        resultCard.visibility = View.GONE

        checkUrlButton.setOnClickListener {
            val enteredUrl = urlEditText.text.toString().trim()
            Log.d("UrlScanner", "Entered URL: $enteredUrl")
            if (enteredUrl.isNotEmpty()) {
                checkUrlSafety(enteredUrl)
            } else {
                Toast.makeText(this, "Please enter a URL.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //Database

    private fun saveScanToDatabase(url: String, isMalicious: Boolean, category: String, message: String) {
        Log.d("SaveToDatabase", "Attempting to save to database with URL: $url")

        if (url.isEmpty()) {
            Log.e("SaveToDatabase", "URL is empty. Skipping save.")
            return
        }
        Log.d("SaveToDatabase", "URL is valid, proceeding to save.")
        Log.d("SaveToDatabase", "Parameters - URL: $url, isMalicious: $isMalicious, Category: $category, Message: $message")

        val scanHistory = ScanHistory(
            url = url,
            isMalicious = isMalicious,
            scanDate = System.currentTimeMillis(),
            category = category,
            message = message
        )

        // Corrected: Insert into database if the URL is not empty
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SecureGuardDatabase.getDatabase(applicationContext).scanHistoryDao().insert(scanHistory)
                Log.d("SaveToDatabase", "Inserted scan entry: $scanHistory")
            } catch (e: Exception) {
                Log.e("SaveToDatabase", "Failed to insert entry: ${e.message}")
            }
        }
    }

    private fun checkUrlSafety(domain: String) {
        Log.d("CheckUrlSafety", "Received domain name: $domain")

        if(domain.isEmpty()){
            Log.e("CheckUrlSafety", "Domain is empty or invalid.")
            return
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://malicious-scanner.p.rapidapi.com/rapid/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(UrQrInter::class.java)

        val call = api.scanDomain(domain)
        call.enqueue(object : Callback<UrQrApi?> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<UrQrApi?>, response: Response<UrQrApi?>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()!!
                    val category = apiResponse.data.category
                    val isMalicious = apiResponse.data.status == "Malicious"
                    val message = apiResponse.data.message ?: "No message"

                    Log.d("CheckUrlSafety", "Full API Response: $apiResponse")
                    Log.d("CheckUrlSafety", "Domain: $domain, Status: ${apiResponse.data.status}")
                    Log.d("CheckUrlSafety", "Category: $category, Status: ${apiResponse.data.status}")


                    // Call the saveScanToDatabase function with valid parameters
                    saveScanToDatabase(domain, isMalicious, category, message)

                    // Update UI accordingly (e.g., show result card)
                    runOnUiThread {
                        resultCard.visibility = View.VISIBLE
                        if (isMalicious) {
                            urlStatusIcon.setImageResource(R.drawable.ic_harmful) // Set harmful icon
                            urlStatus.text = "Warning! Harmful URL"
                            urlDetails.text = "Category:$category"
                            urlStatus.setTextColor(Color.RED)
                            urlMessage.text = "Details: $message"
                            Log.d("API Debug", "URL marked as harmful.")
                        } else {
                            urlStatusIcon.setImageResource(R.drawable.ic_legitimate) // Set legitimate icon
                            urlStatus.text = "Legitimate URL"
                            urlStatus.setTextColor(Color.GREEN)
                            urlMessage.text = "Details: This URL appears safe."
                            Log.d("API Debug", "URL marked as legitimate.")
                        }
                    }
                } else {
                    Log.e("CheckUrlSafety", "API response is not successful or body is null.")
                    Toast.makeText(this@UrlScanner, "Error scanning the URL. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(p0: Call<UrQrApi?>, t: Throwable) {
                Toast.makeText(this@UrlScanner, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.d("API Debug", "Error: ${t.message}")
            }
        })
    }
}
