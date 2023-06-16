package com.example.myapplication.view.restaurante.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVisualizarRestauranteBinding
import com.example.myapplication.model.model.Imagem
import com.example.myapplication.model.model.Review
import com.example.myapplication.model.service.ImagemService
import com.example.myapplication.model.service.ReviewService
import com.example.myapplication.view.restaurante.adapter.ReviewAdapter

class VisualizarRestauranteFragment(var contexto: Context, var idRestaurante : Int) : Fragment(R.layout.fragment_visualizar_restaurante) {

    private lateinit var binding : FragmentVisualizarRestauranteBinding
    private var reviewList = ArrayList<Review>()
    private var imagemList = ArrayList<Imagem>()
    private lateinit var adapter : ReviewAdapter
    private val reviewService = ReviewService(contexto)
    private val imagemService = ImagemService(contexto)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentVisualizarRestauranteBinding.bind(view)

        setRecyclerReview()
        setClickListeners()
    }

    private fun setClickListeners(){
        binding.btnFecharFragmentVisualizarRestaurante.setOnClickListener {
            fecharFragment()
        }
    }

    private fun fecharFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun setRecyclerReview(){
        reviewList = reviewService.getReviewByRestaurante(idRestaurante)
        imagemList = imagemService.getImagensByRestaurante(idRestaurante)
        val recycler = binding.recyclerReviews
        adapter = ReviewAdapter(contexto, reviewList, imagemList)
        recycler.layoutManager = LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter
    }

}