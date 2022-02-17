package com.mecofarid.timelineview.demo

import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding
import com.mecofarid.timelineview.demo.databinding.TextTestStepViewBinding

class TextTestStepView(
  private val inputTestStep: TextTestStep
): TestStepView<TextTestStep, TextTestStepView.ViewHolder>(inputTestStep) {

  override fun getViewType(): ViewType = ViewType.TEXT_TEST_VIEW

  override fun newViewHolder(testStepViewBinding: TestStepViewBinding): ViewHolder =
    ViewHolder(testStepViewBinding)

  class ViewHolder(
    private val testStepViewBinding: TestStepViewBinding
  ) : TestStepView.TestStepViewHolder<TextTestStep>(testStepViewBinding){

    override fun getContentLayoutRes(): Int = R.layout.text_test_step_view

    init {
      val contentView = bindContentLayout(testStepViewBinding)
      TextTestStepViewBinding.bind(contentView)
    }
  }
}