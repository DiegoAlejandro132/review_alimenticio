package com.example.myapplication.view.review.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R

class ImagemAdapter(var list : ArrayList<Bitmap>) : RecyclerView.Adapter<ImagemAdapter.ViewHolder>() {

    private lateinit var listener : OnItemClickListenerImagem

    interface OnItemClickListenerImagem{
        fun removerImagem(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListenerImagem){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_imagem, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imagem.setImageBitmap(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imagem = itemView.findViewById<ImageView>(R.id.img_list_item)

        init {
            imagem.setOnClickListener {
                listener.removerImagem(adapterPosition)
            }
        }
    }
}