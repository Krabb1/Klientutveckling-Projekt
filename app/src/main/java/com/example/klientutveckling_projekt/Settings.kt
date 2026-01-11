package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * Fragmentet för inställningar i appen
 *
 * Ansvarar för att återställa viktig info inom appen
 */
class Settings : Fragment() {

    val job = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + job)

    private val viewModel: SharedViewModel by activityViewModels {
        ViewModelFactory(ClickRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val resetButton = view.findViewById<Button>(R.id.resetButton)

        resetButton.setOnClickListener {
            showPopUpMessageOnResetButton()
        }

        return view
    }

    private fun showPopUpMessageOnResetButton(){
        AlertDialog.Builder(requireContext())
            .setTitle("Reset progress?")
            .setMessage("This will permanently delete all your progress.")
            .setPositiveButton("Reset") { _, _ ->
                uiScope.launch(Dispatchers.IO) {
                    viewModel.reset()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}