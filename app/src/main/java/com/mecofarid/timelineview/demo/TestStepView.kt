package com.mecofarid.timelineview.demo

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
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
    protected fun bindContentLayout(testStepViewBinding: TestStepViewBinding): View {
      testStepViewBinding.content.apply {
        layoutResource = getContentLayoutRes()
        return inflate()
      }
    }

    protected lateinit var step: T
    open fun bind(t: T) {
      step = t
      testStepViewBinding.timeline.setType(t.type)
    }

    open fun updateState(onAnimationEndBlock: () -> Unit = {}) {
      testStepViewBinding.apply {
        val state = step.state
        timeline.setState(state, onAnimationEndBlock)

        // Update container elevation and overlay
        contentHolder.apply {
          val overlay =
            if (state.isStepInState())
              null
            else
              ContextCompat.getDrawable(context, R.color.color_step_view_overlay)

          val elevation =
            if (state.isStepInState())
              context.resources.getDimension(R.dimen.step_view_content_elevation)
            else
              0f

          foreground = overlay
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