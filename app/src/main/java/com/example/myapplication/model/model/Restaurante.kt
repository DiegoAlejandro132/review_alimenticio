package com.example.myapplication.model.model

class Restaurante(var id : Int? = null, var nome : String, var nota: Double) {

    override fun toString(): String {
        return nome
    }
}