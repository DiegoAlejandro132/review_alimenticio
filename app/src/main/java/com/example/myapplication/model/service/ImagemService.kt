package com.example.myapplication.model.service

import android.content.Context
import com.example.myapplication.model.controller.Controller
import com.example.myapplication.model.model.Imagem

class ImagemService(context: Context) {

    private val controller = Controller(context)

    fun createImagem(imagem: Imagem): Long {
        return controller.createImagem(imagem)
    }

    fun getImagem(): ArrayList<Imagem> {
        return controller.getImagem()
    }

    fun getImagemByReview(idReview : Int): ArrayList<Imagem> {
        return controller.getImagemByReview(idReview)
    }
}