package com.mecofarid.timelineview.demo

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding

abstract class TestStepView<T: TestStep, VH: TestStepView.TestStepViewHolder<T>>(
  val testStep: T
) {
  abstract fun getViewType(): ViewType
  abstract fun newViewHolder(testStepViewBinding: TestStepViewBinding): VH
  abstract class TestStepViewHolder<T : TestStep>(
    private val testStepViewBinding: TestStepViewBinding
  ) : RecyclerView.ViewHolder(testStepViewBinding.root) {
    abstract fun getContentLayoutRes(): Int
    protected fun bindContentLayout(testStepViewBinding: TestStepViewBinding) :View {
      testStepViewBinding.content.apply {
        layoutResource = getContentLayoutRes()
        return inflate()
      }
    }

    open fun bind(t: T) {
      testStepViewBinding.timeline.setType(t.type)
      // TODO: Remove this
//      updateState(t.state)
    }

    open fun updateState(state: TimelineView.State, onAnimationEndBlock: () -> Unit = {}) {
      testStepViewBinding.apply {
        timeline.setState(state, onAnimationEndBlock)

        contentHolder.apply {
//          val overlay =
//            if (state.isStepInState())
//              null
//            else
//              ContextCompat.getDrawable(context, R.drawable.content_finished_overlay)

          // TODO: Resolve Magic numbers
          val elevation =
            if (state.isStepInState())
              12f
            else
              0f

//          foreground = overlay
          cardElevation = elevation
        }
      }
    }
  }

  enum class ViewType(val type: Int) {
    TEXT_TEST_VIEW(0),
    VIDEO_TEST_VIEW(1);

    companion object {
      private val typeEnumMap = values().associateBy { it.type }
      fun toViewType(type: Int) = typeEnumMap.getValue(type)
    }
  }

  enum class Payload{
    UPDATE_STEP_STATE
  }
}