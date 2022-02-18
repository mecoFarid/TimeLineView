package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.TimelineView

class VideoTestStep(
  type: TimelineView.Type,
  override var state: TimelineView.State,
  val videoPath: String,
  var videoEnded: Boolean = false
) : TestStep(type, state)