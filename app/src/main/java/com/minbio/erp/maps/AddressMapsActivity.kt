package com.minbio.erp.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.minbio.erp.R
import com.minbio.erp.base.BaseActivity
import com.minbio.erp.utils.AppUtils
import com.minbio.erp.utils.Constants
import kotlinx.android.synthetic.main.activity_address_maps.*
import java.io.IOException
import java.util.*

class AddressMapsActivity : BaseActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    LocationListener {

    private var locationManager: LocationManager? = null
    private lateinit var mMap: GoogleMap
    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var autocompleteSupportFragment: AutocompleteSupportFragment

    private lateinit var accountLatLng: LatLng
    private var accountAddress: String = ""
    private var addressCountryName: String = ""
    private var addressCountryCode: String = ""

    private lateinit var btnSaveAddress: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_address_maps)
        Places.initialize(
            this@AddressMapsActivity,
            resources.getString(R.string.places_api_billing_key)
        )

        checkLocationPermission()

        val mapFragment = this.supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initViews()

    }

    private fun initViews() {

        val bundle = intent.extras
        accountLatLng = LatLng(bundle!!.getDouble("lat"), bundle.getDouble("lng"))
        accountAddress = bundle.getString("address")!!

        btnSaveAddress = findViewById(R.id.btn_save_address)
        btnSaveAddress.setOnClickListener {
            accountAddress = getAddress(accountLatLng.latitude, accountLatLng.longitude)!!

            val intent = Intent()
            intent.putExtra("lat", accountLatLng.latitude)
            intent.putExtra("lng", accountLatLng.longitude)
            intent.putExtra("address", accountAddress)
            intent.putExtra("countryName", addressCountryName)

            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        autocompleteSupportFragment =
            (supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?)!!

        autocompleteSupportFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME
            )
        )
        autocompleteSupportFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                hideKeyboard()
                accountLatLng = place.latLng!!
                val cameraUpdate =
                    CameraUpdateFactory.newLatLngZoom(accountLatLng, 15f)
                mMap.animateCamera(cameraUpdate)
                updateMarker()
                val thread: Thread = object : Thread() {
                    override fun run() {
                        getAddress(accountLatLng.latitude, accountLatLng.longitude)
                    }
                }
                thread.start()
            }

            override fun onError(status: Status) {
                Log.d(
                    "Error",
                    "onError: Auto Complete $status"
                )
            }
        })

        ivBack.setOnClickListener { finish() }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        mMap.uiSettings.isZoomControlsEnabled = false
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this@AddressMapsActivity,
                R.raw.maps_style
            )
        )

        mMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}
            override fun onMarkerDragEnd(marker: Marker) {
                getAddress(marker.position.latitude, marker.position.longitude)
                accountLatLng =
                    LatLng(marker.position.latitude, marker.position.longitude)
            }
        })

    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                Constants.LOCATION_PERMISSION_CODE
            )
        } else {
            getCurrentLocation()

            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build()
            googleApiClient.connect()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.LOCATION_PERMISSION_CODE) {

            if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleApiClient.connect()
                mMap.isMyLocationEnabled = true
                getCurrentLocation()
            } else {
                AppUtils.showToast(
                    this,
                    resources.getString(R.string.locationPermissionDenied),
                    false
                )
                Handler().postDelayed({ finish() }, 1800)
            }
        }
    }

    private fun updateMarker() {
        val markerOptions = MarkerOptions()
        markerOptions.position(accountLatLng)
        markerOptions.draggable(true)
        mMap.clear()
        mMap.addMarker(markerOptions)
    }

    private fun getAddress(latitude: Double, longitude: Double): String? {
        val address: Address
        val result = StringBuilder()
        try {
            println("get address")
            val geoCoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geoCoder.getFromLocation(latitude, longitude, 1)
            if (addresses.size > 0) {
                println("size====" + addresses.size)
                address = addresses[0]
                for (i in 0..addresses[0].maxAddressLineIndex) {
                    if (i == addresses[0].maxAddressLineIndex) {
                        result.append(addresses[0].getAddressLine(i))
                    } else {
                        result.append(addresses[0].getAddressLine(i) + ",")
                    }
                }
                println("address---$address")
                println("result---$result")
                println("country code---$address.countryCode")

                addressCountryName = address.countryName
                addressCountryCode = address.countryCode

                autocompleteSupportFragment.setText(result.toString()) // Here is you AutoCompleteTextView where you want to set your string address (You can remove it if you not need it)
                return result.toString()
            }
        } catch (e: IOException) {
            Log.e("tag", e.message!!)
        }
        return ""
    }

    override fun onConnected(p0: Bundle?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (accountAddress != "") {
                mMap.isMyLocationEnabled = true

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15f)
                mMap.animateCamera(cameraUpdate)

                getAddress(accountLatLng.latitude, accountLatLng.longitude)
                updateMarker()
            } else {
                mMap.isMyLocationEnabled = true
//                getCurrentLocation()
            }
        } else {
            AppUtils.showToast(
                this,
                resources.getString(R.string.pleaseGiveLocationPermission), false
            )
//            Handler().postDelayed({ finish() }, 1800)
        }

    }

    override fun onConnectionSuspended(p0: Int) {
        Log.d("Error", "onStatusChanged: $p0")
    }

    private fun getCurrentLocation() {

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager?
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            AppUtils.showToast(
                this,
                resources.getString(R.string.locationPermissionDenied),
                false
            )
//            Handler().postDelayed({ finish() }, 1800)
        } else
            locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0f, this)
    }

    override fun onLocationChanged(location: Location) {
        locationManager?.removeUpdates(this)

        if (accountAddress == "") {
            accountLatLng = LatLng(location.latitude, location.longitude)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(accountLatLng, 15f)
            mMap.animateCamera(cameraUpdate)
            getAddress(location.latitude, location.longitude)
            updateMarker()
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.d("Error", "onStatusChanged: $provider")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.d("Error", "onProviderEnabled: $provider")
    }

    override fun onProviderDisabled(provider: String?) {
        Log.d("Error", "onProviderDisabled: $provider")
        AppUtils.showToast(this, resources.getString(R.string.turnOnLocation), false)
    }

    override fun onStart() {
        super.onStart()
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(LocationServices.API)
            .build()
    }

    override fun onStop() {
        googleApiClient.disconnect()
        if (locationManager != null)
            locationManager?.removeUpdates(this)
        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        if (googleApiClient.isConnected) {
            googleApiClient.disconnect()
        }
    }

    override fun onResume() {
        super.onResume()
        if (googleApiClient != null)
            if (!googleApiClient.isConnected) {
                googleApiClient.connect()
            }
    }

    private fun hideKeyboard() {
        val inputManager: InputMethodManager = Objects.requireNonNull(this@AddressMapsActivity)
            ?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )

    }

}
