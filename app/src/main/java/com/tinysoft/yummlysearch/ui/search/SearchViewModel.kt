package com.tinysoft.yummlysearch.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tinysoft.yummlysearch.Constants.SEARCH_LIMIT
import com.tinysoft.yummlysearch.network.ResultWrapper
import com.tinysoft.yummlysearch.repository.Repository
import com.tinysoft.yummlysearch.ui.components.LoadingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {

    private var searchJob: Job? = null
    private val _searchResults = MutableLiveData<SearchViewState>()
    val searchResults: LiveData<SearchViewState> = _searchResults
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    var viewState = SearchViewState.EMPTY

    fun clearSearchResult() {
        searchJob?.cancel()
        _searchResults.postValue(SearchViewState.EMPTY)
    }

    fun loadMore() {
        val word = viewState.query ?: return
        if (word.isEmpty()) return

        search(word, viewState.offset)
    }

    fun search(query: String?, index: Int = 0, maxResult: Int = SEARCH_LIMIT) {
        if (index == 0) {
            viewState = SearchViewState.EMPTY
        }
        searchJob?.cancel() // cancel previous search job
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            if (query.isNullOrEmpty()) {
                _loadingState.postValue(LoadingState.Idle)
                _searchResults.postValue(SearchViewState.EMPTY)
                return@launch
            }

            delay(300)

            _loadingState.postValue(LoadingState.Loading)
            val result = repository.getSearch(query, index, maxResult)

            val loading = when (result) {
                is ResultWrapper.GenericError -> LoadingState.LoadFailure(result.message ?: "Unknown error")
                ResultWrapper.NetworkError -> LoadingState.LoadFailure("Network Error")
                is ResultWrapper.Success -> {
                    viewState = SearchViewState(
                        query,
                        result.value.totalMatchCount,
                        result.value.matches.size,
                        viewState.items + result.value.matches
                    )
                    _searchResults.postValue(viewState)

                    LoadingState.Idle
                }
            }
            _loadingState.postValue(loading)
        }
    }
}