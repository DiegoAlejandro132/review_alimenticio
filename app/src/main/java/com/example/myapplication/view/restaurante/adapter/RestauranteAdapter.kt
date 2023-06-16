package com.example.myapplication.view.restaurante.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.util.Util

class RestauranteAdapter(var list : ArrayList<Restaurante>) : RecyclerView.Adapter<RestauranteAdapter.ViewHolder>(){

    private lateinit var listener : OnItemClickListenerRestaurante

    interface OnItemClickListenerRestaurante{
        fun editar(position: Int)
        fun remover(position: Int)
        fun visualizar(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListenerRestaurante){
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.model_item_list_restaurante, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nome.text = if(list[position].nome == "") "Sem nome" else list[position].nome
        holder.nota.text = Util().formatarFloat(list[position].nota)
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val nome = itemView.findViewById<TextView>(R.id.txt_nome)
        val nota = itemView.findViewById<TextView>(R.id.txt_nota)
        val excluir = itemView.findViewById<ImageButton>(R.id.btn_remover_restaurante)
        val editar = itemView.findViewById<ImageButton>(R.id.btn_editar_restaurante)
        val card = itemView.findViewById<CardView>(R.id.card_restaurante)

        init {
            editar.setOnClickListener {
                listener.editar(adapterPosition)
            }

            excluir.setOnClickListener {
                listener.remover(adapterPosition)
            }

            card.setOnClickListener{
                listener.visualizar(adapterPosition)
            }
        }
    }
}