package com.example.klientutveckling_projekt

import com.example.klientutveckling_projekt.Upgrade
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UpgradeAdapter(
    private val upgrades: MutableList<Upgrade>,
    private val viewModel: SharedViewModel
) : RecyclerView.Adapter<UpgradeAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.upgradeName)
        val desc: TextView = view.findViewById(R.id.upgradeDescription)
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
        holder.desc.text = upgrade.description
        holder.cost.text = "Cost: %.2f".format(upgrade.cost)

        holder.button.setOnClickListener {
            if (viewModel.buyUpgrade(upgrade)) {

            }
        }
    }

    override fun getItemCount() = upgrades.size

    fun setUpgrades(newList: List<Upgrade>) {
        upgrades.clear()
        upgrades.addAll(newList)
        notifyDataSetChanged()
    }
}
