package com.example.myapplication.view.review

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityRestauranteBinding
import com.example.myapplication.databinding.ActivityReviewBinding
import com.example.myapplication.view.review.fragment.CriarReviewFragment

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReviewBinding
    private val criarReviewFragment = CriarReviewFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setClickListeners()
    }

    private fun setClickListeners(){
        binding.btnAddReview.setOnClickListener {
            irCriarReview()
        }
    }

    private fun irCriarReview(){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame_layout_review, criarReviewFragment)
            addToBackStack(null)
            commit()
        }
    }
}