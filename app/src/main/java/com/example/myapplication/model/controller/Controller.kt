package com.example.myapplication.model.controller

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.model.model.Restaurante

class Controller (context: Context): SQLiteOpenHelper(context, DATABASENAME, null, DATABASEVERSION) {

    companion object{
        private const val DATABASEVERSION = 1
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
                                "   data TEXT, " +
                                "   id_restaurante INTEGER, " +
                                "   nota REAL" +
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
}