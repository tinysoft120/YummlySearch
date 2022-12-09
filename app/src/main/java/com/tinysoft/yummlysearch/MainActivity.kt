package com.tinysoft.yummlysearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tinysoft.yummlysearch.databinding.ActivityMainBinding
import com.tinysoft.yummlysearch.ui.search.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SearchFragment.newInstance())
                .commitNow()
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}