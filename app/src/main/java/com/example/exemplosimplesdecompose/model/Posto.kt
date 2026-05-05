package com.example.exemplosimplesdecompose.model

import com.example.exemplosimplesdecompose.data.Coordenadas
import java.util.UUID

data class Posto(
    val nome: String,
    val precoAlcool: String = "",
    val precoGasolina: String = "",
    val localizacao: String = "",
    val coordenadas: Coordenadas? = null,
    val dataCadastro: Long = System.currentTimeMillis(),
    val id: String = UUID.randomUUID().toString()
) {

    constructor(nome: String, coordenadas: Coordenadas) : this(
        nome = nome,
        precoAlcool = "", // Adicionando isso, forçamos o Kotlin a ir para o construtor principal!
        coordenadas = coordenadas
    )


    constructor(nome: String) : this(
        nome = nome,
        precoAlcool = "",
        coordenadas = Coordenadas(latitude = 41.40338, longitude = 2.17403)
    )
}