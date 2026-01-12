package com.example.klientutveckling_projekt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView-adapter för uppgraderingar.
 *
 * Ansvarar för att visa uppgraderingsnamn, beskrivning,
 * multiplikator och kostnad.
 */
class UpgradeAdapter(
    private val upgrades: MutableList<Upgrade>,
    private val viewModel: SharedViewModel
) : RecyclerView.Adapter<UpgradeAdapter.ViewHolder>() {

    /**
     * ViewHolder som håller vy-referenser för en uppgraderingsrad.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.upgradeName)
        val description: TextView = view.findViewById(R.id.upgradeDescription)
        val multiplier: TextView = view.findViewById(R.id.upgradeMultiplier)
        val cost: TextView = view.findViewById(R.id.upgradeCost)
        val button: Button = view.findViewById(R.id.upgradeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_upgrade, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val upgrade = upgrades[position]

        holder.name.text = upgrade.name
        holder.description.text = upgrade.description

        holder.cost.text =
            "Cost: ${formatScientific(upgrade.cost)} meters"

        holder.multiplier.text =
            "Multiplier: x${upgrade.multiplier}"

        holder.button.setOnClickListener {
            if (viewModel.buyUpgrade(upgrade)) {
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount() = upgrades.size

    /**
     * Uppdaterar listan med uppgraderingar.
     */
    fun setUpgrades(newList: List<Upgrade>) {
        upgrades.clear()
        upgrades.addAll(newList)
        notifyDataSetChanged()
    }
}
