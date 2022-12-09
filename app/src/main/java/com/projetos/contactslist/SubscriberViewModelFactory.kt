package com.projetos.contactslist


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.projetos.contactslist.db.SubscriberRepository
import java.lang.IllegalArgumentException


class SubscriberViewModelFactory(application: Application) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SubscriberViewModel::class.java)) {
            return SubscriberViewModel() as T
        }
        throw IllegalArgumentException("Unknown View Model class")
    }

}



