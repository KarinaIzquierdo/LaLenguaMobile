package com.marents.app

import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("productos")
    fun getProductos(): Call<List<Producto>>

    @GET("productos/{id}")
    fun getProducto(@Path("id") id: Int): Call<Producto>

    @POST("productos")
    fun crearProducto(@Body producto: Producto): Call<Producto>

    @PUT("productos/{id}")
    fun actualizarProducto(@Path("id") id: Int, @Body producto: Producto): Call<Producto>

    @DELETE("productos/{id}")
    fun eliminarProducto(@Path("id") id: Int): Call<Void>

    @GET("categorias")
    fun getCategorias(): Call<List<Categoria>>

    @GET("modelos")
    fun getModelos(): Call<List<Modelo>>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}