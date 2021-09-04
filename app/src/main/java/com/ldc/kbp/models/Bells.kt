package com.ldc.kbp.models

data class Bells(val bells: MutableList<Bell>){

    data class Bell(val workdaysTime: String, val saturdayTime: String)

    fun load(){
        for (i in 1..7)
            bells.add(Bell("00:00", "00:00"))
    }
}