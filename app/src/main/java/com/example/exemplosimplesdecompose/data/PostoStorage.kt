package com.example.exemplosimplesdecompose.data

import android.content.Context
import com.example.exemplosimplesdecompose.model.Posto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PostoStorage(context: Context) {
    private val prefs = context.getSharedPreferences("PostosPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun salvarPostos(lista: List<Posto>) {
        val json = gson.toJson(lista)
        prefs.edit().putString("lista_postos", json).apply()
    }

    fun buscarPostos(): List<Posto> {
        val json = prefs.getString("lista_postos", null) ?: return emptyList()
        val type = object : TypeToken<List<Posto>>() {}.type
        return gson.fromJson(json, type)
    }

    fun atualizarPosto(nomeAntigo: String, postoAtualizado: Posto) {
        val lista = buscarPostos().toMutableList()
        val index = lista.indexOfFirst { it.nome == nomeAntigo }
        if (index != -1) {
            lista[index] = postoAtualizado
            salvarPostos(lista) // Use o seu método que grava no SharedPreferences/JSON
        }
    }

    fun adicionarPosto(posto: Posto) {
        val lista = buscarPostos().toMutableList()
        lista.add(posto)
        salvarPostos(lista)
    }

    fun excluirPosto(id: String) {
        val lista = buscarPostos().filter { it.id != id }
        salvarPostos(lista)
    }

    fun atualizarPosto(postoAtualizado: Posto) {
        val lista = buscarPostos().map {
            if (it.id == postoAtualizado.id) postoAtualizado else it
        }
        salvarPostos(lista)
    }
}