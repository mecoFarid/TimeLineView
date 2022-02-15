package com.mecofarid.timelineview.demo

import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.mecofarid.timelineview.TimelineView

class TestStepAdapter(
  private val itemViewList: List<TestStepView<*, * ,*>>
): RecyclerView.Adapter<TestStepView.TestStepViewHolder<TestStep, ViewBinding>>() {
  private val typeItemViewMap = mutableMapOf<TestStepView.ViewType, TestStepView<*, *, *>>()

  private lateinit var recyclerView: RecyclerView
  private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)
      // If scrolling down, force scroll to last stepped-in position
      if (dy > 0){
        recyclerView.smoothSnapToPosition(findLastSteppedPosition())
      }
    }
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
    recyclerView.smoothSnapToPosition(positionToBeUpdated)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView
    recyclerView.addOnScrollListener(scrollListener)
  }

  private fun findLastSteppedPosition() : Int{
    itemViewList.forEachIndexed { index, testStepView ->
      if (testStepView.testStep.state.isStepInState()){
        return index
      }
    }
    return 0
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
        if (currentStep.isStepOutState())
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

  private fun getItem(position: Int) = itemViewList[position]

  override fun getItemViewType(position: Int): Int {
    getItem(position).apply {
      typeItemViewMap.getOrPut(getViewType(), { this })
      return getViewType().type
    }
  }

  override fun getItemCount(): Int = itemViewList.size

  private fun RecyclerView.smoothSnapToPosition(position: Int) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
      override fun getVerticalSnapPreference(): Int = SNAP_TO_START
      override fun calculateTimeForScrolling(dx: Int): Int = 250
    }
    smoothScroller.targetPosition = position
    removeOnScrollListener(scrollListener)
    layoutManager?.startSmoothScroll(smoothScroller)
    addOnScrollListener(scrollListener)
  }
}