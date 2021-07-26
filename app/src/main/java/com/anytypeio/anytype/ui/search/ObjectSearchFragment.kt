package com.anytypeio.anytype.ui.search

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.anytypeio.anytype.R
import com.anytypeio.anytype.core_ui.features.navigation.DefaultObjectViewAdapter
import com.anytypeio.anytype.core_utils.ext.imm
import com.anytypeio.anytype.core_utils.ext.invisible
import com.anytypeio.anytype.core_utils.ext.visible
import com.anytypeio.anytype.di.common.componentManager
import com.anytypeio.anytype.presentation.search.ObjectSearchView
import com.anytypeio.anytype.presentation.search.ObjectSearchViewModel
import com.anytypeio.anytype.presentation.search.ObjectSearchViewModelFactory
import com.anytypeio.anytype.ui.base.ViewStateFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.fragment_object_search.*
import timber.log.Timber
import javax.inject.Inject

class ObjectSearchFragment : ViewStateFragment<ObjectSearchView>(R.layout.fragment_object_search) {

    @Inject
    lateinit var factory: ObjectSearchViewModelFactory

    private lateinit var clearSearchText: View
    private lateinit var filterInputField: EditText

    private val vm by viewModels<ObjectSearchViewModel> { factory }

    private val searchAdapter by lazy {
        DefaultObjectViewAdapter(
            onClick = vm::onObjectClicked
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        BottomSheetBehavior.from(sheet).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            isHideable = true
            addBottomSheetCallback(
                object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onSlide(bottomSheet: View, slideOffset: Float) {}
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                            vm.onBottomSheetHidden()
                        }
                    }
                }
            )
        }
        vm.state.observe(viewLifecycleOwner, this)
        vm.navigation.observe(viewLifecycleOwner, navObserver)
        clearSearchText = searchView.findViewById(R.id.clearSearchText)
        filterInputField = searchView.findViewById(R.id.filterInputField)
        filterInputField.setHint(R.string.search)
        initialize()
    }

    private fun focusSearchInput() {
        filterInputField.apply {
            post {
                requestFocus()
                context.imm().showSoftInput(this, InputMethodManager.SHOW_FORCED)
            }
        }
    }

    override fun render(state: ObjectSearchView) {
        when (state) {
            ObjectSearchView.Loading -> {
                recyclerView.invisible()
                tvScreenStateMessage.invisible()
                progressBar.visible()
            }
            is ObjectSearchView.Success -> {
                progressBar.invisible()
                tvScreenStateMessage.invisible()
                recyclerView.visible()
                searchAdapter.submitList(state.objects)
            }
            ObjectSearchView.EmptyPages -> {
                progressBar.invisible()
                recyclerView.invisible()
                tvScreenStateMessage.visible()
                tvScreenStateMessage.text = getString(R.string.search_empty_pages)
            }
            is ObjectSearchView.NoResults -> {
                progressBar.invisible()
                recyclerView.invisible()
                tvScreenStateMessage.visible()
                tvScreenStateMessage.text = getString(R.string.search_no_results, state.searchText)
            }
            is ObjectSearchView.Error -> {
                progressBar.invisible()
                recyclerView.invisible()
                tvScreenStateMessage.visible()
                tvScreenStateMessage.text = state.error
            }
            else -> Timber.d("Skipping state: $state")
        }
    }

    private fun initialize() {
        recyclerView.invisible()
        tvScreenStateMessage.invisible()
        progressBar.invisible()
        searchView.findViewById<View>(R.id.clearSearchText).setOnClickListener {

        }
        clearSearchText.setOnClickListener {
            filterInputField.setText(EMPTY_FILTER_TEXT)
            clearSearchText.invisible()
        }
        filterInputField.doAfterTextChanged { newText ->
            if (newText != null) {
                vm.onSearchTextChanged(newText.toString())
            }
            if (newText.isNullOrEmpty()) {
                clearSearchText.invisible()
            } else {
                clearSearchText.visible()
            }
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = searchAdapter
        focusSearchInput()
    }

    override fun injectDependencies() {
        componentManager().objectSearchComponent.get().inject(this)
    }

    override fun releaseDependencies() {
        componentManager().objectSearchComponent.release()
    }

    companion object {
        const val EMPTY_FILTER_TEXT = ""
    }
}