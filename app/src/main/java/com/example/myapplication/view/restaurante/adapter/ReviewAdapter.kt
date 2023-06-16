package com.example.myapplication.view.restaurante.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.model.model.Imagem
import com.example.myapplication.model.model.Review
import com.example.myapplication.util.Util

class ReviewAdapter(var context : Context, var reviewList : ArrayList<Review>, var imagemList : ArrayList<Imagem>) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_review_por_restaurante, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nome.text = reviewList[position].nome
        holder.nota.text = reviewList[position].nota.toString()
        holder.rating.rating = reviewList[position].nota
        //Problema: nao estive conseguindo fazer a imagem aparecer, provavelmente por erro de caminho
        //como nao consegui resolver esse problema a tempo e iso ja estava me consumindo muito tempo, decidi continuar com o resto
        for (imagem in imagemList){
            if(imagem.idReview == reviewList[position].id){
                holder.imagem.setImageBitmap(Util().uriToBitmap(context, imagem.caminho.toUri()))
            }
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imagem = itemView.findViewById<ImageView>(R.id.img_review)
        val nome = itemView.findViewById<TextView>(R.id.txt_nome_review)
        val nota = itemView.findViewById<TextView>(R.id.txt_nota)
        val rating = itemView.findViewById<RatingBar>(R.id.rating_nota)
    }
}