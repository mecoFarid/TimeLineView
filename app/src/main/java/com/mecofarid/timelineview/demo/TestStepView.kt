package com.mecofarid.timelineview.demo

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.DemoTestStepTimelineBinding

abstract class TestStepView<T: TestStep, VB: ViewBinding, VH: TestStepView.TestStepViewHolder<T, VB>>(
  val testStep: T
) {
  abstract fun getViewType(): ViewType
  abstract fun newViewHolder(parent: ViewGroup): VH
  abstract class TestStepViewHolder<T : TestStep, VB : ViewBinding>(
    protected val bindingHolder: BindingHolder<VB>
  ) : RecyclerView.ViewHolder(bindingHolder.vb.root) {
    open fun bind(t: T) {
      bindingHolder.timelineBinding.timeline.setType(t.type)
      updateState(t.state)
    }

    open fun updateState(state: TimelineView.State, onAnimationEndBlock: () -> Unit = {}){
      bindingHolder.timelineBinding.timeline.setState(state, onAnimationEndBlock)
    }
  }

  enum class ViewType(val type: Int) {
    DEMO_TEST_VIEW(0);

    companion object {
      private val typeEnumMap = values().associateBy { it.type }
      fun toViewType(type: Int) = typeEnumMap.getValue(type)
    }
  }

  data class BindingHolder<VB: ViewBinding>(
    val vb: VB,
    val timelineBinding: DemoTestStepTimelineBinding
  )

  enum class Payload{
    UPDATE_STEP_STATE
  }
}