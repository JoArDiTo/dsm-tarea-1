package com.fisi.tarea1.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.fisi.tarea1.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.fisi.tarea1.api.PlaceResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RestaurantsFragment : Fragment(), OnMapReadyCallback {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var  myMap: GoogleMap;
    //get location
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var currentLocation: Location;
    private val permissionCode = 101;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //Permisos de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCurrentLocationUser()
    }

    private fun getCurrentLocationUser(){
        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            //Solicitar permisos
            ActivityCompat.requestPermissions( requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }


        //Permisos otorgados
        val getLocation = fusedLocationClient.lastLocation.addOnSuccessListener {
                location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    requireContext(),
                    "Obteniendo ubicación actual:" + location.latitude + " " + location.longitude.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                //Obtener mapa
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            } else {
                Toast.makeText(requireContext(), "GPS is off", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            permissionCode -> if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getCurrentLocationUser()
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyLocationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }


    override fun onMapReady(googleMap: GoogleMap) {
        myMap = googleMap

        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("Mi ubicación actual")

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
        googleMap.addMarker(markerOptions)
        fetchRestaurants()
    }

    private fun fetchRestaurants() {
        val url = "https://places.googleapis.com/v1/places:searchNearby"
        val apiKey = "AIzaSyAwx8zGqHdSapvNlshCza3xu-mMZWBAx5A" // Usa tu propia API Key
        val requestBody = """
            {
              "includedTypes": ["restaurant"],
              "maxResultCount": 5,
              "locationRestriction": {
                "circle": {
                  "center": {
                    "latitude": ${currentLocation.latitude},
                    "longitude": ${currentLocation.longitude}
                  },
                  "radius": 1000.0
                }
              }
            }
        """.trimIndent()

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .post(RequestBody.create("application/json".toMediaType(), requestBody))
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader("X-Goog-FieldMask", "places.displayName,places.formattedAddress,places.location")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                if (response.isSuccessful && responseData != null) {
                    activity?.runOnUiThread {
                        parseNearbyRestaurants(responseData)

                    }
                }
            }
        })
    }

    private fun parseNearbyRestaurants(jsonData: String) {
        try {
            // Deserializar el JSON a objetos usando Gson
            val placeResultType = object : TypeToken<PlaceResult>() {}.type
            val placeResult: PlaceResult = Gson().fromJson(jsonData, placeResultType)

            for (place in placeResult.places) {
                // Crear una LatLng para la ubicación del restaurante
                val latLng = LatLng(place.location.latitude, place.location.longitude)

                // Crear opciones de marcador y configurarlo
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("${place.displayName.text}: ${place.formattedAddress}")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))

                // Agregar el marcador al mapa
                myMap.addMarker(markerOptions)

                // Enfocar la cámara en el primer marcador
                if (placeResult.places.indexOf(place) == 0) {
                    myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
