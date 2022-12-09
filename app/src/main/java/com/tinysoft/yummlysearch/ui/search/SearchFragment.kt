package com.tinysoft.yummlysearch.ui.search

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.material.transition.MaterialFadeThrough
import com.tinysoft.yummlysearch.App
import com.tinysoft.yummlysearch.Constants
import com.tinysoft.yummlysearch.R
import com.tinysoft.yummlysearch.adater.ItemListAdapter
import com.tinysoft.yummlysearch.databinding.FragmentSearchBinding
import com.tinysoft.yummlysearch.ui.AbsMainFragment
import com.tinysoft.yummlysearch.ui.components.EndlessScrollListener
import com.tinysoft.yummlysearch.ui.components.LoadingState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : AbsMainFragment(R.layout.fragment_search), TextWatcher {

    companion object {
        private const val TAG = "SearchFragment"
        private const val KEY_STATE = "state"

        fun newInstance() = SearchFragment()
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var searchAdapter: ItemListAdapter
    private lateinit var endlessListener: EndlessScrollListener

    private val isLandscape get() =
        App.getContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mainActivity.setSupportActionBar(binding.toolbar)
        mainActivity.supportActionBar?.title = null

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        setupRecyclerView()
        setupToolbar()

        if (savedInstanceState != null) {
            val state: SearchViewState? = savedInstanceState.getParcelable(KEY_STATE)
            state?.let {
                viewModel.viewState = it
                binding.searchView.setText(it.query)
                render(it)
            }
        } else {
            viewModel.clearSearchResult()
        }

        addListeners()

        viewModel.searchResults.observe(viewLifecycleOwner) {
            Log.d(TAG, "search - " +
                    " result_count = ${it.items.size}," +
                    " added_count=${it.newAddedCount}," +
                    " total_count=${it.totalCount}")
            render(it)
        }

        viewModel.loadingState.observe(viewLifecycleOwner) {
            Log.d(TAG, "loading state = $it")
            render(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_STATE, viewModel.viewState)
    }

    private fun render(state: LoadingState) {
        if (state is LoadingState.LoadFailure) {
            Toast.makeText(requireContext(), state.errorMsg, Toast.LENGTH_SHORT).show()
        }

        if (state != LoadingState.Loading) {
            endlessListener.finishLoading()
        }
    }

    private fun render(viewState: SearchViewState) {
        with(binding) {
            searchAdapter.swapDataSet(viewState.items, viewState.newAddedCount)
            if (viewState.offset == 0) {
                recyclerView.smoothScrollToPosition(0)
            }
            recyclerView.isVisible = viewState.items.isNotEmpty()
            empty.isVisible = viewState.items.isEmpty()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            binding.searchView.focusAndShowKeyboard()
        }
    }

    private fun setupRecyclerView() {
        searchAdapter = ItemListAdapter(requireActivity(), emptyList())
        searchAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()

                val height = dipToPix(52)
                binding.recyclerView.setPadding(0, 0, 0, height)
            }
        })

        val count = columnCount()
        val lm = if (count == 1) {
            LinearLayoutManager(requireContext())
        } else {
            GridLayoutManager(requireContext(), count, GridLayoutManager.VERTICAL, false)
        }
        binding.recyclerView.apply {
            layoutManager = lm
            adapter = searchAdapter
        }

        endlessListener = object: EndlessScrollListener(lm) {
            override fun onLoadMore(totalItemsCount: Int, view: RecyclerView) {
                Log.e(TAG, "onLoadMore(totalItemsCount=$totalItemsCount)")
                viewModel.loadMore()
            }
        }
        binding.recyclerView.addOnScrollListener(endlessListener)
    }

    private fun columnCount(): Int {
        return if (isLandscape) 2 else 1
    }

    private fun addListeners() {
        binding.clearText.setOnClickListener {
            binding.searchView.text = null
            binding.clearText.isVisible = false
            viewModel.clearSearchResult()
        }
        binding.searchView.apply {
            addTextChangedListener(this@SearchFragment)
            focusAndShowKeyboard()
        }
    }

    private fun search(query: String) {
        Log.e(TAG, "search: $query")
        TransitionManager.beginDelayedTransition(binding.appBarLayout)
        binding.clearText.isInvisible = query.isEmpty()
        viewModel.search(query)
    }

    override fun afterTextChanged(newText: Editable?) {
        if (newText != null) {
            search(newText.toString())
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }
}

fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}

fun Fragment.dipToPix(dp: Int): Int {
    val scale = resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}