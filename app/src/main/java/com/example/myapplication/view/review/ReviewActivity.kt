package com.example.myapplication.view.review

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityReviewBinding
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review
import com.example.myapplication.model.service.RestauranteService
import com.example.myapplication.model.service.ReviewService
import com.example.myapplication.view.restaurante.RestauranteActivity
import com.example.myapplication.view.review.adapter.ReviewAdapter
import com.example.myapplication.view.review.fragment.CriarReviewFragment
import com.google.android.material.navigation.NavigationView

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding
    private val restauranteService = RestauranteService(this)
    private val reviewService = ReviewService(this)
    private var restauranteList = ArrayList<Restaurante>()
    private var reviewList = ArrayList<Review>()
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var toggle : ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
        getRestaurantes()
        setRecyclerReview()
        setAtualizarTela()
        setNavDrawer()
    }

    private fun setClickListeners(){
        binding.btnAddReview.setOnClickListener {
            irCriarReview()
        }
    }

    private fun setRecyclerReview(){
        val recycler = binding.recyclerReviews
        reviewList = reviewService.getReview()
        reviewAdapter = ReviewAdapter(reviewList)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = reviewAdapter

        reviewAdapter.setOnItemClickListener(object : ReviewAdapter.OnItemClickListenerReview{
            override fun editarReview(position: Int) {
                TODO("Not yet implemented")
            }

            override fun removerReview(position: Int) {
                dialogRemoverReview(reviewList[position].id!!)
            }

        })
    }

    private fun irCriarReview(){
        val criarReviewFragment = CriarReviewFragment(this, restauranteList)
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_review, criarReviewFragment)
            addToBackStack(null)
            commit()
        }
    }

    private fun getRestaurantes(){
        restauranteList = restauranteService.getRestaurante()
    }

    private fun setAtualizarTela(){
        binding.refreshLayout.setOnRefreshListener {
            setRecyclerReview()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun dialogRemoverReview(id: Int){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_remover_review)

        val btnFecharDialog = dialog.findViewById<ImageButton>(R.id.btn_fechar_dialog)
        val btnRemover = dialog.findViewById<Button>(R.id.btn_remover_review)

        btnRemover.setOnClickListener {
            val removeu = reviewService.removeReview(id)
            if (removeu > 0){
                setRecyclerReview()
                dialog.dismiss()
            }else{
                Toast.makeText(this, "Não foi possível remover o review. Tente mais tarde", Toast.LENGTH_SHORT).show()
            }
        }

        btnFecharDialog.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setNavDrawer(){
        val drawerLayout = binding.drawerLayoutReviews
        val navigationView = binding.navViewReviews
        val menuItem = navigationView.menu.findItem(R.id.nav_menu_review)
        menuItem.isChecked = true
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawer(GravityCompat.START)
            val id = menuItem.itemId
            when (id) {
                R.id.nav_menu_restaurante -> irRestauranteActivity()
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

    private fun irRestauranteActivity(){
        val intent = Intent(this, RestauranteActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}