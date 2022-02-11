package com.mecofarid.timelineview.demo

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.fab.setOnClickListener { view ->
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show()
    }


    Log.d("TAG", "onSizeChanged:b")
    binding.timeline.setType(TimelineView.Type.END)
    binding.timeline.setState(TimelineView.State.INACTIVE)
  }
}