package com.example.myapplication.view.restaurante

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRestauranteBinding
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.service.RestauranteService
import com.example.myapplication.view.restaurante.adapter.RestauranteAdapter
import com.example.myapplication.view.restaurante.fragment.VisualizarRestauranteFragment
import com.example.myapplication.view.review.ReviewActivity

class RestauranteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestauranteBinding
    private lateinit var restaurantesAdapter : RestauranteAdapter
    private var restauranteList = ArrayList<Restaurante>()
    private var restauranteService = RestauranteService(this)
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestauranteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
        setRecyclerRestaurantes()
        setNavDrawer()
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
            override fun editar(position: Int) {
                dialogRestaurante(restauranteList[position], position)
            }

            override fun remover(position: Int) {
                dialogRemoverRestaurante(restauranteList[position])
            }

            override fun visualizar(position: Int) {
                visualizarRetaurante(restauranteList[position])
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

    private fun setNavDrawer(){
        val drawerLayout = binding.drawerLayoutRestaurantes
        val navigationView = binding.navViewRestaurantes
        val menuItem = navigationView.menu.findItem(R.id.nav_menu_restaurante)
        menuItem.isChecked = true
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            val id = menuItem.itemId
            when (id) {
                R.id.nav_menu_review -> irReviewActivity()
            }
            true
        }

    }

    override fun onOptionsItemSelected(item : MenuItem) : Boolean{

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun irReviewActivity(){
        val intent = Intent(this, ReviewActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun visualizarRetaurante(restaurante: Restaurante){
        val visualizarRestauranteFragment = VisualizarRestauranteFragment(this, restaurante)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_restaurante, visualizarRestauranteFragment)
            addToBackStack(null)
            commit()
        }
    }

}