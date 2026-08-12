package com.example.util

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * AES-256 Encryption & Decryption Helper for Password Vault Security.
 * Encrypts passwords locally before storing in SQLite / Room or syncing to Cloud.
 */
object PasswordEncryptionHelper {

    private const val ALGORITHM = "AES/CBC/PKCS5Padding"
    private const val SECRET_KEY_SEED = "PALM_SECURE_VAULT_ENCRYPTION_KEY_2026"
    private val iv = ByteArray(16) { 0 } // Standard IV initialization

    private fun getSecretKey(): SecretKeySpec {
        val sha = MessageDigest.getInstance("SHA-256")
        val keyBytes = sha.digest(SECRET_KEY_SEED.toByteArray(Charsets.UTF_8))
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plain text password to AES-256 Base64 string.
     */
    fun encrypt(plainText: String): String {
        if (plainText.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = getSecretKey()
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            plainText
        }
    }

    /**
     * Decrypts AES-256 Base64 string back to plain text password.
     */
    fun decrypt(encryptedText: String): String {
        if (encryptedText.isEmpty()) return ""
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            val keySpec = getSecretKey()
            val ivSpec = IvParameterSpec(iv)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            val decodedBytes = Base64.decode(encryptedText, Base64.NO_WRAP)
            val plainBytes = cipher.doFinal(decodedBytes)
            String(plainBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            encryptedText
        }
    }
}
