package com.mecofarid.timelineview.demo

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
import com.mecofarid.timelineview.demo.databinding.TestStepViewBinding

internal const val SEEK_INCREMENT = 5_000L
internal const val RECYCLERVIEW_CACHE_SIZE = 2 // Same as RecyclerView.Recycler.DEFAULT_CACHE_SIZE

class TestStepAdapter(
  private val context: Context,
  private val itemViewList: List<TestStepView<*,*>>,
  internal val player: Lazy<ExoPlayer>
): RecyclerView.Adapter<TestStepView.TestStepViewHolder<TestStep>>() {
  private val typeItemViewMap = mutableMapOf<TestStepView.ViewType, TestStepView<*, *>>()
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
      if (testStepView.testStep.state.isAnyStepInState()){
        return index
      }
    }
    return 0
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestStepView.TestStepViewHolder<TestStep> =
    with(TestStepView.ViewType.toViewType(viewType)) {
      val testStepViewBinding = TestStepViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      typeItemViewMap.getValue(this).newViewHolder(testStepViewBinding) as TestStepView.TestStepViewHolder<TestStep>
    }

  override fun onViewRecycled(holder: TestStepView.TestStepViewHolder<TestStep>) {
    super.onViewRecycled(holder)
    removePlayerListener(holder)
  }

  private fun removePlayerListener(holder: TestStepView.TestStepViewHolder<*>){
    // Remove video listener
    if (holder is VideoTestStepView.ViewHolder)
      holder.removeListener()
  }

  override fun onBindViewHolder(holderTestStep: TestStepView.TestStepViewHolder<TestStep>, position: Int) =
    holderTestStep.bind(getItem(position).testStep)

  override fun onBindViewHolder(holder: TestStepView.TestStepViewHolder<TestStep>, position: Int, payloads: List<Any>) {
    // Bind whole view only if it is triggered by system
    if (!payloads.contains(TestStepView.Payload.UPDATE_STEP_STATE))
      onBindViewHolder(holder, position)

    updateState(holder)
  }

  private fun updateState(holder: TestStepView.TestStepViewHolder<TestStep>) = with(holder) {
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
      override fun calculateTimeForScrolling(dx: Int): Int = scrollDuration
    }
    smoothScroller.targetPosition = position
    removeOnScrollListener(scrollListener)
    layoutManager?.startSmoothScroll(smoothScroller)
    addOnScrollListener(scrollListener)
  }
}