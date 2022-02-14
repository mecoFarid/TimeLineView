package com.mecofarid.timelineview.demo

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mecofarid.timelineview.demo.databinding.DemoTestStepTimelineBinding

abstract class TestStepView<T: TestStep, VB: ViewBinding, VH: TestStepView.TestStepViewHolder<T, VB>>(
  val testStep: T
) {
  abstract fun getViewType(): ViewType
  abstract fun newViewHolder(parent: ViewGroup): VH
  abstract class TestStepViewHolder<T : TestStep, VB : ViewBinding>(
    private val bindingHolder: BindingHolder<VB>
  ) : RecyclerView.ViewHolder(bindingHolder.vb.root) {
    open fun bind(t: T) {
      bindingHolder.timelineBinding.timeline.setType(t.pair.first)
      bindingHolder.timelineBinding.timeline.setState(t.pair.second)
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
}