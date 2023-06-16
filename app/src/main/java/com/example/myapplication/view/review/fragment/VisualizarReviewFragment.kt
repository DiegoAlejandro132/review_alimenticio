package com.example.myapplication.view.review.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentCriarReviewBinding
import com.example.myapplication.databinding.FragmentVisualizarReviewBinding
import com.example.myapplication.model.model.Review
import com.example.myapplication.model.service.ReviewService


class VisualizarReviewFragment(var contexto : Context, var review : Review) : Fragment(R.layout.fragment_visualizar_review) {

    private lateinit var binding : FragmentVisualizarReviewBinding
    private var modoEdicao = 0
    private val reviewService = ReviewService(contexto)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentVisualizarReviewBinding.bind(view)

        configuracaoInicial()
        setClickListeners()
    }

    private fun configuracaoInicial(){
        binding.txtNomeReview.text = review.nome.toUpperCase()
        binding.txtRestaurante.text = if(review.nomeRestaurante != null) review.nomeRestaurante else "Sem Restaurante Registrado"
        binding.txtComentarioReview.setText(review.comentario)
        binding.ratingNotaReview.rating = review.nota
        binding.btnDialogAddComentario.visibility = View.GONE
    }

    private fun setClickListeners(){
        binding.btnFecharFragmentVerReview.setOnClickListener {
            fecharFragment()
        }

        binding.btnDialogAddComentario.setOnClickListener {
            dialogAddComentario()
        }

        binding.btnSalvarReview.setOnClickListener {
            if (modoEdicao == 0){
                ativarModoEdicao()
            }else{
                updateReview()
            }
        }
    }

    private fun fecharFragment(){
        parentFragmentManager.popBackStack()
    }

    private fun ativarModoEdicao(){
        modoEdicao = 1
        binding.btnDialogAddComentario.isVisible = true
        binding.labelNotaReview.text = "Nota (agora você pode editar)"
        binding.ratingNotaReview.setIsIndicator(false)
        binding.btnSalvarReview.text = "SALVAR"
    }

    private fun dialogAddComentario(){
        val dialog = Dialog(contexto)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_adicionar_comentario)

        val btnSalvar = dialog.findViewById<Button>(R.id.btn_adicionar_comentario)
        val btnFechar = dialog.findViewById<ImageButton>(R.id.btn_fechar_dialog)
        val comentatio = dialog.findViewById<EditText>(R.id.txt_comentario)

        btnSalvar.setOnClickListener {
            binding.txtComentarioReview.text = "${binding.txtComentarioReview.text}\n${comentatio.text}"
            comentatio.setText("")
        }

        btnFechar.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateReview(){
        review.nota = binding.ratingNotaReview.rating
        review.comentario = binding.txtComentarioReview.text.toString()
        val alterou = reviewService.updateReview(review)
        if (alterou > 0){
            Toast.makeText(contexto, "Review alterado com sucesso. Deslize para baixo para atualizar", Toast.LENGTH_SHORT).show()
            fecharFragment()
        }else{
            Toast.makeText(contexto, "Houve um erro ao tentar alterar o review. Tente mais tarde", Toast.LENGTH_SHORT).show()
            fecharFragment()
        }
    }

}