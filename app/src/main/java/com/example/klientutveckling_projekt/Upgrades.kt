package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

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

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.purchasedUpgrades.collect { purchased ->
                val available = viewModel.allUpgrades.filterNot { it.id in purchased }
                adapter.setUpgrades(available)
            }
        }

        val multiplierInUpgradestab = view.findViewById<TextView>(R.id.textCurrentMultiplierValue)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.multiplier.collect { meters ->
                multiplierInUpgradestab.text = "x%.2f".format(meters)
            }
        }


        val currentMetersDigged = view.findViewById<TextView>(R.id.meterValueXML)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.meters.collect { meters ->
                currentMetersDigged.text = "%.2f m".format(meters)
            }
        }

        //view.findViewById<>()

        return view
    }
}
