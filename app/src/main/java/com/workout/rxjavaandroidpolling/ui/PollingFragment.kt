package com.workout.rxjavaandroidpolling.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.workout.rxjavaandroidpolling.MainActivity
import com.workout.rxjavaandroidpolling.R
import com.workout.rxjavaandroidpolling.presentation.PollingViewModel
import kotlinx.android.synthetic.main.polling_fragment.*

class PollingFragment : Fragment() {

    companion object {
        fun newInstance(): Fragment = PollingFragment()
    }

    private val viewModel: PollingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.polling_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MainActivity).supportActionBar?.setTitle(R.string.polling_title)

        performPollingButton.setOnClickListener { viewModel.performPolling() }

        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel.pollingValue.observe(viewLifecycleOwner) { value ->
            pollingValue.text = value
        }

        viewModel.loadingInProgress.observe(viewLifecycleOwner) { loading ->
            loadingText.isVisible = loading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearCompositeDisposable()
    }
}