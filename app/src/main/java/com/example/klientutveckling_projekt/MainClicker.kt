package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch


class MainClicker : Fragment() {

    private lateinit var repository: ClickRepository

    private val viewModel: SharedViewModel by viewModels{
        ViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = ClickRepository(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main_clicker, container, false)

        val clickableGround = view.findViewById<View>(R.id.ground_view)

        val clickCounter = view.findViewById<TextView>(R.id.click_counter)

        clickableGround.setOnClickListener {
            viewModel.clicksIncrease()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.clicks.collect { count ->
                    clickCounter.text = "Meters digged: $count m"
                }
            }
        }


        return view
    }
}