package com.example.klientutveckling_projekt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
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

        val volumeSlider = view.findViewById<SeekBar>(R.id.musicVolumeSlider)
        volumeSlider.max = 100

        volumeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(fromUser){
                    viewModel.setMusicVolume(progress / 100f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        uiScope.launch(Dispatchers.IO){
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.musicVolume.collect { volume ->
                    volumeSlider.progress = (volume * 100).toInt()
                }
            }
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
        super.onDestroy()
        job.cancel()
    }

}