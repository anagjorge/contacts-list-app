package com.projetos.contactslist


import android.util.Patterns
import androidx.lifecycle.*
import com.projetos.contactslist.db.Subscriber
import com.projetos.contactslist.db.SubscriberRepository
import kotlinx.coroutines.launch


class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    val inputName = MutableLiveData<String>()

    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateBtnText = MutableLiveData<String>()

    val clearOrDeleteBtnText = MutableLiveData<String>()

    private var isUpdateOrDelete = false

    private lateinit var subscriberToUpdateOrDelete: Subscriber

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateBtnText.value = "Save"
        clearOrDeleteBtnText.value = "Clear All"
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateBtnText.value = "Update"
        clearOrDeleteBtnText.value = "Delete"
    }

    fun saveOrUpdate() {
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter subscriber's name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter subscriber's email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a correct email address")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                updateSubscriber(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    private fun updateSubscriber(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRows = repository.update(subscriber)
        if (noOfRows > 0) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateBtnText.value = "Save"
            clearOrDeleteBtnText.value = "Clear All"
            statusMessage.value = Event("$noOfRows Row Updated Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }

    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            deleteSubscriber(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun deleteSubscriber(subscriber: Subscriber) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if (noOfRowsDeleted > 0) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateBtnText.value = "Save"
            clearOrDeleteBtnText.value = "Clear All"
            statusMessage.value = Event("$noOfRowsDeleted Row Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun insert(subscriber: Subscriber) = viewModelScope.launch {
        val newRowId = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Subscriber Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getSavedSubscribers() = liveData {
        repository.subscribers.collect {
            emit(it)
        }
    }



    fun clearAll() = viewModelScope.launch {
        repository.deleteAll()
    }

}