package com.example.secureguardapplication

data class Data(
    val _id: String,
    val category: String,
    val domain: String,
    val domain_age: String,
    val finishScan: Boolean,
    val is_anti_bot: Boolean,
    val is_captcha: Boolean,
    val is_new_domain: Boolean,
    val is_top_domain: Boolean,
    val malware_type: String,
    val message: String,
    val name: String,
    val original_url: String,
    val redirect_url: String,
    val scan: List<Any>,
    val status: String,
    val sub_status: List<SubStatu>,
    val type: String,
    val url: String
)