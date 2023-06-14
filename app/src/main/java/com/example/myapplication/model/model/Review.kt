package com.example.myapplication.model.model

import java.time.LocalDateTime

class Review (var id : Int? = null, var data : Long, var idRestaurante : Int? = null,
              var latitude : Double, var longitude : Double, var nota: Float, var comentario : String, var nome : String){
}