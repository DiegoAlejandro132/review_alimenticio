package com.example.myapplication.view.review.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCriarReviewBinding
import com.example.myapplication.model.model.Imagem
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review
import com.example.myapplication.model.service.ImagemService
import com.example.myapplication.model.service.ReviewService
import com.example.myapplication.util.Util
import com.example.myapplication.view.review.adapter.ImagemAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.FieldPosition
import java.time.LocalDateTime
import java.time.ZoneId

class CriarReviewFragment(var contexto: Context, var restaurantes : ArrayList<Restaurante>) : Fragment(R.layout.fragment_criar_review) {

    private lateinit var binding : FragmentCriarReviewBinding
    private val reviewService = ReviewService(contexto)
    private val imagemService = ImagemService(contexto)
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
    private val imagemList = ArrayList<Bitmap>()
    private lateinit var imagemAdapter: ImagemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(contexto)
        vereificaPermissaoLocalizacao()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentCriarReviewBinding.bind(view)

        setClickListeners()
        setSpinnerRestaurante()
        setRecyclerImgens()
    }

    private fun setClickListeners(){
        binding.btnFecharFragmentCriarReview.setOnClickListener {
            fecharFragment()
        }

        binding.btnAdicionarFotoCamera.setOnClickListener {
            verificaPermissaoCamera()
        }

        binding.btnAdicionarFotoGaleria.setOnClickListener {
            verificaPermissaoGaleria()
        }

        binding.btnCriarReview.setOnClickListener {
            getUltimaLocalizacao()
        }
    }

    private fun fecharFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun verificaPermissaoCamera() {
        if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
        } else {
            tirarFoto()
        }
    }

    private fun tirarFoto() {
        if(imagemList.size == 5){
            Toast.makeText(contexto, "Não é possível inserir mais de 5 imagens", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }


    private fun salvarImagemNoDispositivo(bitmap: Bitmap): Uri? {
        return try {
            val fileDir = File(contexto.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "comidas")
            fileDir.mkdirs()
            val fileName = "comidas_${System.currentTimeMillis()}.jpg"
            val file = File(fileDir, fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            FileProvider.getUriForFile(contexto, "com.example.myapplication", file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun verificaPermissaoGaleria(){
        if (ContextCompat.checkSelfPermission(
                contexto,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                contexto as Activity,
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_GALLERY) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                iniciarIntentGaleria()
            } else {
                Toast.makeText(contexto, "permissao negada", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //val uri : Uri? = data?.data
            imagemList.add(imageBitmap)
            setRecyclerImgens()
        }

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            val imageBitmap = uri?.let { Util().uriToBitmap(contexto, it) }
            imageBitmap?.let {
                imagemList.add(it)
                setRecyclerImgens()
            }
        }
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
            criarImagens()
        }else{
            Toast.makeText(contexto, "Não foi possível criar o review. Tente mais tarde", Toast.LENGTH_SHORT).show()
        }
    }

    private fun criarImagens(){
        val idReview = reviewService.getMaiorId()
        for (bitmap in imagemList){
            val path = salvarImagemNoDispositivo(bitmap)
            val imagem = Imagem(caminho = path.toString(), idReview = idReview)
            val criouImagem = imagemService.createImagem(imagem)
            if(criouImagem <= 0){
                Toast.makeText(contexto, "Houve um erro ao tentar salvar as imagens", Toast.LENGTH_SHORT).show()
            }
        }
        fecharFragment()
    }

    private fun setSpinnerRestaurante(){
        val spinner = binding.spnRestaurantesCadastrados
        val adapter = ArrayAdapter(contexto, android.R.layout.simple_spinner_item, restaurantes)

        val selecione = "Selecione"
        adapter.insert(Restaurante(nome = selecione, nota = (0.0).toFloat()), 0)

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

    private fun setRecyclerImgens(){
        val recycler = binding.recyclerImagens
        imagemAdapter = ImagemAdapter(imagemList)
        recycler.layoutManager = LinearLayoutManager(contexto, LinearLayoutManager.HORIZONTAL, false)
        recycler.adapter = imagemAdapter

        imagemAdapter.setOnItemClickListener(object : ImagemAdapter.OnItemClickListenerImagem{
            override fun removerImagem(position: Int) {
                alertRemoverImagem(position)
            }

        })
    }

    private fun alertRemoverImagem(position: Int){
        val dialog = AlertDialog.Builder(contexto)
        dialog.setTitle("Remover Imagem")
            .setMessage("Deseja remover esta imagem?")
            .setCancelable(true)
            .setPositiveButton("Sim") {dialog, id ->
                this.imagemList.removeAt(position)
                this.setRecyclerImgens()
                dialog.dismiss()
            }
            .setNegativeButton("Não"){dialog, id -> dialog.dismiss()}

        dialog.show()
    }

    @SuppressLint("MissingPermission")
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