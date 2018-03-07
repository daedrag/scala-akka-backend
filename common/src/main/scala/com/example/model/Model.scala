package com.example.model

case class Greeting(name: String)

case class Factorial(n: Int, sender: String = "")
case class FactorialResult(n: Int, result: Long, sender: String = "")