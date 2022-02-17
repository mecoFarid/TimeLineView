package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.TimelineView

class TextTestStep(
  type: TimelineView.Type,
  override var state: TimelineView.State
) : TestStep(type, state)