package com.abovo.tris

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private class Player{
        var points = 0
            set(value){
                field = value - value%Int.MAX_VALUE
            }
        var statistics = 0.0
        var games = 1
            set(value){
                require(field < value){
                    "Number of games must increase"
                }
                field = value
            }
        var name = "" //(TODO) develop UI for name and session
            set(value){
                require(value != ""){
                    "You must enter a valid name"
                }
                field = value
            }
    }

    private var buttons: MutableList<Button> = mutableListOf()
    private var listPositions: Array<Int> = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private val player = Player()

    private fun isFilled(listPositions: Array<Int>): Boolean{
        for(i in 0..8){
            if(listPositions[i] == 0){
                return false
            }
        }
        return true
    }

    private fun isWon(listPositions: Array<Int>): Boolean{
        for(i in 0..2){
            var bool = true
            for(e in 0..2){
                bool = bool && listPositions[i*3+e] == 2
            }
            if(bool){
                return true
            }
        }
        for(i in 0..2){
            var bool = true
            for(e in 0..2){
                bool = bool && listPositions[i+3*e] == 2
            }
            if(bool){
                return true
            }
        }
        if(listPositions[0] == 2 && listPositions[4] == 2 && listPositions[8] == 2){
            return true
        }
        if(listPositions[2] == 2 && listPositions[4] == 2 && listPositions[6] == 2){
            return true
        }
        return false
    }

    private fun isLost(listPositions: Array<Int>): Boolean{
        for(i in 0..2){
            var bool = true
            for(e in 0..2){
                bool = bool && listPositions[i*3+e] == 1
            }
            if(bool){
                return true
            }
        }
        for(i in 0..2){
            var bool = true
            for(e in 0..2){
                bool = bool && listPositions[i+3*e] == 1
            }
            if(bool){
                return true
            }
        }
        if(listPositions[0] == 1 && listPositions[4] == 1 && listPositions[8] == 1){
            return true
        }
        if(listPositions[2] == 1 && listPositions[4] == 1 && listPositions[6] == 1){
            return true
        }
        return false
    }

    private fun findBestMove(listPositions: Array<Int>, myTurn: Boolean): Int{
        if(isWon(listPositions)){
            return 1
        } else if(isLost(listPositions)){
            return 0
        }
        var counter = 0
        for(i in 0..8){
            if(listPositions[i] == 0){
                if(myTurn){
                    listPositions[i] = 1
                    counter += findBestMove(listPositions, false)
                    listPositions[i] = 0
                } else {
                    listPositions[i] = 2
                    counter += findBestMove(listPositions, true)
                    listPositions[i] = 0
                }
            }
        }
        return counter
    }

    private fun chooseMove(listPositions: Array<Int>): Int {
        val possibleCombinations: MutableList<Int> = mutableListOf()
        var bestDecision = Pair(1e8.toInt(), -1)
        for(i in 0..8){
            if(listPositions[i] == 0){
                possibleCombinations.add(i)
            }
        }
        for(i in possibleCombinations){
            listPositions[i] = 1
            val solution = findBestMove(listPositions.clone(), false)
            if(solution < bestDecision.first){
                bestDecision = Pair(solution, i)
                // printScheme(bestDecision.second)
                // print(i)
                // println(solution)
            }
            listPositions[i] = 0
        }
        return bestDecision.second
    }

    private fun reset(){
        for(i in 0..8){
            listPositions[i] = 0
            buttons[i].text = " "
            buttons[i].isEnabled = true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun state(): Boolean{
        if(isWon(listPositions)){
            player.points++
            player.games++
            player.statistics = player.points.toDouble()/player.games.toDouble()
            findViewById<TextView>(R.id.textview).text = "Points: ${player.points}"
            reset()
            return true
        } else if(isLost(listPositions)){
            player.points--
            player.games++
            player.statistics = player.points.toDouble()/player.games.toDouble()
            findViewById<TextView>(R.id.textview).text = "Points: ${player.points}"
            reset()
        } else if(isFilled(listPositions)){
            player.games++
            player.statistics = player.points.toDouble()/player.games.toDouble()
            findViewById<TextView>(R.id.textview).text = "Points: ${player.points}"
            reset()
            return true
        }
        return false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        buttons.add(findViewById(R.id.button1))
        buttons.add(findViewById(R.id.button2))
        buttons.add(findViewById(R.id.button3))
        buttons.add(findViewById(R.id.button4))
        buttons.add(findViewById(R.id.button5))
        buttons.add(findViewById(R.id.button6))
        buttons.add(findViewById(R.id.button7))
        buttons.add(findViewById(R.id.button8))
        buttons.add(findViewById(R.id.button9))
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(view: View) {
        val button = findViewById<Button>(view.id)
        listPositions[button.tag.toString().toInt()] = 2
        button.text = "X"
        button.isEnabled = false
        if(state()){
            return
        }
        val id = chooseMove(listPositions)
        listPositions[id] = 1
        buttons[id].text = "O"
        buttons[id].isEnabled = false
        if(state()){
            return
        }
    }
}