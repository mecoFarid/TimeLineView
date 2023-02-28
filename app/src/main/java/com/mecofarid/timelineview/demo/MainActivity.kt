package com.mecofarid.timelineview.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.ActivityMainBinding
import com.mecofarid.timelineview.demo.step.RECYCLERVIEW_CACHE_SIZE
import com.mecofarid.timelineview.demo.step.StepView
import com.mecofarid.timelineview.demo.step.TestStepAdapter
import com.mecofarid.timelineview.demo.step.VideoStepView
import com.mecofarid.timelineview.demo.step.VideoTestStep

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private val player: Lazy<ExoPlayer> = lazy {
    ExoPlayer.Builder(this)
      .build()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setupSteps()
  }

  private fun setupSteps(){
    val itemViewList = createSteps()
    val adapter = TestStepAdapter(context = this, itemViewList, player)
    var position = 0
    binding.fab.setOnClickListener {
      if (position > itemViewList.lastIndex)
        return@setOnClickListener

      adapter.stepIn(position)
      position++
    }

    binding.timeline.also {
      it.adapter = adapter
      it.itemAnimator = null
      it.setItemViewCacheSize(RECYCLERVIEW_CACHE_SIZE)
    }
  }

  private fun createSteps(): List<StepView<*, *>> {
    val middleCount = 12
    val typeStatePairList = mutableListOf<Pair<TimelineView.Type, TimelineView.State>>()
    typeStatePairList.add(Pair(TimelineView.Type.START, TimelineView.State.INACTIVE))
    for (i in 1..middleCount){
      typeStatePairList.add(Pair(TimelineView.Type.MIDDLE, TimelineView.State.INACTIVE))
    }
    typeStatePairList.add(Pair(TimelineView.Type.END, TimelineView.State.INACTIVE))

    val itemViewList = mutableListOf<StepView<*, *>>()
    typeStatePairList.forEach {
      itemViewList.add(
        VideoStepView(
          testStep = VideoTestStep(
            type = it.first,
            state = it.second,
            videoPath = "file:///android_asset/video.mp4"
          ),
          player
        )
      )
    }
    return itemViewList
  }

  override fun onDestroy() {
    super.onDestroy()
    player.value.apply {
      stop()
      release()
    }
  }
}