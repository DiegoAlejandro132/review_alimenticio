package com.example.myapplication.model.service

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.myapplication.model.controller.Controller
import com.example.myapplication.model.model.HistoricoNota
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review

class ReviewService(var context: Context) {

    private val controller = Controller(context)
    private val restauranteService = RestauranteService(context)

    fun createReview(review: Review): Long {
        if(review.idRestaurante != null){
            val restaurante = restauranteService.getRestauranteById(review.idRestaurante!!)
            if(restaurante != null){
                val notasRestaurante = restauranteService.getMediaAtualByRestaurante(restaurante.id!!)
                val notaNova = (notasRestaurante.totalNotas + review.nota)/(notasRestaurante.qtdNotas + 1)
                val restauranteNovo = Restaurante(restaurante.id, restaurante.nome, notaNova)
                val alterou = restauranteService.updateRestaurante(restauranteNovo)
                if(alterou <= 0){
                    Log.e("reviewService.createReview ", "Nota do restaurante nao alterada")
                }
            }
        }
        controller.createLogNota(HistoricoNota(idRestaurante = review.idRestaurante, idReview = getMaiorId() + 1, nota = review.nota))
        return controller.createReview(review)
    }

    fun getReview(filtro : String? = null): ArrayList<Review> {
        return controller.getReview(filtro)
    }

    fun getReviewById(id: Int): Review? {
        return controller.getReviewById(id)
    }

    fun getMaiorId() : Int{
        return controller.getMaiorId()
    }

    fun getReviewByRestaurante(idRestaurante : Int): ArrayList<Review> {
        return controller.getReviewByRestaurante(idRestaurante)
    }

    fun removeReview(id: Int): Int {
        return controller.removeReview(id)
    }

    fun updateReview(review: Review): Int {
        val reviewAntigo = getReviewById(review.id!!)
        if(reviewAntigo != null && (reviewAntigo.nota != review.nota)){
            controller.desativarNotasAnteriores(review.id!!)
            controller.createLogNota(HistoricoNota(idRestaurante = review.idRestaurante, idReview = review.id!!, nota = review.nota))
        }
        if(review.idRestaurante != null){
            val restaurante = restauranteService.getRestauranteById(review.idRestaurante!!)
            if(restaurante != null){
                val notasRestaurante = restauranteService.getMediaAtualByRestaurante(restaurante.id!!)
                val notaNova = (notasRestaurante.totalNotas)/(notasRestaurante.qtdNotas)
                val restauranteNovo = Restaurante(restaurante.id, restaurante.nome, notaNova)
                val alterou = restauranteService.updateRestaurante(restauranteNovo)
                if(alterou <= 0){
                    Log.e("reviewService.createReview ", "Nota do restaurante nao alterada")
                }
            }
        }
        return controller.updateReview(review)
    }
}