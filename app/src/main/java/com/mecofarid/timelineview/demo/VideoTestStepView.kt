package com.mecofarid.timelineview.demo

import android.net.Uri
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_READY
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding
import com.mecofarid.timelineview.demo.databinding.VideoTestStepViewBinding

class VideoTestStepView(
  testStep: VideoTestStep
) : TestStepView<VideoTestStep, VideoTestStepView.ViewHolder>(testStep) {

  override fun getViewType(): ViewType = ViewType.VIDEO_TEST_VIEW

  override fun newViewHolder(testStepViewBinding: TestStepViewBinding): ViewHolder =
    ViewHolder(testStepViewBinding)

  class ViewHolder(testStepViewBinding: TestStepViewBinding) : TestStepViewHolder<VideoTestStep>(testStepViewBinding) {

    private val binding = VideoTestStepViewBinding.bind(bindContentLayout(testStepViewBinding))
    private lateinit var player: Lazy<ExoPlayer>
    private lateinit var uri: Uri
    private val playerListener = object : Player.Listener {
      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if (playbackState == STATE_READY) {
          binding.thumbnail.isVisible = false
          binding.video.isVisible = true
          player.value.play()
        }
      }
    }

    override fun getContentLayoutRes(): Int = R.layout.video_test_step_view

    override fun bind(t: VideoTestStep) {
      super.bind(t)
      uri = Uri.parse(t.videoPath)
      player = (bindingAdapter as TestStepAdapter).player
      unsetPlayerAndShowThumbnail()
    }

    override fun updateState(state: TimelineView.State, onAnimationEndBlock: () -> Unit) {
      super.updateState(state, onAnimationEndBlock)
      player.value.apply {
        removeListener()
        if (state.isStepInState()) {
          binding.video.player = this
          setMediaItem(MediaItem.fromUri(uri))
          addListener(playerListener)
          prepare()
        }else{
          unsetPlayerAndShowThumbnail()
        }
      }
    }

    fun removeListener(){
      player.value.removeListener(playerListener)
    }

    private fun unsetPlayerAndShowThumbnail(){
      binding.video.player = null
      binding.apply {
        thumbnail.isVisible = true
        video.isVisible = false
        Glide.with(thumbnail).load(uri).into(thumbnail)
      }
    }
  }
}