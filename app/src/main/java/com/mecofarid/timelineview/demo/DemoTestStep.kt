package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.TimelineView

data class DemoTestStep(
  private val inputPair: Pair<TimelineView.Type, TimelineView.State>
): TestStep(inputPair)