package com.geek.tarea_marcadores

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.squareup.picasso.Picasso

class InfoWindowAdapter(context: Context) : GoogleMap.InfoWindowAdapter  {

    var mContext = context
    var mWindow = (context as Activity).layoutInflater.inflate(R.layout.layout_info_window, null)

    private fun rendowWindowText(marker: Marker, view: View){

        var parseo =marker.snippet.toString()
        val list: List<String> = listOf(*parseo.split("?").toTypedArray())


        val itemTitle = view.findViewById<TextView>(R.id.TxtTitulo)
        val itemDescripcion = view.findViewById<TextView>(R.id.TxtDescripcion)
        val itemFacultad = view.findViewById<TextView>(R.id.TxtFacultad)
        val itemDecano=view.findViewById<TextView>(R.id.TxtDecano)
        val itemUbicacion = view.findViewById<TextView>(R.id.TxtUbicacion)
        val itemcoodendas = view.findViewById<TextView>(R.id.TxtCoordenas)
        val itemimage = view.findViewById<ImageView>(R.id.imageEdificio)
        if(list.size>0)
        {
            itemTitle.text = marker.title
            itemDescripcion.text = "Descripcion: "+list?.get(0)
            itemFacultad.text ="Facultad: "+ list?.get(2)
            itemDecano.text="Decano: "+list?.get(3)
            itemUbicacion.text= "Ubicacion: "+list?.get(4)
            itemcoodendas.text = "Coordenads: latitude:"+marker.position.latitude.toString()+", longitude:"+marker.position.longitude.toString()

            //Picasso.get().load("https://www.uteq.edu.ec/images/about/logo_fci.jpg").into(itemimage);
            Picasso.get().load(list?.get(1)).into(itemimage)

        }
    }

    override fun getInfoContents(p0: Marker): View? {
        rendowWindowText(p0, mWindow)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker): View? {
        try {
            rendowWindowText(p0, mWindow)
            return mWindow

        }
        catch (e: IllegalStateException) {
            return null
        }

    }
}