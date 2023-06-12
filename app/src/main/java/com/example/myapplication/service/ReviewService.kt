package com.example.myapplication.service

import android.content.Context
import com.example.myapplication.model.controller.Controller
import com.example.myapplication.model.model.Review

class ReviewService(context: Context) {

    private val controller = Controller(context)

    fun createReview(review: Review): Long {
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