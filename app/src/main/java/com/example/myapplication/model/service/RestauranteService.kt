package com.example.myapplication.model.service

import android.content.Context
import com.example.myapplication.model.controller.Controller
import com.example.myapplication.model.dataClass.MediaNotas
import com.example.myapplication.model.model.Restaurante

class RestauranteService(context: Context) {

    private val controller = Controller(context)

    fun createRestaurante(restaurante: Restaurante): Long {
        return controller.createRestaurante(restaurante)
    }

    fun getRestaurante(): ArrayList<Restaurante> {
        return controller.getRestaurante()
    }

    fun getRestauranteById(id : Int): Restaurante? {
        return controller.getRestauranteById(id)
    }

    fun getMediaAtualByRestaurante(id: Int): MediaNotas {
        return controller.getMediaAtualByRestaurante(id)
    }

    fun updateRestaurante(restaurante: Restaurante): Int {
        return controller.updateRestaurante(restaurante)
    }

    fun removeRestaurante(id: Int): Int {
        return controller.removeRestaurante(id)
    }
}