package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding

class VideoTestStepView(
  testStep: VideoTestStep
) : TestStepView<VideoTestStep, VideoTestStepView.ViewHolder>(testStep) {

  override fun getViewType(): ViewType = ViewType.VIDEO_TEST_VIEW

  override fun newViewHolder(testStepViewBinding: TestStepViewBinding): ViewHolder =
    ViewHolder(testStepViewBinding)

  class ViewHolder(private val testStepViewBinding: TestStepViewBinding) : TestStepViewHolder<VideoTestStep>(testStepViewBinding) {

    override fun getContentLayoutRes(): Int = R.layout.video_test_step_view

    init {
      bindContentLayout(testStepViewBinding)
    }
  }
}