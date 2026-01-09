package com.example.klientutveckling_projekt

import com.example.klientutveckling_projekt.Upgrade
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Upgrades : Fragment() {

    private val viewModel: SharedViewModel by activityViewModels {
        ViewModelFactory(ClickRepository(requireContext()))
    }

    private val allUpgrades = listOf(
        Upgrade(1, "Faster Clicks", "More meters per click", 1.1, 10.0),
        Upgrade(2, "Stronger Drill", "Dig deeper", 1.25, 25.0),
        Upgrade(3, "Turbo Mode", "Big boost", 1.5, 50.0),
        Upgrade(4, "Mega Drill", "Huge boost", 2.0, 100.0)
    )

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
                val available = allUpgrades.filterNot { it.id in purchased }
                adapter.setUpgrades(available)
            }
        }

        return view
    }
}
