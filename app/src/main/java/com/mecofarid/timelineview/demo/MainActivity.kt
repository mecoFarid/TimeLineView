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

    val typeStatePairList = mutableListOf<Pair<TimelineView.Type, TimelineView.State>>()
    for (type in TimelineView.Type.values()) {
      for (state in TimelineView.State.values()) {
        typeStatePairList.add(Pair(type, state))
      }
    }

    var clickCount = 0
    binding.fab.setOnClickListener { view ->
      if (clickCount >= 15){
        clickCount = 0
      }

      val pair = typeStatePairList[clickCount]
      binding.timeline.setType(pair.first)
      binding.timeline.setState(pair.second)
      binding.state.text = "${pair.first.name} - ${pair.second.name}"

      clickCount++
    }
  }
}