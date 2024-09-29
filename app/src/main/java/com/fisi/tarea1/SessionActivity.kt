package com.fisi.tarea1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.fisi.tarea1.R
import com.fisi.tarea1.databinding.ActivitySessionBinding
import com.fisi.tarea1.fragments.MyLocationFragment
import com.fisi.tarea1.fragments.RestaurantsFragment
import com.fisi.tarea1.fragments.SelectLocationsFragment

class SessionActivity : AppCompatActivity() {

    lateinit var binding: ActivitySessionBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().add(R.id.frameContainer, MyLocationFragment() ).commit()

        binding.bottomMenu.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mylocation -> supportFragmentManager.beginTransaction().replace(R.id.frameContainer, MyLocationFragment()).commit()
                R.id.selectlocation -> supportFragmentManager.beginTransaction().replace(R.id.frameContainer, SelectLocationsFragment()).commit()
                R.id.restaurants -> supportFragmentManager.beginTransaction().replace(R.id.frameContainer, RestaurantsFragment()).commit()
            }
            true
        }
    }

}