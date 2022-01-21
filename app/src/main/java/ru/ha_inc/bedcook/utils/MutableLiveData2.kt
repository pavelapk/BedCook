package ru.ha_inc.bedcook.utils

import androidx.lifecycle.LiveData

@Suppress("UNCHECKED_CAST")
class MutableLiveData2<T>(value: T) : LiveData<T>(value) {

    override fun getValue(): T = super.getValue() as T
    public override fun setValue(value: T) = super.setValue(value)
    public override fun postValue(value: T) = super.postValue(value)
}