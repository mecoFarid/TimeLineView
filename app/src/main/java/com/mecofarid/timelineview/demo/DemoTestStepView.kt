package com.mecofarid.timelineview.demo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.mecofarid.timelineview.TimelineView
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
  ) : TestStepView.TestStepViewHolder<DemoTestStep, DemoTestStepViewBinding>(bindingHolder) {
    override fun updateState(state: TimelineView.State, onAnimationEndBlock: () -> Unit) {
      super.updateState(state, onAnimationEndBlock)
      bindingHolder.vb.content.apply {
        val overlay =
          if (state.isStepInState())
            null
          else
            ContextCompat.getDrawable(context, R.drawable.content_finished_overlay)

        val elevation =
          if (state.isStepInState())
            12f
          else
            0f

        foreground = overlay
        cardElevation = elevation
      }
    }
  }
}