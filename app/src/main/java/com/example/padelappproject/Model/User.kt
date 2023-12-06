package com.example.padelappproject.Model

data class User(
    val name:String="",
    val uid: String="",
    val handpreference: String="",
    val positionpreference: String="",
    val genderpreference:String="",
    val gender:String="",
    val matches:List<String> = mutableListOf()
) {
    constructor() : this("","", "","","","",mutableListOf())
}