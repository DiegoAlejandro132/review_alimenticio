package com.example.myapplication.service

import android.content.Context
import android.util.Log
import com.example.myapplication.model.controller.Controller
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review

class ReviewService(context: Context) {

    private val controller = Controller(context)
    private val restauranteService = RestauranteService(context)

    fun createReview(review: Review): Long {
        if(review.idRestaurante != null){
            val restaurante = restauranteService.getRestauranteById(review.idRestaurante!!)
            if(restaurante != null){
                val notasRestaurante = restauranteService.getMediaAtualByRestaurante(restaurante.id!!)
                val notaNova = (notasRestaurante.totalNotas + review.nota)/(notasRestaurante.qtdNotas + 1)
                val restauranteNovo = Restaurante(restaurante.id, restaurante.nome, notaNova.toDouble())
                val alterou = restauranteService.updateRestaurante(restauranteNovo)
                if(alterou <= 0){
                    Log.e("reviewService.createReview ", "Nota do restaurante nao alterada")
                }
            }
        }
        return controller.createReview(review)
    }

    fun getReview(): ArrayList<Review> {
        return controller.getReview()
    }

    fun removeReview(id: Int): Int {
        return controller.removeReview(id)
    }

    fun updateReview(review: Review): Int {
        return controller.updateReview(review)
    }
}