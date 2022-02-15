package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.TimelineView

class DemoTestStep(
  type: TimelineView.Type,
  override var state: TimelineView.State
) : TestStep(type, state)