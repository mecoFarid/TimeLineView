package com.mecofarid.timelineview.demo.step

import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.STATE_ENDED
import com.mecofarid.timelineview.demo.R
import com.mecofarid.timelineview.demo.databinding.StepViewBinding
import com.mecofarid.timelineview.demo.databinding.VideoStepViewBinding

class VideoStepView(
  testStep: VideoTestStep,
  private val player: Lazy<Player>
) : StepView<VideoTestStep, VideoStepView.ViewHolder>(testStep) {

  override fun getViewType(): ViewType = ViewType.VIDEO_TEST_VIEW

  override fun newViewHolder(stepViewBinding: StepViewBinding): ViewHolder =
    ViewHolder(stepViewBinding)

  override fun steppedInViewOutsideBoundArea() {
    player.value.stop()
  }

  class ViewHolder(stepViewBinding: StepViewBinding) : TestStepViewHolder<VideoTestStep>(stepViewBinding) {

    private val binding = VideoStepViewBinding.bind(bindContentLayout(stepViewBinding))
    private lateinit var player: ExoPlayer
    private lateinit var uri: Uri
    private val playerListener = object : Player.Listener {
      override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        handlePlayBackState(playbackState)
      }
    }

    private fun handlePlayBackState(playbackState: Int) {
      binding.apply {
        video.isVisible = true
        updateVideoSurfaceViewAndThumbnailVisibility()
        if (step.videoEnded) {
          stopVideoAndDisplayControllers()
        } else {
          start(playbackState)
        }
      }
    }

    private fun start(playbackState: Int){
      this@ViewHolder.player.play()
      step.videoEnded = playbackState == STATE_ENDED
      if (step.videoEnded)
        stopVideoAndDisplayControllers()
    }

    private fun stopVideoAndDisplayControllers(){
      this@ViewHolder.player.stop()
    }

    private fun updateVideoSurfaceViewAndThumbnailVisibility() {
      binding.apply {
        thumbnail.isVisible = step.videoEnded

        // This could have been done with View.isVisible but when doing so
        // leads to inconsistent UI behaviour. Sometimes, on scroll, there appear long gap between two
        // RecyclerView items or sometimes two RecyclerView items overlap
        video.videoSurfaceView?.visibility =
          if (step.videoEnded)
            View.INVISIBLE
          else
            View.VISIBLE
      }
    }

    override fun getContentLayoutRes(): Int = R.layout.video_step_view

    override fun bind(t: VideoTestStep) {
      super.bind(t)
      step = t
      initPlayer()
    }

    override fun updateState(onAnimationEndBlock: () -> Unit) {
      super.updateState(onAnimationEndBlock)
      removeListener()
      if (step.state.isAnyStepInState()) {
        startPlayerAndHideThumbNail()
      } else {
        stopPlayerAndShowThumbnail()
      }
    }

    fun removeListener(){
      player.removeListener(playerListener)
    }

    private fun initPlayer(){
      uri = Uri.parse(step.videoPath)
      player = (bindingAdapter as TestStepAdapter).player.value
      stopPlayerAndShowThumbnail()
    }

    private fun startPlayerAndHideThumbNail() {
      player.apply {
        // Problem with ExoPlayer while switching target SurfaceView's:
        // Sometimes view will show black screen.
        // Suggested solution says to use PlayerVIew.switchTargetView() but Recyclerview holder
        // doesn't have reference to previous holder.
        // https://github.com/google/ExoPlayer/issues/677
        (binding.video.parent as ViewGroup).apply {
          removeView(binding.video)
          addView(binding.video)
        }
        binding.video.player = this
        setMediaItem(MediaItem.fromUri(uri))
        addListener(playerListener)
        prepare()
      }
    }

    private fun stopPlayerAndShowThumbnail() = with(step.state) {
      if (isAnyStepOutState())
        player.stop()

      binding.video.player = null
      binding.apply {
        thumbnail.isVisible = true
        video.isVisible = false
        Glide.with(thumbnail).load(uri).into(thumbnail)
      }
    }
  }
}