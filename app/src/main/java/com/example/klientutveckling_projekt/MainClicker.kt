package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

/**
 * MainClicker är huvud gameplay-fragmentet där användaren klickar
 * för att gräva ner meter och generera meter per sekund.
 *
 * Den observerar meter-relaterade state:n från [SharedViewModel]
 * och uppdaterar UI:t via lifecycle-enliga korutiner.
 */
class MainClicker : Fragment() {

    /** Repository som hanterar "meter persistence" */
    private lateinit var repository: ClickRepository

    /** Repository som hanterar leaderboard-data */
    private lateinit var leaderboardRepository: LeaderboardRepository

    private val viewModel: SharedViewModel by activityViewModels {
        ViewModelFactory(ClickRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        repository = ClickRepository(requireContext())
        leaderboardRepository = LeaderboardRepository(requireContext())

        val view = inflater.inflate(R.layout.fragment_main_clicker, container, false)

        setupClickHandling(view)
        observeMeters(view)
        observeMetersPerSecond(view)

        return view
    }

    /**
     * Hanterar klick på marken och triggar ett klick i ViewModel.
     */
    private fun setupClickHandling(view: View) {
        val clickableGround = view.findViewById<View>(R.id.ground_view)
        clickableGround.setOnClickListener {
            viewModel.click()
        }
    }

    /**
     * Observerar totalt antal grävda meter och uppdaterar UI:t
     * med vetenskaplig/engineering-formatering.
     */
    private fun observeMeters(view: View) {
        val meterCounter = view.findViewById<TextView>(R.id.click_counter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.meters.collect { meters ->
                    meterCounter.text =
                        getString(
                            R.string.meters_digged_formats,
                            formatScientific(meters)
                        )

                    leaderboardRepository.updateScore(meters)
                }
            }
        }
    }

    /**
     * Observerar meter per sekund och uppdaterar UI:t
     * med vetenskaplig/engineering-formatering.
     */
    private fun observeMetersPerSecond(view: View) {
        val metersPerSecondCounter =
            view.findViewById<TextView>(R.id.meterPerSecondsCounter)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.metersPerSecond.collect { metersPerSecond ->
                    metersPerSecondCounter.text =
                        getString(
                            R.string.meters_per_second,
                            formatScientific(metersPerSecond)
                        )
                }
            }
        }
    }
}
