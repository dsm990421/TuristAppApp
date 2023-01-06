package com.example.turistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class CustomAdapter (private val descp: Array<String?>,private val tipo: Array<String?>,private val imagenes: Array<String?>,private val tittles: Array<String?>, private val details: Array<String?>, private val context:Context?): RecyclerView.Adapter<CustomAdapter.ViewHolder>(){


    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun OnItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_layout, viewGroup, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.item_tipe.text=tipo[i]
        viewHolder.item_Tilte.text= tittles[i]
        viewHolder.item_Detail.text=details[i]
        viewHolder.item_desc.text=descp[i]
        val requestQueue= Volley.newRequestQueue(context)
        val URL="http://192.168.0.20:4500/negocios/get_imagen/"+imagenes[i];
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
        return tittles.size
    }

    inner class ViewHolder(itemView:View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        var item_Imagen: ImageView
        var item_Tilte: TextView
        var item_Detail: TextView
        var item_tipe: TextView
        var item_desc:TextView
        init {
            item_Imagen=itemView.findViewById(R.id.item_Imagen)
            item_Tilte=itemView.findViewById(R.id.item_Title)
            item_Detail=itemView.findViewById(R.id.item_Detail)
            item_tipe=itemView.findViewById(R.id.item_tipe)
            item_desc=itemView.findViewById(R.id.item_desc)
            itemView.setOnClickListener{
                listener.OnItemClick(adapterPosition)
            }
        }
    }
}