package com.mecofarid.timelineview.demo.step

import android.content.Context
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.mecofarid.timelineview.TimelineView
import com.mecofarid.timelineview.demo.R
import com.mecofarid.timelineview.demo.databinding.StepViewBinding

internal const val RECYCLERVIEW_CACHE_SIZE = 2

class TestStepAdapter(
  private val context: Context,
  private val itemViewList: List<StepView<*, *>>,
  internal val player: Lazy<ExoPlayer>
): RecyclerView.Adapter<StepView.TestStepViewHolder<TestStep>>() {
  private val typeItemViewMap = mutableMapOf<StepView.ViewType, StepView<*, *>>()
  private lateinit var recyclerView: RecyclerView
  private val scrollListener = object : RecyclerView.OnScrollListener() {
    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
      super.onScrolled(recyclerView, dx, dy)
      // If scrolling down, force scroll to last stepped-in position
      if (dy > 0){
        recyclerView.smoothSnapToPosition(findLastSteppedPosition())
      }

      stopVideoPlayerIfLastSteppedInStepNotVisible()
    }
  }
  internal val handler = Handler(context.mainLooper)
  private val scrollDuration by lazy {
    context.resources.run {
      getInteger(R.integer.test_step_view_step_in_animation) + getInteger(R.integer.test_step_view_step_out_animation)
    }
  }

  private fun stopVideoPlayerIfLastSteppedInStepNotVisible(){
    val lastSteppedPosition = findLastSteppedPosition()
    (recyclerView.layoutManager as LinearLayoutManager).apply {
      val firstBoundItemPosition = findFirstVisibleItemPosition() - RECYCLERVIEW_CACHE_SIZE
      val lastBoundItemPosition = findLastVisibleItemPosition() + RECYCLERVIEW_CACHE_SIZE
      if (lastSteppedPosition < firstBoundItemPosition || lastSteppedPosition > lastBoundItemPosition)
        getItem(lastSteppedPosition).steppedInViewOutsideBoundArea()
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
    notifyItemChanged(positionToBeUpdated, StepView.Payload.UPDATE_STEP_STATE)
    recyclerView.smoothSnapToPosition(positionToBeUpdated)
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    this.recyclerView = recyclerView
    recyclerView.addOnScrollListener(scrollListener)
  }

  private fun findLastSteppedPosition() : Int{
    itemViewList.forEachIndexed { index, testStepView ->
      if (testStepView.testStep.state.isAnyStepInState()){
        return index
      }
    }
    return 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepView.TestStepViewHolder<TestStep> =
    with(StepView.ViewType.toViewType(viewType)) {
      val testStepViewBinding = StepViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      typeItemViewMap.getValue(this).newViewHolder(testStepViewBinding) as StepView.TestStepViewHolder<TestStep>
    }

  override fun onViewRecycled(holder: StepView.TestStepViewHolder<TestStep>) {
    super.onViewRecycled(holder)
    removePlayerListener(holder)
  }

  private fun removePlayerListener(holder: StepView.TestStepViewHolder<*>){
    // Remove video listener
    if (holder is VideoStepView.ViewHolder)
      holder.removeListener()
  }

  override fun onBindViewHolder(holderTestStep: StepView.TestStepViewHolder<TestStep>, position: Int) =
    holderTestStep.bind(getItem(position).testStep)

  override fun onBindViewHolder(holder: StepView.TestStepViewHolder<TestStep>, position: Int, payloads: List<Any>) {
    // Bind whole view only if it is triggered by system
    if (!payloads.contains(StepView.Payload.UPDATE_STEP_STATE))
      onBindViewHolder(holder, position)

    updateState(holder)
  }

  private fun updateState(holder: StepView.TestStepViewHolder<TestStep>) = with(holder) {
    val currentStep = getItem(bindingAdapterPosition).testStep
    updateState {
      if (currentStep.state.isStepOutState())
        notifyNextStep(bindingAdapterPosition)
      finishStepAt(bindingAdapterPosition)
    }
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
    notifyItemChanged(nextPosition, StepView.Payload.UPDATE_STEP_STATE)
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
      override fun calculateTimeForScrolling(dx: Int): Int = scrollDuration
    }
    smoothScroller.targetPosition = position
    removeOnScrollListener(scrollListener)
    layoutManager?.startSmoothScroll(smoothScroller)
    addOnScrollListener(scrollListener)
  }
}