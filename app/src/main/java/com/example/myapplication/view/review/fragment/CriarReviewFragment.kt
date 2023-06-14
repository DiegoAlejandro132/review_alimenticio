package com.example.myapplication.view.review.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCriarReviewBinding
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review
import com.example.myapplication.service.ReviewService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.LocalDateTime
import java.time.ZoneId

class CriarReviewFragment(var contexto: Context, var restaurantes : ArrayList<Restaurante>) : Fragment(R.layout.fragment_criar_review) {

    private lateinit var binding : FragmentCriarReviewBinding
    private val reviewService = ReviewService(contexto)
    //acesso a midia
    private val PERMISSION_CAMERA = 1
    private val PERMISSION_GALLERY = 2
    private val REQUEST_IMAGE_CAPTURE = 3
    private val REQUEST_IMAGE_PICK = 4

    private var restauranteSelecionado : Restaurante? = null
    //acesso a localizacao
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitude : Double = 0.0
    private var longitude : Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(contexto)
        vereificaPermissaoLocalizacao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCriarReviewBinding.bind(view)

        setClickListeners()
        setSpinnerRestaurante()
    }

    private fun setClickListeners(){
        binding.btnFecharFragmentCriarReview.setOnClickListener {
            fecharFragment()
        }

        binding.btnAdicionarFotoCamera.setOnClickListener {
            tirarFoto()
        }

        binding.btnAdicionarFotoGaleria.setOnClickListener {
            selecionarImagemGaleria()
        }

        binding.btnCriarReview.setOnClickListener {
            getUltimaLocalizacao()
        }
    }

    private fun fecharFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun tirarFoto() {
        if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                contexto,
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

    private fun criarReview(){
        val comentario = binding.txtComentarioReview.text
        val nota = binding.ratingNotaReview.rating
        val dataAtual = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val idRestaurante = restauranteSelecionado?.id
        val nome = binding.txtNomeReview.text

        val review = Review(data = dataAtual, nota = nota, comentario = comentario.toString(),
            idRestaurante = idRestaurante, latitude = latitude, longitude = longitude, nome = nome.toString()
        )

        val criou = reviewService.createReview(review)
        if(criou > 0){
            Toast.makeText(contexto, "Review criado com sucesso. Deslize para baixo para atualizar", Toast.LENGTH_SHORT).show()
            fecharFragment()
        }else{
            Toast.makeText(contexto, "Não foi possível criar o review. Tente mais tarde", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setSpinnerRestaurante(){
        val spinner = binding.spnRestaurantesCadastrados
        val adapter = ArrayAdapter(contexto, android.R.layout.simple_spinner_item, restaurantes)

        val selecione = "Selecione"
        adapter.insert(Restaurante(nome = selecione, nota = 0.0), 0)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selecionado = parent?.getItemAtPosition(position) as Restaurante
                restauranteSelecionado = if(selecionado.nome == "Selecione") null else parent.getItemAtPosition(position) as Restaurante

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    fun getUltimaLocalizacao() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
            }
            criarReview()
        }.addOnFailureListener {
            criarReview()
        }
    }

    private fun vereificaPermissaoLocalizacao(){
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


}