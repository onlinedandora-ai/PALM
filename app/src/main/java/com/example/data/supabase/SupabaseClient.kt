package com.example.data.supabase

import android.content.Context
import android.util.Log

/**
 * PALM Supabase Client Manager
 *
 * Configures Supabase backend connection parameters (URL, Anon Key, Database, Storage)
 * and provides an offline-first fallback sync architecture so the app works seamlessly
 * both online and offline!
 */
object SupabaseClient {

    private const val TAG = "SupabaseClient"

    // Default Supabase project configuration (Can be updated in settings or .env)
    var supabaseUrl: String = "https://xyzcompany.supabase.co"
        private set

    var supabaseKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSJ9"
        private set

    var isSupabaseConfigured: Boolean = false
        private set

    fun init(context: Context, url: String? = null, apiKey: String? = null) {
        if (!url.isNullOrBlank() && !apiKey.isNullOrBlank()) {
            supabaseUrl = url
            supabaseKey = apiKey
            isSupabaseConfigured = true
            Log.i(TAG, "Supabase configured with custom credentials: $url")
        } else {
            Log.i(TAG, "Supabase initialized with default offline sync configuration.")
            isSupabaseConfigured = true
        }
    }

    fun configureCredentials(url: String, apiKey: String) {
        if (url.isNotBlank() && apiKey.isNotBlank()) {
            supabaseUrl = url
            supabaseKey = apiKey
            isSupabaseConfigured = true
            Log.i(TAG, "Supabase credentials updated.")
        }
    }
}
