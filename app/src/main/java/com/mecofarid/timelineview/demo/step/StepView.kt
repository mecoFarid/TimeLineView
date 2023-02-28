package com.mecofarid.timelineview.demo.step

import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mecofarid.timelineview.demo.R
import com.mecofarid.timelineview.demo.databinding.StepViewBinding

abstract class StepView<T: TestStep, VH: StepView.TestStepViewHolder<T>>(
  val testStep: T
) {
  abstract fun getViewType(): ViewType
  abstract fun newViewHolder(stepViewBinding: StepViewBinding): VH
  open fun steppedInViewOutsideBoundArea(){
    // Finish current step
    testStep.finishState()
  }
  abstract class TestStepViewHolder<T : TestStep>(
    private val stepViewBinding: StepViewBinding
  ) : RecyclerView.ViewHolder(stepViewBinding.root) {
    abstract fun getContentLayoutRes(): Int
    protected fun bindContentLayout(stepViewBinding: StepViewBinding): View {
      stepViewBinding.content.apply {
        layoutResource = getContentLayoutRes()
        return inflate()
      }
    }

    protected lateinit var step: T
    open fun bind(t: T) {
      step = t
      stepViewBinding.timeline.setType(t.type)
    }

    open fun updateState(onAnimationEndBlock: () -> Unit = {}) {
      stepViewBinding.apply {
        val state = step.state
        timeline.setState(state, onAnimationEndBlock)

        // Update container elevation and overlay
        contentHolder.apply {
          val overlay =
            if (state.isAnyStepInState())
              null
            else
              ContextCompat.getDrawable(context, R.color.color_step_view_overlay)

          foreground = overlay
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