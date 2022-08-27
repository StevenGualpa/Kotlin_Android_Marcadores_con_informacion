package com.geek.tarea_marcadores

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener , GoogleMap.OnMapClickListener {

    private lateinit var map: GoogleMap
    var REQUEST_CODE_LOCATION = 0
    var puntos: ArrayList<LatLng> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MuestraMapa()
    }

    private fun MuestraMapa() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun DescargarMarcadores()
    {
        val queue = Volley.newRequestQueue(this)
        val url: String = "https://my-json-server.typicode.com/StevenGualpa/Api_Uteq_MarketPlace/Edificios"

        // Request a string response from the provided URL.
        val stringReq = StringRequest(
            Request.Method.GET, url,
            { response ->
                var strResp = response.toString()
                var str: JSONArray = JSONArray(strResp)

                //Contador
                var index=0
                //Cantidad de Elementos
                var n=str.length()
                //Variables auxiliares  que usaremos

                //MensajeLargo(n.toString())
                while (index<n) {
                    var elemento: JSONObject = str.getJSONObject(index)

                    var parametros: String=""
                    parametros=elemento.getString("Descripcion")+"?"+
                            elemento.getString("UrlImage")+"?"+
                            elemento.getString("Facultad")+"?"+
                            elemento.getString("Decano")+"?"+
                            elemento.getString("Ubicacion")
                    val melbourneLatLng = LatLng(elemento.getDouble("Latitude"), elemento.getDouble("Longitude"))
                    val melbourne = map.addMarker(
                        MarkerOptions()
                            .position(melbourneLatLng)
                            .title(elemento.getString("Titulo")).snippet(parametros)


                    )



                    melbourne?.showInfoWindow()
                    index++

                }
            },
            { Log.d("API", "that didn't work") })
        queue.add(stringReq)
    }


    //Funcion Para Agregar Marcador
    private fun CreaMarcador() {
     /*
        val coordenadas = LatLng(-1.0803351324691082, -79.50145350501472)
        val marcador: MarkerOptions = MarkerOptions().position(coordenadas).title("UTEQ")
        marcador.snippet("")
        map.addMarker(marcador)
        //Animacion para Dirigir la camara hacia el marcador
      */
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-1.0803351324691082, -79.50145350501472), 18f), 100, null)
        DescargarMarcadores()
    }

    fun MensajeLargo(Mensaje: String) {
        Toast.makeText(this, Mensaje.toString(), Toast.LENGTH_LONG).show()

    }


    override fun onMarkerClick(p0: Marker): Boolean {
        return false

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        CreaMarcador()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        map.setOnMapClickListener(this)
        map.setOnMarkerClickListener(this)

        enableLocation()
        map.setInfoWindowAdapter(InfoWindowAdapter(this))
    }

    override fun onMyLocationButtonClick(): Boolean {
        MensajeLargo("Boton Pulsado")
        return false   //False A mi Ubicacion true no hace nada
    }

    override fun onMyLocationClick(p0: Location) {
        MensajeLargo("Estas en ${p0.latitude},${p0.longitude}")
    }

    override fun onMapClick(p0: LatLng) {
      /*
        val coordenadas = LatLng(p0.latitude,p0.longitude)
        val marcador: MarkerOptions = MarkerOptions().position(coordenadas).title(puntos.size.toString())
        marcador.snippet("")
        map.addMarker(marcador)
        //Animacion para Dirigir la camara hacia el marcador
        // map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 18f), 5000, null)
        puntos.add(p0)
        if(puntos.size==4){
            var lineas: PolylineOptions = PolylineOptions()
            for(index:LatLng in puntos)
                lineas.add(index)
            lineas.add(puntos.get(0))
            map.addPolyline(lineas)
            puntos.clear()
        }

       */
        MensajeLargo("Estas en ${p0.latitude},${p0.longitude}")

    }


    //Gestion de Permisos

    private fun isLocatedPermissionGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocatedPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requesLocationPermission() //Pide el Permiso
        }

    }




    private fun requesLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            MensajeLargo("Acepta los Permisos")
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                MensajeLargo("Para Actucar la localizacion ve a ajustes y acepta los permisos")
            }
            else -> {}

        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isLocatedPermissionGranted()) {
            map.isMyLocationEnabled = false
            MensajeLargo("Para Activar la localizacion acepta los permisos")
        }
    }
    fun ChangeNORMAL(view: View?)
    {
        map.mapType=GoogleMap.MAP_TYPE_NORMAL
    }
    fun ChangeSATELLITE(view: View?)
    {
        map.mapType=GoogleMap.MAP_TYPE_SATELLITE
    }
    fun ChangeHYBRID(view: View?)
    {
        map.mapType=GoogleMap.MAP_TYPE_HYBRID
    }
    fun ChangeTERRAIN(view: View?)
    {
        map.mapType=GoogleMap.MAP_TYPE_TERRAIN
    }



}

