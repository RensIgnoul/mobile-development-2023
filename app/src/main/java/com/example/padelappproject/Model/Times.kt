package com.example.padelappproject.Model

import java.io.Serializable

data class Times (val time:String="",
                  var reserved : Boolean = false,
    var reservedBy:String="",
    var canInitiateReservation: Boolean = true
):Serializable