package com.sonsation.shadowlayout

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.sonsation.shadowlayout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val bind by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bind.root)

        setupBackgroundShadow()
    }

    private fun setupBackgroundShadow() {

        val shadowLayout = bind.shadowLayout

        var xOffset = 0f
        var yOffset = 0f
        var blur = 0f
        var spread = 0f
        var color = Color.parseColor("#bf000000")

        bind.xSeekbar.apply {
            min = -200
            max = 200
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    xOffset = resources.displayMetrics.density * progress
                    shadowLayout.updateBackgroundShadow(blur, xOffset, yOffset, spread, color)
                    bind.xValue.text = "${xOffset}dp"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        bind.ySeekbar.apply {
            min = -200
            max = 200
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    yOffset = resources.displayMetrics.density * progress
                    shadowLayout.updateBackgroundShadow(blur, xOffset, yOffset, spread, color)
                    bind.yValue.text = "${yOffset}dp"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        bind.radiusSeekbar.apply {
            min = 0
            max = 200
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    blur = resources.displayMetrics.density * progress
                    shadowLayout.updateBackgroundShadow(blur, xOffset, yOffset, spread, color)
                    bind.radiusValue.text = "${blur}dp"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }

        bind.spreadSeekbar.apply {
            min = 0
            max = 100
            setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    spread = resources.displayMetrics.density * progress
                    shadowLayout.updateBackgroundShadow(blur, xOffset, yOffset, spread, color)
                    bind.spreadValue.text = "${spread}dp"
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            })
        }
    }
}