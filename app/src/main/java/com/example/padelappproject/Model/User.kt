package com.example.padelappproject.Model

data class User(
    val name:String="",
    val uid: String="",
    val matches:List<String> = mutableListOf()
) {
    constructor() : this("","", mutableListOf())
}