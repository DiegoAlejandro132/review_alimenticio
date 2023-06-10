package com.example.myapplication.view.restaurante

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRestauranteBinding
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.view.restaurante.adapter.RestauranteAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RestauranteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestauranteBinding
    private lateinit var restaurantesAdapter : RestauranteAdapter
    private var restauranteList = ArrayList<Restaurante>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestauranteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
        setRecyclerRestaurantes()
    }

    private fun setClickListeners(){
        binding.btnAddRestaurante.setOnClickListener {
            dialogRestaurante()
        }
    }

    private fun dialogRestaurante(restaurante: Restaurante? = null, position: Int? = null){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_restaurante)

        val nomeEditText = dialog.findViewById<TextView>(R.id.txt_nome)
        val btnFecharDialog = dialog.findViewById<ImageButton>(R.id.btn_fechar_dialog)
        val btnSalvarRestaurante = dialog.findViewById<Button>(R.id.btn_criar_restaurante)

        if(restaurante != null){
            nomeEditText.text = restaurante.nome
        }

        btnFecharDialog.setOnClickListener {
            dialog.dismiss()
        }

        btnSalvarRestaurante.setOnClickListener {
            if ((restaurante != null) && (position != null))  editarRestaurante(dialog, position) else criarRestaurante(dialog)
        }

        dialog.show()
    }

    private fun setRecyclerRestaurantes(){
        val recycler = binding.recyclerRestaurantes
        restaurantesAdapter = RestauranteAdapter(restauranteList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = restaurantesAdapter

        restaurantesAdapter.setOnItemClickListener(object : RestauranteAdapter.OnItemClickListenerRestaurante{
            override fun editarRestaurante(position: Int) {
                dialogRestaurante(restauranteList[position], position)
            }

        })
    }

    private fun criarRestaurante(dialog: Dialog){
        val nome = dialog.findViewById<EditText>(R.id.txt_nome)
        val nota = 4.5
        val restaurante = Restaurante(nome = nome.text.toString(), localizacao = "localizacao", nota = nota)
        restauranteList.add(restaurante)
        restaurantesAdapter.notifyDataSetChanged()
        dialog.dismiss()
    }

    private fun editarRestaurante(dialog: Dialog, position: Int){
        val nome = dialog.findViewById<EditText>(R.id.txt_nome)
        val nota = 4.2
        val restaurante = Restaurante(nome = nome.text.toString(), localizacao = "localizacao", nota = nota)
        restauranteList[position] = restaurante
        restaurantesAdapter.notifyItemChanged(position)
        dialog.dismiss()
    }


}