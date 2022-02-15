package com.mecofarid.timelineview.demo

import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mecofarid.timelineview.TimelineView

class TestStepAdapter(
  private val itemViewList: List<TestStepView<*, * ,*>>
): RecyclerView.Adapter<TestStepView.TestStepViewHolder<TestStep, ViewBinding>>() {
  private val typeItemViewMap = mutableMapOf<TestStepView.ViewType, TestStepView<*, *, *>>()

  lateinit var recyclerView: RecyclerView
  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView


    var previousY = 0f
    var scrollingUp = false
    recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
      override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        Log.d("TAG", "onInterceptTouchEvent: po t ${e.y} <= $previousY")
        scrollingUp = previousY <= e.y + 5
        previousY = e.y

        return super.onInterceptTouchEvent(rv, e)
      }
    })

//    recyclerView.layoutManager = object : LinearLayoutManager(recyclerView.context) {
//      override fun canScrollVertically(): Boolean {
//        Log.d("TAG", "onInterceptTouchEvent: scroll $scrollingUp")
//        return scrollingUp
//      }
//    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestStepView.TestStepViewHolder<TestStep, ViewBinding> =
    with(TestStepView.ViewType.toViewType(viewType)) {
      typeItemViewMap.getValue(this).newViewHolder(parent) as TestStepView.TestStepViewHolder<TestStep, ViewBinding>
    }

  override fun onViewRecycled(holder: TestStepView.TestStepViewHolder<TestStep, ViewBinding>) {
    super.onViewRecycled(holder)
    finishStepAt(holder.adapterPosition)
  }

  override fun onBindViewHolder(holderTestStep: TestStepView.TestStepViewHolder<TestStep, ViewBinding>, position: Int) =
    with(getItem(position).testStep) {
      holderTestStep.bind(this)
    }

  override fun onBindViewHolder(holder: TestStepView.TestStepViewHolder<TestStep, ViewBinding>, position: Int, payloads: List<Any>) {
    if (payloads.contains(TestStepView.Payload.UPDATE_STEP_STATE)){
      updateState(holder)
    } else{
      onBindViewHolder(holder, position)
    }
  }

  private fun updateState(holder: TestStepView.TestStepViewHolder<TestStep, ViewBinding>) = with(holder) {
    val currentStep = getItem(adapterPosition).testStep
    updateState(
      state = currentStep.state,
      onAnimationEndBlock = {
        finishStepAt(adapterPosition)
        if (currentStep.isInOutState())
          notifyNextStep(adapterPosition)
      }
    )
  }

  private fun finishStepAt(position: Int){
    if (position == RecyclerView.NO_POSITION)
      return

    // Finish current step
    getItem(position).testStep.finishState()
  }

  private fun notifyNextStep(position: Int){
    val nextPosition = position.inc()
    if (position == RecyclerView.NO_POSITION || nextPosition == itemCount)
      return

    // Update view
    notifyItemChanged(nextPosition, TestStepView.Payload.UPDATE_STEP_STATE)
  }

  fun stepIn(position: Int){
    var positionToBeUpdated = position
    val targetStep = getItem(position)
    targetStep.testStep.stepIn()
    if (targetStep.testStep.type != TimelineView.Type.START){
      positionToBeUpdated = position.dec()
      getItem(positionToBeUpdated).testStep.stepOut()
    }
    notifyItemChanged(positionToBeUpdated, TestStepView.Payload.UPDATE_STEP_STATE)
    recyclerView.smoothScrollToPosition(positionToBeUpdated)
  }

  private fun getItem(position: Int) = itemViewList[position]

  override fun getItemViewType(position: Int): Int {
    getItem(position).apply {
      typeItemViewMap.getOrPut(getViewType(), { this })
      return getViewType().type
    }
  }

  override fun getItemCount(): Int = itemViewList.size
}