package com.tinysoft.yummlysearch

import com.tinysoft.yummlysearch.network.provideOkHttp
import com.tinysoft.yummlysearch.network.provideSearchApiService
import com.tinysoft.yummlysearch.repository.Repository
import com.tinysoft.yummlysearch.repository.RepositoryImpl
import com.tinysoft.yummlysearch.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val networkModule = module {
    factory {
        provideOkHttp()
    }
    single {
        provideSearchApiService(get())
    }
}

private var dataModule = module {
    single {
        RepositoryImpl(get())
    } bind Repository::class
}

private var viewModules = module {
//    viewModel { (item: Matche) ->
//        YummlyDetailsViewModel(item, get())
//    }

    viewModel {
        SearchViewModel(get())
    }
}

val appModules = listOf(networkModule, dataModule, viewModules)