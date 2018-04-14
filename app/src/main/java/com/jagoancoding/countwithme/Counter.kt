package com.jagoancoding.countwithme

class Counter(var title: String, var count: Long = 0L) {
    fun increment() {
        count++
    }
}