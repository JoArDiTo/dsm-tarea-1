package com.fisi.tarea1.fragments

import android.content.IntentSender
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
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import com.google.android.gms.location.LocationServices
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyLocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyLocationFragment : Fragment(), OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var  myMap: GoogleMap;
    //get location
    private lateinit var fusedLocationClient: FusedLocationProviderClient;
    private lateinit var currentLocation: Location;
    private val permissionCode = 101;

/*
    //Prueba de ubicación activada
    private lateinit var locationRequest: LocationRequest;
    private lateinit var locationSettingsRequest: LocationSettingsRequest;
    private val REQUEST_CHECK_SETTINGS = 1001
*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        //Permisos de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        /*
        //Elementos para verificar si ubicacion está activada
        // Crear la solicitud de ubicación
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(1000)
            .build()

        // Verificar configuración de ubicación
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        locationSettingsRequest = builder.build() */

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_location, container, false)
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

        /*
        // Inicializar el SettingsClient para verificar si la ubicación está activada
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        settingsClient.checkLocationSettings(locationSettingsRequest)
            .addOnSuccessListener {
                // Todo está habilitado

                //Permisos otorgados
                val getLocation = fusedLocationClient.lastLocation.addOnSuccessListener {
                        location ->
                    if(location != null){
                        currentLocation = location
                        Toast.makeText(requireContext(),
                            "Obteniendo ubicación actual:" + location.latitude + " " + location.longitude.toString(),
                            Toast.LENGTH_SHORT).show()

                        //Obtener mapa
                        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                        mapFragment?.getMapAsync(this)
                    }else{
                        Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual.", Toast.LENGTH_SHORT).show()
                    }
                }
                    .addOnFailureListener { e ->
                        val statusCode = (e as ResolvableApiException).statusCode
                        if(statusCode == LocationSettingsStatusCodes.REMOTE_EXCEPTION){
                            try{
                                Toast.makeText(requireContext(), "GPS is off, dispositivo no pudo obtener ubicación", Toast.LENGTH_SHORT).show()
                                e.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                            }catch(sendEx: IntentSender.SendIntentException){}
                        }
                }
            } */
    }

    /*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CHECK_SETTINGS){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(requireContext(), "GPS is turned on", Toast.LENGTH_SHORT).show()
                getCurrentLocationUser()
            }else{
                Toast.makeText(requireContext(), "GPS request was declined", Toast.LENGTH_SHORT).show()
            }
        }
    }*/

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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyLocation.
         */
        // TODO: Rename and change types and number of parameters
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
    }

}