package com.example.myapplication.view.review.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCriarReviewBinding

class CriarReviewFragment : Fragment(R.layout.fragment_criar_review) {

    private lateinit var binding : FragmentCriarReviewBinding
    private val PERMISSION_CAMERA = 1
    private val PERMISSION_GALLERY = 2
    private val REQUEST_IMAGE_CAPTURE = 3
    private val REQUEST_IMAGE_PICK = 4

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCriarReviewBinding.bind(view)

        setClickListeners()
    }

    private fun setClickListeners(){
        binding.btnFecharFragmentCriarReview.setOnClickListener {
            fehcarFragment()
        }

        binding.btnAdicionarFotoCamera.setOnClickListener {
            tirarFoto()
        }

        binding.btnAdicionarFotoGaleria.setOnClickListener {
            selecionarImagemGaleria()
        }
    }

    private fun fehcarFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun tirarFoto() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
        } else {
            iniciarIntentCamera()
        }
    }

    private fun iniciarIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    private fun selecionarImagemGaleria() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_GALLERY
            )
        } else {
            iniciarIntentGaleria()
        }
    }

    private fun iniciarIntentGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }
}