package com.dawidj.compass.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.visible() { visibility = View.VISIBLE }
fun View.gone() { visibility = View.GONE }
fun View.onClick(onClick: () -> Unit) = setOnClickListener { onClick() }
fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}