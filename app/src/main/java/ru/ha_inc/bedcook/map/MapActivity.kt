package ru.ha_inc.bedcook.map

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ru.ha_inc.bedcook.R
import ru.ha_inc.bedcook.databinding.ActivityMapBinding
import ru.ha_inc.bedcook.models.Complexity
import ru.ha_inc.bedcook.models.Order
import ru.ha_inc.bedcook.models.OrderDataSource
import ru.ha_inc.bedcook.order.OrderActivity
import ru.ha_inc.bedcook.order.OrderViewModel

class MapActivity : AppCompatActivity() {

    private val viewModel by viewModels<MapViewModel>()
    private var soundPool: SoundPool? = null
    private val soundId = 1
    private var map: GoogleMap? = null

    private val locationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showMyLocationBtn()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool?.load(baseContext, R.raw.btn, 1)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            map = it

            showMyLocationBtn()
            val tomsk = LatLng(56.4691454, 84.9729507)
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(tomsk, 13.25f))

            viewModel.getOrders().forEach { order ->
                it.addMarker(
                    MarkerOptions().position(LatLng(order.lat, order.lng))
                        .title(order.cake.name).icon(
                            BitmapDescriptorFactory.fromResource(
                                when (order.complexity) {
                                    Complexity.EASY -> R.drawable.img_order_easy
                                    Complexity.NORMAL -> R.drawable.img_order_normal
                                    Complexity.HARD -> R.drawable.img_order_hard
                                }
                            )
                        )
                )?.tag = order
            }
            it.setOnMarkerClickListener {
                soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
                startActivity(Intent(this, OrderActivity::class.java).apply {
                    putExtra(OrderActivity.EXTRA_ORDER, it.tag as Order)
                })

                true
            }
        }
    }

    private fun showMyLocationBtn() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermission.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        } else {
            map?.isMyLocationEnabled = true
        }
    }
}