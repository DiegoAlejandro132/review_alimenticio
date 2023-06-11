package com.example.myapplication.view.restaurante.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.model.model.Restaurante

class RestauranteAdapter(var list : ArrayList<Restaurante>) : RecyclerView.Adapter<RestauranteAdapter.ViewHolder>(){

    private lateinit var listener : OnItemClickListenerRestaurante

    interface OnItemClickListenerRestaurante{
        fun editarRestaurante(position: Int)
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
        holder.nome.text = list[position].nome
        holder.nota.text = list[position].nota.toString()
        holder.localizacao.text = list[position].localizacao
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val nome = itemView.findViewById<TextView>(R.id.txt_nome)
        val nota = itemView.findViewById<TextView>(R.id.txt_nota)
        val localizacao = itemView.findViewById<TextView>(R.id.txt_localizacao)
        val editar = itemView.findViewById<ImageButton>(R.id.btn_editar_restaurante)

        init {
            editar.setOnClickListener {
                listener.editarRestaurante(adapterPosition)
            }
        }
    }
}