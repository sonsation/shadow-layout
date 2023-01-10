package com.sonsation.shadowlayout

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

    fun loadBitmapFromView(v: View?, weight: Int? = null, height: Int? = null): Bitmap? {

        if (v == null)
            return null

        val b = Bitmap.createBitmap(
            weight ?: v.width,
            height ?: v.height,
            Bitmap.Config.ARGB_8888
        )

        val c = Canvas(b)
        c.drawColor(Color.TRANSPARENT)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }
}