package com.jagoancoding.countwithme

interface ItemStateListener {
    fun incrementCount(counter: Counter)
    fun onCounterAdd()
    fun onItemDelete(itemId: String)
}