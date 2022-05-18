package com.subhambikash.jetpack_datastore

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.subhambikash.jetpack_datastore.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //forNowStaticKey
    val keyValue = "staticKey"

    // At the top level of your kotlin file:
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.getValue.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val data = readData(keyValue)
                runOnUiThread {
                    binding.fetchedValue.text = data
                }
            }

        }

        binding.save.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                writeData(keyValue, binding.value.text.toString().trim())
            }
        }
    }

    private suspend fun readData(key: String?): String? {
        val dataStoreKey = stringPreferencesKey(key ?: "noKey")
        return dataStore.data.first()[dataStoreKey]
    }

    suspend fun writeData(key: String?, value: String?) {
        val dataStoreKey = stringPreferencesKey(key ?: "noKey")
        if (value.isNullOrEmpty()) {
            MainScope().launch {
                Toast.makeText(this@MainActivity, "Enter the value First", Toast.LENGTH_SHORT).show()
            }

        } else {
            dataStore.edit { setSettings -> setSettings[dataStoreKey] = value }
            MainScope().launch {
                Toast.makeText(this@MainActivity, "Saved", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
