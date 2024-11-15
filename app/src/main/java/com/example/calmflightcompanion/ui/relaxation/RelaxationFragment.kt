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
        binding.seekBarTime.max = 30 // Example max 60 minutes
        binding.seekBarTime.progress = 5 // Default 5 minutes

        // Set initial button text
        binding.buttonStartStopTimer.text = "Start Timer"

        // Set OnClickListener for the techniques
        binding.technique1.setOnClickListener { showTechniqueDescription(it) }
        binding.technique2.setOnClickListener { showTechniqueDescription(it) }
        binding.technique3.setOnClickListener { showTechniqueDescription(it) }
        binding.technique4.setOnClickListener { showTechniqueDescription(it) }

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

    fun showTechniqueDescription(view: View) {
        // Toggle the visibility of the corresponding description
        when (view.id) {
            R.id.technique1 -> {
                if (binding.description1.visibility == View.VISIBLE) {
                    // If description is already visible, hide it
                    binding.description1.visibility = View.GONE
                } else {
                    // If description is hidden, show it and set text
                    binding.description1.text = "Inhala profundamente por la nariz contando hasta 4, mantén el aire contando hasta 4 y exhala por la boca contando hasta 4. Repite durante unos minutos."
                    binding.description1.visibility = View.VISIBLE
                }
            }
            R.id.technique2 -> {
                if (binding.description2.visibility == View.VISIBLE) {
                    binding.description2.visibility = View.GONE
                } else {
                    binding.description2.text = "Inhala durante 4 segundos, mantén el aire durante 7 segundos y exhala durante 8 segundos. Repite durante unos minutos."
                    binding.description2.visibility = View.VISIBLE
                }
            }
            R.id.technique3 -> {
                if (binding.description3.visibility == View.VISIBLE) {
                    binding.description3.visibility = View.GONE
                } else {
                    binding.description3.text = "Comienza por los dedos de los pies y tensa cada grupo muscular (pies, piernas, abdomen, brazos, cara) durante unos 5 segundos, luego relaja completamente. Repite con cada grupo muscular."
                    binding.description3.visibility = View.VISIBLE
                }
            }
            R.id.technique4 -> {
                if (binding.description4.visibility == View.VISIBLE) {
                    binding.description4.visibility = View.GONE
                } else {
                    binding.description4.text = "Cierra los ojos e imagina que estás en un lugar tranquilo (por ejemplo, una playa, un bosque o una montaña). Concéntrate en los detalles del lugar y respira profundamente."
                    binding.description4.visibility = View.VISIBLE
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        startSound.release()
        endSound.release()
    }
}
