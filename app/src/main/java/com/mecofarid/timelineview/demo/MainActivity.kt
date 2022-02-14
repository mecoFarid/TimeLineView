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
    typeStatePairList.add(Pair(TimelineView.Type.START, TimelineView.State.STEP_OUT_FINISHED))
    typeStatePairList.add(Pair(TimelineView.Type.MIDDLE, TimelineView.State.STEP_IN_FINISHED))
    typeStatePairList.add(Pair(TimelineView.Type.END, TimelineView.State.INACTIVE))


    val itemViewList = mutableListOf<TestStepView<*, * ,*>>()
    typeStatePairList.forEach {
      itemViewList.add(DemoTestStepView(DemoTestStep(inputPair = it)))
    }
    val adapter = TestStepAdapter(this)
    binding.timeline.adapter = adapter
    adapter.updateDataSet(itemViewList)
  }
}