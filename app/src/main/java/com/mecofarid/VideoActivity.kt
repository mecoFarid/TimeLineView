package com.mecofarid

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.mecofarid.timelineview.demo.R
import com.mecofarid.timelineview.demo.databinding.ActivityVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SeekParameters


class VideoActivity : AppCompatActivity() {
  private lateinit var binding: ActivityVideoBinding
  private val player by lazy {
    ExoPlayer.Builder(this)
      .setSeekBackIncrementMs(5_000)
      .setSeekForwardIncrementMs(5_000)
      .build()
  }
  private val uri by lazy { Uri.parse("android.resource://$packageName/${R.raw.video}") }
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityVideoBinding.inflate(layoutInflater)
    setContentView(binding.root)
    loadThumbnail()
    binding.start.setOnClickListener {
      loadVideo()
    }

    binding.remove.setOnClickListener{
      removeListener()
    }
//    setUpControllers()
  }

  private fun loadThumbnail(){
    binding.thumbnail.apply {
      Glide.with(this).load(uri).into(this)
    }
  }

  private val listener = object: Player.Listener {
    override fun onPlaybackStateChanged(playbackState: Int) {
      super.onPlaybackStateChanged(playbackState)
      player.play()
      binding.thumbnail.isVisible = false
    }
  }

  private fun loadVideo() {
    player.apply {

      removeListener(listener)
      binding.video.player = this
      val mediaItem: MediaItem = MediaItem.fromUri(uri)
      setMediaItem(mediaItem)
      prepare()
      addListener(listener)
    }
  }

  private fun removeListener(){
    player.removeListener(listener)
  }

//  private fun setUpControllers() {
//    player.apply {
//
//      binding.rewind.setOnClickListener {
//        seekTo(currentPosition - 5000)
//      }
//
//      binding.forward.setOnClickListener {
//        seekTo(currentPosition + 5000)
//      }
//
//      binding.startPause.setOnClickListener {
//        if (isPlaying)
//          pause()
//        else
//          play()
//      }
//    }
//  }
}