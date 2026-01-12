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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

/**
 * Uppgraderingsfragmentet för appen DigInc
 *
 * Hanterar uppdatering av data för multiplier och totala meter genom ClickRepository
 */
class Upgrades : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels {
        ViewModelFactory(ClickRepository(requireContext()))
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_upgrades, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.upgradesRecyclerView)

        val adapter = UpgradeAdapter(mutableListOf<Upgrade>(), viewModel)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.purchasedUpgrades.collect { purchased ->
                    val available = viewModel.allUpgrades.filterNot { it.id in purchased }
                    adapter.setUpgrades(available)
                }
            }
        }


        val multiplierInUpgradestab = view.findViewById<TextView>(R.id.textCurrentMultiplierValue)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.multiplier.collect { multi ->
                multiplierInUpgradestab.text = getString(R.string.multiplier_in_upgrades_fragment, multi)
            }
        }


        val currentMetersDigged = view.findViewById<TextView>(R.id.meterValueXML)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meters.collect { meters ->
                currentMetersDigged.text = getString(R.string.meters_dug_in_upgrades_fragment, formatScientific(meters))
            }
        }

        return view
    }
}
