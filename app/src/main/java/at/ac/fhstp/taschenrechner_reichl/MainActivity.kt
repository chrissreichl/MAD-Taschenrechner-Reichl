package at.ac.fhstp.taschenrechner_reichl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import kotlinx.android.synthetic.main.activity_main.*
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Numbers
        buZero.setOnClickListener{appendOnEprestion("0", true)}
        buOne.setOnClickListener{appendOnEprestion("1", true)}
        buTwo.setOnClickListener{appendOnEprestion("2", true)}
        buThree.setOnClickListener{appendOnEprestion("3", true)}
        buFour.setOnClickListener{appendOnEprestion("4", true)}
        buFive.setOnClickListener{appendOnEprestion("5", true)}
        buSix.setOnClickListener{appendOnEprestion("6", true)}
        buSeven.setOnClickListener{appendOnEprestion("7", true)}
        buEight.setOnClickListener{appendOnEprestion("8", true)}
        buNine.setOnClickListener{appendOnEprestion("9", true)}
        buComma.setOnClickListener{appendOnEprestion(".",true)}

        //Actions
        buPlus.setOnClickListener{appendOnEprestion("+",false)}
        buMinus.setOnClickListener{appendOnEprestion("-",false)}
        buMulti.setOnClickListener{appendOnEprestion("*",false)}
        buDivide.setOnClickListener{appendOnEprestion("/",false)}
        buPercent.setOnClickListener{valueToPercent()}
        buPlusMinus.setOnClickListener{changeSign()}


        buClear.setOnClickListener{
            if(tvResult.text == ""){
                val string = tvExpression.text.toString() //save the input of tvExpression in string
                if(string.isNotEmpty()){

                    val stringLength = string.length //if tvExpression contains no more value, then write AC
                    if(stringLength <= 1){
                        buClear.text = "AC"
                    }

                    tvExpression.text = string.substring(0,string.length-1) //Delete the last character of the string

                }
                tvResult.text = "" //clear tvResults
            }else {
                tvExpression.text = ""
                tvResult.text = ""
            }
        }




        buEquals.setOnClickListener{
            try{
                val expression = ExpressionBuilder(tvExpression.text.toString()).build()
                val result = expression.evaluate() //save result to var result

                doubleOrLong(result) // if value = 10.0 value will be 10 (the decimal is not needed)
                buClear.text = "AC"


            }catch (e: Exception){
                Log.d("Exception", "message : " + e.message)
            }
        }
    }

    fun appendOnEprestion(string: String, canClear: Boolean){
            buClear.text = "C"
        if(canClear){ //number was entered
            if(tvResult.text != ""){ //if result not "" and user enter a number, then delete Expression to start a new calculation
                tvExpression.text = ""
            }
            tvResult.text = ""
            tvExpression.append(string)

        }else {//operation was entered
            if(tvResult.text != ""){ //if result not "" and user enter a operation, then result = expression
                tvExpression.text = tvResult.text
            }
            if (tvExpression.text.takeLast(1).isDigitsOnly()){ //if last character of expression is numeric (Not: + - * /) append string (e.g. 2) -> else delete it and append new calculation operation -> this prevent the possibility to input +--++...
                tvExpression.append(string)
                tvResult.text = ""
            }else{
                val currentExpression = tvExpression.text.toString() // read out current expression
                tvExpression.text = currentExpression.substring(0,currentExpression.length-1) //delete the last character of the string (old calculation operation)
                tvExpression.append(string) //add the new calculation operation
                tvResult.text = ""
            }
        }
    }

    fun valueToPercent (){
        try {
            if (tvResult.text == "") {
                val expression = tvExpression.text.toString()
                splitExpression(expression, "%")
            } else{
                val number = tvResult.text.toString().toDouble() / 100
                doubleOrLong(number)
            }
        }catch(e: Exception){
            Toast.makeText(this, "NO Number to convert to percent!", Toast.LENGTH_SHORT).show()
        }
    }

    fun changeSign (){
        try {
            if (tvResult.text == "") {
                val expression = tvExpression.text.toString()
                splitExpression(expression, "+/-")
            } else{
                val number = tvResult.text.toString().toDouble() * (-1)
                doubleOrLong(number)
            }
        }catch(e: Exception){
            Toast.makeText(this, "NO number to change the sign!", Toast.LENGTH_SHORT).show()
        }
    }

    fun doubleOrLong(number: Double){ // if value = 10.0 value will be 10 (the decimal is not needed)
        val longResult = number.toLong()
        if(number == longResult.toDouble())
            tvResult.text = longResult.toString()  //save as long value
        else
            tvResult.text = number.toString() //save as double value
    }


    fun splitExpression(expressionString: String, SignOrPercent: String) { //I need to split the expression string so that I can change the numbers

        val delimiter1 = "+"
        val delimiter2 = "-"
        val delimiter3 = "*"
        val delimiter4 = "/"

        var plusFlag = false

        val parts = expressionString.split(delimiter1, delimiter2, delimiter3, delimiter4) //Array of numbers from tvExpression
        var lastNumber = parts[parts.lastIndex].toDouble()

        val lastIndexLength = parts[parts.lastIndex].length //I need to know how long last input was to delete it from tvExpression
        var newExpression = expressionString.substring(0,expressionString.length-lastIndexLength) //Delete the last the last input from tvExpression

        if(SignOrPercent == "%"){
            lastNumber /=100 //calculation to percent
        }else{
            if(newExpression.takeLast(1) != "-") { //if there is no - before the number, then make it negative
                lastNumber *= (-1) //change sign
                if(newExpression.takeLast(1) == "+"){ //if there was before a + , then delete it negative
                    newExpression = newExpression.substring(0,newExpression.length-1)
                }
            }else if (newExpression.takeLast(1) == "-"){
                plusFlag = true
                newExpression = newExpression.substring(0,newExpression.length-1) //if there was before a - , then delete it negative
            }else{
                plusFlag = true
            }
        }

        //doubleOrLong
        val longResult = lastNumber.toLong()
        if(lastNumber == longResult.toDouble()) {
            if (plusFlag) {
                newExpression += "+" + longResult  //save as long value with +
            } else {
                newExpression += longResult //save as long value
            }
        }else {
            if (plusFlag) {
                newExpression += "+" + lastNumber //save as double value with +
            } else {
                newExpression += lastNumber //save as double value
            }
        }
        tvExpression.text = newExpression
    }
}
