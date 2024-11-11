package com.example.calmflightcompanion.relaxation

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.calmflightcompanion.R
import com.example.calmflightcompanion.databinding.FragmentRelaxationBinding

class RelaxationFragment : Fragment() {

    private var _binding: FragmentRelaxationBinding? = null
    private val binding get() = _binding!!
    private var timer: CountDownTimer? = null
    private lateinit var startSound: MediaPlayer
    private lateinit var endSound: MediaPlayer
    private var isTimerRunning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRelaxationBinding.inflate(inflater, container, false)
        startSound = MediaPlayer.create(context, R.raw.gong_sound)
        endSound = MediaPlayer.create(context, R.raw.gong_sound)

        // Set default time to 5 minutes (SeekBar max should be in minutes)
        binding.seekBarTime.max = 60 // Example max 60 minutes
        binding.seekBarTime.progress = 5 // Default 5 minutes

        // Set initial button text
        binding.buttonStartStopTimer.text = "Start Timer"

        binding.seekBarTime.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Optionally update the text view when the progress changes
                binding.textViewTimer.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.buttonStartStopTimer.setOnClickListener {
            if (isTimerRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        return binding.root
    }

    private fun startTimer() {
        val timeInMillis = binding.seekBarTime.progress * 60000L // Convert minutes to milliseconds
        if (timeInMillis > 0) {
            startSound.start()
            isTimerRunning = true

            // Disable the SeekBar while the timer is running
            binding.seekBarTime.isEnabled = false
            binding.buttonStartStopTimer.text = "Stop Timer"

            timer = object : CountDownTimer(timeInMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // Convert milliseconds to minutes and seconds
                    val minutes = (millisUntilFinished / 1000) / 60
                    val seconds = (millisUntilFinished / 1000) % 60

                    // Format the time as MM:SS and append "minutes"
                    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                    binding.textViewTimer.text = timeFormatted
                }

                override fun onFinish() {
                    endSound.start()
                    binding.textViewTimer.text = "00:00 min"
                    isTimerRunning = false

                    // Re-enable the SeekBar once the timer finishes
                    binding.seekBarTime.isEnabled = true
                    binding.buttonStartStopTimer.text = "Start Timer"

                    // Set the TextView to the initial time selected in the SeekBar (in minutes)
                    val initialTime = binding.seekBarTime.progress
                    binding.textViewTimer.text = String.format("%02d:00 minutes", initialTime)
                }
            }.start()
        } else {
            Toast.makeText(context, "Please set a valid time", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        isTimerRunning = false

        // Re-enable the SeekBar once the timer is stopped
        binding.seekBarTime.isEnabled = true
        binding.buttonStartStopTimer.text = "Start Timer"

        // Set the TextView to the initial time selected in the SeekBar (in minutes)
        val initialTime = binding.seekBarTime.progress
        binding.textViewTimer.text = String.format("%02d:00 minutes", initialTime)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        startSound.release()
        endSound.release()
    }
}
