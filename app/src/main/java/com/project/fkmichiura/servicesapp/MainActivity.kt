package com.project.fkmichiura.servicesapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner

class MainActivity : AppCompatActivity(){

    private var manager : LocationManager? = null

    private var categorySpinner: Spinner? = null
    private var category : EditText? = null

    private val REQUEST_IMAGE_CAPTURE = 1
    private val GPS_REQUEST_CODE = 0
    private val CAMERA_REQUEST_CODE = 1888

    private val TAG = "MainActivity.kt"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createCategorySpinner()

        val camera = findViewById<ImageView>(R.id.iv_camera)

        //Criar persistência de referência do LocationManager
        manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permissão de câmera negada")
            makeCameraRequest()
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=  PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "Permissão de localização negada")
            makeGPSRequest()
        }

        camera.setOnClickListener{
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i(TAG, "Camera permission has been denied by user")
                else
                    Log.i(TAG, "Camera permission has been granted by user")
            }
            GPS_REQUEST_CODE -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Log.i(TAG, "GPS permission has been denied by user")
                else
                    Log.i(TAG, "GPS permission has been granted by user")
            }
        }
    }

    //Associa e popula o Spinner de Categoria com conteúdo da lista de strings.xml
    fun createCategorySpinner(){

        categorySpinner = findViewById(R.id.spinner_category)
        categorySpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                category = findViewById(R.id.et_category)
                category!!.visibility = View.GONE

                if(p2 == 4){
                    category!!.visibility = View.VISIBLE
                }
                else{
                    category!!.visibility = View.GONE
                }
            }
        }
    }

    //Requisição de permissões para o Android M+ de GPS Fine Location e Coarse Location
    private fun makeGPSRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
            GPS_REQUEST_CODE)
    }

    //Requisição de permissões para o Android M+ de Câmera
    private fun makeCameraRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_REQUEST_CODE)
    }

    private val locationListener : LocationListener = object : LocationListener{
        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

        override fun onLocationChanged(p0: Location?) {
            Log.i("MainActivity.kt", "Latitude: " + (p0?.latitude))
            Log.i("MainActivity.kt", "Longitude: " + (p0?.longitude))
        }
    }
}
