package com.example.hwada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hwada.Model.Ad
import com.example.hwada.Model.User
import com.example.hwada.repository.SearchRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    var searchRepo: SearchRepo
) : ViewModel() {
   private var _searchState :MutableStateFlow<List<Ad>> = MutableStateFlow(listOf())
    var searchState :StateFlow<List<Ad>> = _searchState

    fun search(user: User ,text:String) = viewModelScope.launch {
        searchRepo.search(user,text).collect{
            _searchState.value = it
        }
    }

}