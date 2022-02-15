package com.mecofarid.timelineview.demo

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val middleCount = 6
    val typeStatePairList = mutableListOf<Pair<TimelineView.Type, TimelineView.State>>()
    typeStatePairList.add(Pair(TimelineView.Type.START, TimelineView.State.INACTIVE))
    for (i in 1..middleCount){
      typeStatePairList.add(Pair(TimelineView.Type.MIDDLE, TimelineView.State.INACTIVE))
    }
    typeStatePairList.add(Pair(TimelineView.Type.END, TimelineView.State.INACTIVE))


    val itemViewList = mutableListOf<TestStepView<*, * ,*>>()
    typeStatePairList.forEach {
      itemViewList.add(DemoTestStepView(DemoTestStep(type = it.first, state = it.second)))
    }


    val adapter = TestStepAdapter(itemViewList)

    var position = 0
    binding.fab.setOnClickListener {
      if (position < middleCount + 2)
        adapter.stepIn(position)
      binding.timeline.smoothSnapToPosition(position)
      position++
    }

    binding.timeline.also {
      it.adapter = adapter
      it.itemAnimator = null
    }
  }

  private fun RecyclerView.smoothSnapToPosition(position: Int) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
      override fun getVerticalSnapPreference(): Int = SNAP_TO_START
      override fun calculateTimeForScrolling(dx: Int): Int = 250
    }
    smoothScroller.targetPosition = position
    layoutManager?.startSmoothScroll(smoothScroller)
  }
}