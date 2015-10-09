package com.github.andszabo.exchange.api

sealed trait Direction extends Serializable {

  def reverse(): Direction
}

object Buy extends Direction {

  def reverse = Sell

  override def toString(): String = "Buy"
}

object Sell extends Direction {

  def reverse = Buy

  override def toString(): String = "Sell"
}