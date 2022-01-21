package ru.ha_inc.bedcook.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityMapBinding
import ru.ha_inc.bedcook.databinding.ActivityProfileBinding
import ru.ha_inc.bedcook.order.OrderActivity

class MapActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMapBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
//            mMap = googleMap

            // Add a marker in Sydney and move the camera
            val sydney = LatLng(-34.0, 151.0)
            it.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            it.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }

        binding.order1.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.order2.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
        binding.order3.setOnClickListener {
            startActivity(Intent(this, OrderActivity::class.java))
        }
    }
}