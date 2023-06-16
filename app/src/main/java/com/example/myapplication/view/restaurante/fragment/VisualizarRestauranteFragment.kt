package com.example.myapplication.view.restaurante.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentVisualizarRestauranteBinding
import com.example.myapplication.model.model.Imagem
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review
import com.example.myapplication.model.service.ImagemService
import com.example.myapplication.model.service.ReviewService
import com.example.myapplication.view.restaurante.adapter.ReviewAdapter
import com.google.android.gms.maps.MapView

class VisualizarRestauranteFragment(var contexto: Context, var restaurante : Restaurante) : Fragment(R.layout.fragment_visualizar_restaurante) {

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
        binding.txtNomeRestaurante.text = restaurante.nome

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
        reviewList = reviewService.getReviewByRestaurante(restaurante.id!!)
        imagemList = imagemService.getImagensByRestaurante(restaurante.id!!)
        val recycler = binding.recyclerReviews
        adapter = ReviewAdapter(contexto, reviewList, imagemList)
        recycler.layoutManager = LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false)
        recycler.adapter = adapter

        adapter.setOnItemClickListener(object : ReviewAdapter.OnItemClickListenerReviewPorRestaurante{
            override fun verNoMaps(position: Int) {
                val latitude = reviewList[position].latitude
                val longitude = reviewList[position].longitude
                val uriMaps = Uri.parse("geo:$latitude,$longitude")
                val mapIntent = Intent(Intent.ACTION_VIEW, uriMaps)
                mapIntent.setPackage("com.google.android.apps.maps")

                if (mapIntent.resolveActivity(context!!.packageManager) != null) {
                    context!!.startActivity(mapIntent)
                } else {
                    Toast.makeText(context, "Aplicativo do Google Maps não encontrado", Toast.LENGTH_SHORT).show()
                }

            }

        })
    }

}