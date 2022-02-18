package com.mecofarid.timelineview.demo

import android.net.Uri
import android.util.Log
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.google.android.exoplayer2.ui.PlayerView
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding
import com.mecofarid.timelineview.demo.databinding.VideoPlaybackControlBinding
import com.mecofarid.timelineview.demo.databinding.VideoTestStepViewBinding

class VideoTestStepView(
  testStep: VideoTestStep
) : TestStepView<VideoTestStep, VideoTestStepView.ViewHolder>(testStep) {

  override fun getViewType(): ViewType = ViewType.VIDEO_TEST_VIEW

  override fun newViewHolder(testStepViewBinding: TestStepViewBinding): ViewHolder =
    ViewHolder(testStepViewBinding)

  class ViewHolder(testStepViewBinding: TestStepViewBinding) : TestStepViewHolder<VideoTestStep>(testStepViewBinding) {

    private val binding = VideoTestStepViewBinding.bind(bindContentLayout(testStepViewBinding))
    private val controllerBinding = VideoPlaybackControlBinding.bind(binding.root)
    private lateinit var player: Lazy<ExoPlayer>
    private lateinit var uri: Uri
    private val playerListener = object : Player.Listener {
      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        handlePlayBackState(playbackState)
      }
    }

    private fun handlePlayBackState(playbackState: Int){
      binding.apply {
        thumbnail.isVisible = false
        video.isVisible = true
        Log.d("TAG", "handlePlayBackState: ${playbackState}")
        if (step.videoEnded) {
          video.displayControllers()
        }else{
          Log.d("TAG", "handlePlayBackState: rot  ${step.videoEnded}")
          video.start(playbackState)
        }
      }
    }

    private fun PlayerView.start(playbackState: Int){
      Log.d("TAG", "handlePlayBackState:")
      this@ViewHolder.player.value.play()
      step.videoEnded = playbackState == STATE_ENDED
      toggleControllerVisibility(showMainControllers = !step.videoEnded)
      if (step.videoEnded)
        displayControllers()
    }

    private fun PlayerView.displayControllers(){
      keepControllersOn(showIndefinitely = true)
      showController()
    }

    override fun getContentLayoutRes(): Int = R.layout.video_test_step_view

    override fun bind(t: VideoTestStep) {
      super.bind(t)
      step = t
      uri = Uri.parse(t.videoPath)
      player = (bindingAdapter as TestStepAdapter).player
      stopPlayerAndShowThumbnail()
      setupReplayButton()
    }

    override fun updateState(onAnimationEndBlock: () -> Unit) {
      super.updateState(onAnimationEndBlock)
      removeListener()
      if (step.state.isStepInState()) {
        startPlayerAndHideThumbNail()
      } else {
        stopPlayerAndShowThumbnail()
      }
    }

    fun removeListener(){
      player.value.removeListener(playerListener)
    }

    private fun startPlayerAndHideThumbNail(){
      player.value.apply {
        binding.video.player = this
        setMediaItem(MediaItem.fromUri(uri))
        addListener(playerListener)
        prepare()
      }
    }

    private fun stopPlayerAndShowThumbnail() = with(step.state) {
      if (isStepOutState())
        player.value.stop()

      binding.video.player = null
      binding.apply {
        thumbnail.isVisible = true
        video.isVisible = false
        Glide.with(thumbnail).load(uri).into(thumbnail)
      }
    }

    private fun toggleControllerVisibility(showMainControllers: Boolean){
      controllerBinding.mainController.isVisible = showMainControllers
      controllerBinding.replay.isVisible = !showMainControllers
    }

    private fun setupReplayButton(){
      controllerBinding.replay.setOnClickListener {
        player.value.apply {
          binding.video.keepControllersOn(showIndefinitely = false)
          play()
          seekTo(0)
        }
      }
    }
    
    private fun PlayerView.keepControllersOn(showIndefinitely: Boolean) {
      controllerShowTimeoutMs =
        if (showIndefinitely)
          0
        else
          resources.getInteger(R.integer.test_step_view_controller_auto_hide_delay)
    }
  }
}