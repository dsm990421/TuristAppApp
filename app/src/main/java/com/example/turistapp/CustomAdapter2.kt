package com.example.turistapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley

class CustomAdapter2: RecyclerView.Adapter<CustomAdapter2.ViewHolder>() {

    val titles = arrayOf("Tlalmanalco",
        "San Rafael",
        "Tlalmanalco Centro")

    val details= arrayOf("Pueblo con Encanto",
        "Ponte en Contacto con la Naturaleza",
        "Un emblema del municipio",
        "Vistas increibles")

    val images = intArrayOf(R.drawable.centro,
        R.drawable.sanrafa,
        R.drawable.tlalma)

    private lateinit var mListener:onItemClickListener

    interface onItemClickListener{
        fun OnItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener=listener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_layout2, viewGroup, false)
        return ViewHolder(v, mListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.item_Tilte.text= titles[i]
        viewHolder.item_Detail.text=details[i]
        viewHolder.item_Imagen.setImageResource(images[i])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

    inner class ViewHolder(itemView: View, listener: onItemClickListener):RecyclerView.ViewHolder(itemView){
        var item_Imagen: ImageView
        var item_Tilte: TextView
        var item_Detail: TextView

        init {
            item_Imagen=itemView.findViewById(R.id.item_Imagen2)
            item_Tilte=itemView.findViewById(R.id.item_Title2)
            item_Detail=itemView.findViewById(R.id.item_Detail2)

            itemView.setOnClickListener{
                listener.OnItemClick(adapterPosition)
            }
        }
    }
}