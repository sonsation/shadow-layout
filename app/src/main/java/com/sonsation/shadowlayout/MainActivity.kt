package com.sonsation.shadowlayout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.sonsation.library.ShadowLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*findViewById<SeekBar>(R.id.seek_bar).min = 0
        findViewById<SeekBar>(R.id.seek_bar).max = 255
        findViewById<SeekBar>(R.id.seek_bar).setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                findViewById<ShadowLayout>(R.id.shadow_layout).alpha = progress.toFloat() / 255
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })*/
    }
}