package com.example.turistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley


class CustomAdapter3 (private val nombre: Array<String?>,private val precio: Array<String?>, private val imagenes: Array<String?>, private val descripcion: Array<String?>,private val context:Context?): RecyclerView.Adapter<CustomAdapter3.ViewHolder>(){


    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun OnItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.servicio_layout, viewGroup, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.item_nombre.text= nombre[i]
        viewHolder.item_precio.text=precio[i]
        viewHolder.item_descripcion.text=descripcion[i]
        val requestQueue= Volley.newRequestQueue(context)
        val URL="http://192.168.0.20:4500/menu/get_imagen/"+imagenes[i];
        val respuestaConsulta = ImageRequest(
            URL,
            { response ->
                viewHolder.item_Imagen.setImageBitmap(response)
            }, 0, 0, null, null
        ) {
        }
        requestQueue.add(respuestaConsulta)
    }

    override fun getItemCount(): Int {
        return nombre.size
    }

    inner class ViewHolder(itemView:View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        var item_Imagen: ImageView
        var item_nombre: TextView
        var item_precio: TextView
        var item_descripcion: TextView
        init {
            item_Imagen=itemView.findViewById(R.id.imagen_producto)
            item_nombre=itemView.findViewById(R.id.tv_nombre_producto)
            item_precio=itemView.findViewById(R.id.tv_precioproducto)
            item_descripcion=itemView.findViewById(R.id.tv_descripcionproducto)
            itemView.setOnClickListener{
                listener.OnItemClick(adapterPosition)
            }
        }
    }
}