package com.example.myapplication.model.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.myapplication.model.dataClass.MediaNotas
import com.example.myapplication.model.model.Imagem
import com.example.myapplication.model.model.Restaurante
import com.example.myapplication.model.model.Review

class Controller (context: Context): SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERSION) {

    companion object{
        private const val DATABASEVERSION = 5
        private const val DATABASENAME = "reviewDB.db"
        private const val TBLRESTAURANTE = "tbl_restaurante"
        private const val TBLREVIEW = "tbl_review"
        private const val TBLIMAGEM = "tbl_imagem"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTabelaRestaurante =("CREATE TABLE IF NOT EXISTS $TBLRESTAURANTE" +
                                    "(" +
                                    "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "   nome TEXT, " +
                                    "   nota REAL" +
                                    ")")

        val createTabelaReview =("CREATE TABLE IF NOT EXISTS $TBLREVIEW" +
                                "(" +
                                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "   data INTEGER, " +
                                "   id_restaurante INTEGER, " +
                                "   latitude REAL, " +
                                "   longitude REAL, " +
                                "   comentario TEXT, " +
                                "   nota REAL, " +
                                "   nome TEXT" +
                                ")")

        val createTabelaImagem =("CREATE TABLE IF NOT EXISTS $TBLIMAGEM" +
                                "(" +
                                "   id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "   caminho TEXT, " +
                                "   id_review INTEGER " +
                                ")")

        db?.execSQL(createTabelaRestaurante)
        db?.execSQL(createTabelaReview)
        db?.execSQL(createTabelaImagem)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS ${TBLRESTAURANTE}")
        db.execSQL("DROP TABLE IF EXISTS ${TBLREVIEW}")
        db.execSQL("DROP TABLE IF EXISTS ${TBLIMAGEM}")
        onCreate(db)
    }

    /////////////////////////////// RESTAURANTE /////////////////////////////////

    fun createRestaurante(restaurante: Restaurante): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("nome", restaurante.nome)
        contentValues.put("nota", restaurante.nota)

        val sucesso = db.insert(TBLRESTAURANTE, null, contentValues)
        db.close()
        return sucesso
    }

    @SuppressLint("Range")
    fun getRestaurante(): ArrayList<Restaurante> {
        val db = this.writableDatabase
        val list = ArrayList<Restaurante>()
        val sql = ("SELECT * FROM $TBLRESTAURANTE")

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(sql, null)
        }catch (e:Exception){
            e.printStackTrace()
            return list
        }

        var id : Int
        var nome: String
        var nota : Double

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                nome = cursor.getString(cursor.getColumnIndex("nome"))
                nota = cursor.getDouble(cursor.getColumnIndex("nota"))
                val restaurante = Restaurante(id, nome, nota)
                list.add(restaurante)
            }while (cursor.moveToNext())
        }
        return list
    }

    @SuppressLint("Range")
    fun getRestauranteById(id: Int) : Restaurante?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TBLRESTAURANTE WHERE id = ?"
        val valores = arrayOf(id.toString())
        val cursor: Cursor?
        var restaurante: Restaurante? = null

        try {
            cursor = db.rawQuery(query, valores)

            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val nome = cursor.getString(cursor.getColumnIndex("nome"))
                val nota = cursor.getDouble(cursor.getColumnIndex("nota"))
                restaurante = Restaurante(id, nome, nota)
            }

            cursor?.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } finally {
            db.close()
        }

        return restaurante
    }

    @SuppressLint("Range")
    fun getMediaAtualByRestaurante(id : Int) : MediaNotas {
        val db = this.writableDatabase
        val query = "SELECT sum(nota) as total_notas, count(*) as qtd_notas FROM $TBLREVIEW WHERE id_restaurante = ?"
        val valores = arrayOf(id.toString())
        val cursor: Cursor?
        var totalNotas = 0
        var qtdNotas = 0

        try {
            cursor = db.rawQuery(query, valores)

            if (cursor.moveToFirst()) {
                totalNotas = cursor.getInt(cursor.getColumnIndex("total_notas"))
                qtdNotas = cursor.getInt(cursor.getColumnIndex("qtd_notas"))
            }
            cursor?.close()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        } finally {
            db.close()
        }
        Log.d("msgg ", totalNotas.toString())
        Log.d("msgg ", qtdNotas.toString())
        return MediaNotas(totalNotas, qtdNotas)
    }

    fun removeRestaurante(id: Int): Int{
        val db = this.writableDatabase

        val sucesso = db.delete(TBLRESTAURANTE, "id = $id", null)
        db.close()

        return sucesso
    }

    fun updateRestaurante(restaurante : Restaurante): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("nome", restaurante.nome)
        contentValues.put("nota", restaurante.nota)

        val sucesso = db.update(TBLRESTAURANTE, contentValues, "id = ${restaurante.id}", null)
        db.close()
        return sucesso
    }


    /////////////////////////////// REVIEW /////////////////////////////////

    fun createReview(review: Review): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("data", review.data.toString())
        contentValues.put("id_restaurante", review.idRestaurante)
        contentValues.put("latitude", review.latitude)
        contentValues.put("longitude", review.longitude)
        contentValues.put("nota", review.nota)
        contentValues.put("comentario", review.comentario)
        contentValues.put("nome", review.nome)

        val sucesso = db.insert(TBLREVIEW, null, contentValues)
        db.close()
        return sucesso
    }

    @SuppressLint("Range")
    fun getReview(): ArrayList<Review> {
        val db = this.writableDatabase
        val list = ArrayList<Review>()
        val sql = "SELECT $TBLREVIEW.*, $TBLRESTAURANTE.nome as nome_restaurante FROM $TBLREVIEW " +
                  "LEFT JOIN $TBLRESTAURANTE ON $TBLREVIEW.id_restaurante = $TBLRESTAURANTE.id "

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(sql, null)
        }catch (e:Exception){
            e.printStackTrace()
            return list
        }

        var id : Int
        var comentario: String
        var nota : Float
        var latitude : Double
        var longitude : Double
        var idRestaurante : Int
        var data : Long
        var nome : String
        var nomeRestaurante : String?

        if(cursor.moveToFirst()){
            do {
                id = cursor.getInt(cursor.getColumnIndex("id"))
                comentario = cursor.getString(cursor.getColumnIndex("comentario"))
                nota = cursor.getFloat(cursor.getColumnIndex("nota"))
                latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                idRestaurante = cursor.getInt(cursor.getColumnIndex("id_restaurante"))
                data = cursor.getLong(cursor.getColumnIndex("data"))
                nome = cursor.getString(cursor.getColumnIndex("nome"))
                nomeRestaurante = cursor.getString(cursor.getColumnIndex("nome_restaurante"))

                val review = Review(id, data, idRestaurante, latitude, longitude, nota, comentario, nome, nomeRestaurante)
                list.add(review)
            }while (cursor.moveToNext())
        }
        return list
    }

    @SuppressLint("Range")
    fun getMaiorId() : Int{
        val db = this.readableDatabase
        val sql = "SELECT MAX(id) as id FROM $TBLREVIEW"
        var cursor : Cursor? = null
        var id : Int = 0

        try{
            cursor = db.rawQuery(sql, null)

            if(cursor.moveToFirst()){
                do {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                }while (cursor.moveToNext())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return id
    }

    @SuppressLint("Range")
    fun getReviewByRestaurante(idRestaurante : Int): ArrayList<Review> {
        val db = this.writableDatabase
        val list = ArrayList<Review>()
        val sql = ("SELECT * FROM $TBLREVIEW WHERE id_restaurante = ?")
        val valores = arrayOf(idRestaurante.toString())

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(sql, valores)

            if(cursor.moveToFirst()){
                var id : Int
                var comentario: String
                var nota : Float
                var latitude : Double
                var longitude : Double
                var idRestaurante : Int
                var data : Long
                var nome : String

                do {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                    comentario = cursor.getString(cursor.getColumnIndex("comentario"))
                    nota = cursor.getFloat(cursor.getColumnIndex("nota"))
                    latitude = cursor.getDouble(cursor.getColumnIndex("latitude"))
                    longitude = cursor.getDouble(cursor.getColumnIndex("longitude"))
                    idRestaurante = cursor.getInt(cursor.getColumnIndex("id_restaurante"))
                    data = cursor.getLong(cursor.getColumnIndex("data"))
                    nome = cursor.getString(cursor.getColumnIndex("nome"))

                    val review = Review(id, data, idRestaurante, latitude, longitude, nota, comentario, nome)
                    list.add(review)
                }while (cursor.moveToNext())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }
        return list
    }

    fun removeReview(id: Int): Int{
        val db = this.writableDatabase

        val sucesso = db.delete(TBLREVIEW, "id = $id", null)
        db.close()

        return sucesso
    }

    fun updateReview(review: Review): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("data", review.data.toString())
        contentValues.put("id_restaurante", review.idRestaurante)
        contentValues.put("latitude", review.latitude)
        contentValues.put("longitude", review.longitude)
        contentValues.put("nota", review.nota)
        contentValues.put("comentario", review.comentario)
        contentValues.put("nome", review.nome)

        val sucesso = db.update(TBLREVIEW, contentValues, "id = ${review.id}", null)
        db.close()
        return sucesso
    }


    /////////////////////////////// IMAGEM /////////////////////////////////

    fun createImagem(imagem : Imagem): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put("caminho", imagem.caminho)
        contentValues.put("id_review", imagem.idReview)

        val sucesso = db.insert(TBLIMAGEM, null, contentValues)
        db.close()
        return sucesso
    }

    @SuppressLint("Range")
    fun getImagem() : ArrayList<Imagem> {
        val db = this.readableDatabase
        val list = ArrayList<Imagem>()
        val sql = "SELECT * FROM $TBLIMAGEM"
        var cursor : Cursor? = null

        try{
            cursor = db.rawQuery(sql, null)

            if (cursor.moveToFirst()) {
                var id : Int
                var caminho : String
                var idReview : Int

                do {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                    caminho = cursor.getString(cursor.getColumnIndex("caminho"))
                    idReview = cursor.getInt(cursor.getColumnIndex("id_review"))
                    val imagem = Imagem(id, caminho, idReview)
                    list.add(imagem)
                } while (cursor.moveToNext())
            }
        }catch (e:Exception){
                e.printStackTrace()
        }finally {
            db.close()
        }

        return list
    }

    @SuppressLint("Range")
    fun getImagemByReview(idReview: Int) : ArrayList<Imagem> {
        val db = this.readableDatabase
        val list = ArrayList<Imagem>()
        val sql = "SELECT * FROM $TBLIMAGEM WHERE id_review = ?"
        val valores = arrayOf(idReview.toString())
        var cursor : Cursor? = null

        try{
            cursor = db.rawQuery(sql, valores)

            if (cursor.moveToFirst()) {
                var id : Int
                var caminho : String
                var idReview : Int

                do {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                    caminho = cursor.getString(cursor.getColumnIndex("caminho"))
                    idReview = cursor.getInt(cursor.getColumnIndex("id_review"))
                    val imagem = Imagem(id, caminho, idReview)
                    list.add(imagem)
                } while (cursor.moveToNext())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return list
    }

    @SuppressLint("Range")
    fun getImagensByRestaurante(idRestaurante : Int): ArrayList<Imagem> {
        val db = this.readableDatabase
        val list = ArrayList<Imagem>()
        val sql = "SELECT * FROM $TBLIMAGEM " +
                  "JOIN $TBLREVIEW ON $TBLIMAGEM.id_review = $TBLIMAGEM.id " +
                  "JOIN $TBLRESTAURANTE ON $TBLREVIEW.id_restaurante = $TBLRESTAURANTE.id  " +
                  "WHERE $TBLRESTAURANTE.id = ? " +
                  "LIMIT 1"
        val valores = arrayOf(idRestaurante.toString())
        var cursor : Cursor? = null

        try{
            cursor = db.rawQuery(sql, valores)

            if (cursor.moveToFirst()) {
                var id : Int
                var caminho : String
                var idReview : Int

                do {
                    id = cursor.getInt(cursor.getColumnIndex("id"))
                    caminho = cursor.getString(cursor.getColumnIndex("caminho"))
                    idReview = cursor.getInt(cursor.getColumnIndex("id_restaurante"))
                    val imagem = Imagem(id, caminho, idReview)
                    list.add(imagem)
                } while (cursor.moveToNext())
            }
        }catch (e:Exception){
            e.printStackTrace()
        }finally {
            db.close()
        }

        return list
    }
}