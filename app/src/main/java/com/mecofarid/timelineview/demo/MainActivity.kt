package com.mecofarid.timelineview.demo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding

  private val player: Lazy<ExoPlayer> = lazy {
    ExoPlayer.Builder(this)
      .setSeekBackIncrementMs(SEEK_INCREMENT)
      .setSeekForwardIncrementMs(SEEK_INCREMENT)
      .build()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    val middleCount = 12
    val typeStatePairList = mutableListOf<Pair<TimelineView.Type, TimelineView.State>>()
    typeStatePairList.add(Pair(TimelineView.Type.START, TimelineView.State.INACTIVE))
    for (i in 1..middleCount){
      typeStatePairList.add(Pair(TimelineView.Type.MIDDLE, TimelineView.State.INACTIVE))
    }
    typeStatePairList.add(Pair(TimelineView.Type.END, TimelineView.State.INACTIVE))

    val itemViewList = mutableListOf<TestStepView<*,*>>()
    typeStatePairList.forEach {
      if (false)
      itemViewList.add(TextTestStepView(TextTestStep(type = it.first, state = it.second)))
      else
        itemViewList.add(VideoTestStepView(
          VideoTestStep(
            type = it.first,
            state = it.second,
            videoPath = "android.resource://$packageName/${R.raw.video}"
          ))
        )
    }
    val adapter = TestStepAdapter(itemViewList, player)
    var position = 0
    binding.fab.setOnClickListener {
      if (position < middleCount + 2)
        adapter.stepIn(position)
      position++
    }

    binding.timeline.also {
      it.adapter = adapter
      it.itemAnimator = null
      it.setItemViewCacheSize(RECYCLERVIEW_CACHE_SIZE)
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    player.value.apply {
      stop()
      release()
    }
  }
}