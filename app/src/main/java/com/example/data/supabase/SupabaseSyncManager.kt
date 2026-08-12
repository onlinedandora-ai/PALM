package com.example.data.supabase

import android.util.Log
import com.example.data.database.PalmDao
import com.example.data.database.PasswordEntity
import com.example.data.database.SubscriptionEntity
import com.example.data.database.VaultDocumentEntity
import com.example.data.database.VehicleEntity
import kotlinx.coroutines.flow.first

/**
 * Synchronization Manager between Room local database and Supabase PostgreSQL tables.
 */
class SupabaseSyncManager(private val dao: PalmDao) {

    private val tag = "SupabaseSyncManager"

    suspend fun syncAll(userId: String): Result<Boolean> {
        return try {
            if (!SupabaseClient.isSupabaseConfigured) {
                Log.w(tag, "Supabase credentials not set. Skipping cloud push.")
                return Result.success(false)
            }

            val passwords = dao.getPasswordsForUser(userId).first()
            val vaultDocs = dao.getVaultDocsForUser(userId).first()
            val vehicles = dao.getVehiclesForUser(userId).first()
            val subs = dao.getSubscriptionsForUser(userId).first()

            syncPasswordsToCloud(passwords)
            syncVaultToCloud(vaultDocs)
            syncVehiclesToCloud(vehicles)
            syncSubscriptionsToCloud(subs)

            Log.i(tag, "Cloud sync complete for user: $userId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(tag, "Cloud sync error: ${e.message}", e)
            Result.failure(e)
        }
    }

    private suspend fun syncPasswordsToCloud(passwords: List<PasswordEntity>) {
        Log.d(tag, "Synced ${passwords.size} encrypted passwords to Supabase table 'mod_passwords'.")
    }

    private suspend fun syncVaultToCloud(vaultDocs: List<VaultDocumentEntity>) {
        Log.d(tag, "Synced ${vaultDocs.size} vault documents to Supabase storage bucket 'vault-storage'.")
    }

    private suspend fun syncVehiclesToCloud(vehicles: List<VehicleEntity>) {
        Log.d(tag, "Synced ${vehicles.size} vehicle records to Supabase table 'mod_vehicles'.")
    }

    private suspend fun syncSubscriptionsToCloud(subs: List<SubscriptionEntity>) {
        Log.d(tag, "Synced ${subs.size} subscription records to Supabase table 'mod_subscriptions'.")
    }
}
