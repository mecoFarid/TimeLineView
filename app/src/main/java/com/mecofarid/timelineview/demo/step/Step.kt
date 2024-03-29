package com.mecofarid.timelineview.demo.step

import com.mecofarid.timelineview.TimelineView

abstract class TestStep(
  val type: TimelineView.Type,
  open var state: TimelineView.State
){
  fun finishState() {
    if (state == TimelineView.State.STEP_IN)
      state = TimelineView.State.STEP_IN_FINISHED
    if (state == TimelineView.State.STEP_OUT)
      state = TimelineView.State.STEP_OUT_FINISHED
  }

  fun stepIn(){
    state = TimelineView.State.STEP_IN
  }

  fun stepOut(){
    if (state != TimelineView.State.STEP_OUT_FINISHED)
      state = TimelineView.State.STEP_OUT
  }
}

internal fun TimelineView.State.isAnyStepInState(): Boolean =
  this == TimelineView.State.STEP_IN
      || this == TimelineView.State.STEP_IN_FINISHED

internal fun TimelineView.State.isAnyStepOutState(): Boolean = isStepOutState() || this == TimelineView.State.STEP_OUT_FINISHED

internal fun TimelineView.State.isStepOutState(): Boolean = this == TimelineView.State.STEP_OUT