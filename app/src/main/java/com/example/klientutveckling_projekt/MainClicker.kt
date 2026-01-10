package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class MainClicker : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels {
        ViewModelFactory(ClickRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_main_clicker, container, false)

        val clickableGround = view.findViewById<View>(R.id.ground_view)
        val meterCounter = view.findViewById<TextView>(R.id.click_counter)

        clickableGround.setOnClickListener {
            viewModel.click()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meters.collect { meters: Double ->
                    meterCounter.text = getString(R.string.MetersDigged, meters)
                }
            }
        }

        val metersPerSecondCounter = view.findViewById<TextView>(R.id.meterPerSecondsCounter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.metersPerSecond.collect { meters: Double ->
                    metersPerSecondCounter.text = getString(R.string.meters_per_second, meters)
                }
            }
        }

        return view
    }
}
