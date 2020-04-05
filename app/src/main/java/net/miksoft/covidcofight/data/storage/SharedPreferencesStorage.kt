package net.miksoft.covidcofight.data.storage

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object SharedPreferencesStorage {

    private const val PREFS_NAME = "covid_guard_prefs"
    private const val ENCRYPTED_PREFS_NAME = "encrypted_$PREFS_NAME"

    private lateinit var applicationContext: Context

    private val sharedPrefs by lazy {
        applicationContext.getSharedPreferences(
            PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val encryptedSharedPrefs by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            ENCRYPTED_PREFS_NAME,
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun init(applicationContext: Context) {
        this.applicationContext = applicationContext
    }

    fun put(key: String, value: String, encrypted: Boolean = false) {
        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
        with(prefs.edit()) {
            putString(key, value)
            commit()
        }
    }

    fun get(key: String, default: String? = null, encrypted: Boolean = false): String? {
        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
        return prefs.getString(key, default)
    }

    fun remove(key: String, encrypted: Boolean = false) {
        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
        prefs.edit().remove(key).apply()
    }

    fun put(key: String, value: Boolean, encrypted: Boolean = false) {
        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
        with(prefs.edit()) {
            putBoolean(key, value)
            commit()
        }
    }

    fun get(key: String, default: Boolean = false, encrypted: Boolean = false): Boolean {
        val prefs = if (encrypted) sharedPrefs else encryptedSharedPrefs
        return prefs.getBoolean(key, default)
    }
}
