package com.mecofarid.timelineview.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import com.mecofarid.timelineview.demo.databinding.DemoTestStepViewBinding

class DemoTestStepView(
  private val inputTestStep: DemoTestStep
): TestStepView<DemoTestStep, DemoTestStepViewBinding, DemoTestStepView.TestStepViewHolder>(inputTestStep) {

  override fun getViewType(): ViewType = ViewType.DEMO_TEST_VIEW

  override fun newViewHolder(parent: ViewGroup): TestStepViewHolder{
    val demoTestStepBinding = DemoTestStepViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    val timelineBinding = demoTestStepBinding.demoTestStepTimeline

    return TestStepViewHolder(BindingHolder(demoTestStepBinding, timelineBinding))
  }

  class TestStepViewHolder(
    bindingHolder: BindingHolder<DemoTestStepViewBinding>
  ) : TestStepView.TestStepViewHolder<DemoTestStep, DemoTestStepViewBinding>(bindingHolder)
}