package com.example.turistapp.utils

import android.widget.Toast
import com.example.turistapp.utils.Constants.OPEN_GOOGLE
import com.example.turistapp.utils.Constants.OPEN_SEARCH
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {

    fun basicResponses(_message: String): String {

        val random = (0..0).random()
        val message =_message.toLowerCase()

        return when {

            //Flips a coin
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..0).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }

            //Math calculations
            message.contains("resuelve") -> {
                val equation: String? = message.substringAfterLast("resuelve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    "$answer"

                } catch (e: Exception) {
                    "lo siento, no puedo resolverlo"
                }
            }




            //
            message.contains("1") -> {
                when (random) {
                    0 -> "Tacos\nTienda\nFarmacias"
                    else -> "error" }

            }

            //H
            message.contains("2") -> {
                when (random) {
                    0 -> "Patio de juegos\nParque\nGimnasio"
                    else -> "error"
                }
            }

            //
            message.contains("3") -> {
                when (random) {
                    0 -> "Montaña\nCascada\nHotel"
                    else -> "error" }

            }
            //
            message.contains("4") -> {
                when (random) {
                    0 -> "Envianos un mensaje al Whatsapp: 5584496591!!"
                    else -> "error" }

            }

            //What time is it?
            message.contains("time") && message.contains("?")-> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }

            //Open Google
            message.contains("open") && message.contains("google")-> {
                OPEN_GOOGLE
            }

            //Search on the internet
            message.contains("search")-> {
                OPEN_SEARCH
            }

            //When the programme doesn't understand...
            else -> {
                when (random) {
                    0 -> "I don't understand..."
                    1 -> "Try asking me something different"
                    2 -> "Idk"
                    else -> "error"
                }
            }
        }
    }
}