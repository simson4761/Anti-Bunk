package com.example.proj1.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

object ExtensionFunction {
    fun <T> LiveData<T>.observeOnce(lifecycleOwner : LifecycleOwner, observer: Observer<T>){
        observe(lifecycleOwner , object : Observer<T>{
            override fun onChanged(t: T) {
                observer.onChanged(t)
                removeObserver(this)
            }

        })
    }
}