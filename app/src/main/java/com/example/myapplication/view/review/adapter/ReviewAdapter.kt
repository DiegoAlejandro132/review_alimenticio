package com.example.myapplication.view.review.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.model.Review
import com.example.myapplication.util.Util
import com.example.myapplication.view.restaurante.adapter.RestauranteAdapter

class ReviewAdapter(var list: ArrayList<Review>): RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    private lateinit var listener : OnItemClickListenerReview

    interface OnItemClickListenerReview{
        fun visualizar(position: Int)
        fun remover(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListenerReview){
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_review, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nome.text = if(list[position].nome == "") "Sem nome" else list[position].nome
        holder.data.text = Util().getDataHoraFromLong(list[position].data)
        holder.nota.text = list[position].nota.toString()
        holder.restaurante.text = if(list[position].nomeRestaurante == null) "Sem restaurante" else  list[position].nomeRestaurante
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var nota = itemView.findViewById<TextView>(R.id.txt_nota)
        var nome = itemView.findViewById<TextView>(R.id.txt_nome_review)
        var restaurante = itemView.findViewById<TextView>(R.id.txt_restaurante)
        var data = itemView.findViewById<TextView>(R.id.txt_data)
        var btnRemover = itemView.findViewById<ImageButton>(R.id.btn_remover_review)
        val card = itemView.findViewById<CardView>(R.id.card_review)

        init {
            btnRemover.setOnClickListener {
                listener.remover(adapterPosition)
            }

            card.setOnClickListener {
                listener.visualizar(adapterPosition)
            }
        }
    }
}