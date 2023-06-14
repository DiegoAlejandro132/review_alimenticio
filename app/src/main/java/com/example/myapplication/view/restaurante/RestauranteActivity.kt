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
import com.example.myapplication.service.RestauranteService
import com.example.myapplication.view.restaurante.adapter.RestauranteAdapter

class RestauranteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestauranteBinding
    private lateinit var restaurantesAdapter : RestauranteAdapter
    private var restauranteList = ArrayList<Restaurante>()
    private var restauranteService = RestauranteService(this)

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

    private fun dialogRemoverRestaurante(restaurante: Restaurante?){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_remover_restaurante)

        val texto = dialog.findViewById<TextView>(R.id.label_remover_restaurante)
        val btnDeletar = dialog.findViewById<Button>(R.id.btn_remover_restaurante)
        val btnFecharDialog = dialog.findViewById<ImageButton>(R.id.btn_fechar_dialog)

        texto.text = "${texto.text} \n${restaurante?.nome?.toUpperCase()}?"

        btnDeletar.setOnClickListener {
            val deletou = restauranteService.removeRestaurante(restaurante!!.id!!)
            if(deletou > 0){
                setRecyclerRestaurantes()
            }else{
                Toast.makeText(this, "Houve um erro ao remover o restaurante. Tente mais tarde", Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }

        btnFecharDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setRecyclerRestaurantes(){
        restauranteList = restauranteService.getRestaurante()
        val recycler = binding.recyclerRestaurantes
        restaurantesAdapter = RestauranteAdapter(restauranteList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = restaurantesAdapter

        restaurantesAdapter.setOnItemClickListener(object : RestauranteAdapter.OnItemClickListenerRestaurante{
            override fun editarRestaurante(position: Int) {
                dialogRestaurante(restauranteList[position], position)
            }

            override fun removerRestaurante(position: Int) {
                dialogRemoverRestaurante(restauranteList[position])
            }

        })
    }

    private fun criarRestaurante(dialog: Dialog){
        val nome = dialog.findViewById<EditText>(R.id.txt_nome)
        val restaurante = Restaurante(nome = nome.text.toString(), nota = 0.0)
        val criou = restauranteService.createRestaurante(restaurante)
        if(criou > 0){
            setRecyclerRestaurantes()
        }else{
            Toast.makeText(this, "Houve um erro ao criar o restaurante. Tente mais tarde", Toast.LENGTH_LONG).show()
        }
        dialog.dismiss()
    }

    private fun editarRestaurante(dialog: Dialog, position: Int){
        val nome = dialog.findViewById<EditText>(R.id.txt_nome)
        val nota = 4.2
        val restaurante = Restaurante(restauranteList[position].id, nome.text.toString(),nota)
        val alterou = restauranteService.updateRestaurante(restaurante)
        if(alterou > 0){
            setRecyclerRestaurantes()
        }else{
            Toast.makeText(this, "Houve um erro ao atualizar o restaurante. Tente mais tarde", Toast.LENGTH_LONG).show()
        }
        dialog.dismiss()
    }

    private fun removerRestaurante(id: Int){
        restauranteService.removeRestaurante(id)
    }

}