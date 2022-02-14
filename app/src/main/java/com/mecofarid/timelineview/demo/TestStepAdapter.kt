package com.mecofarid.timelineview.demo

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import androidx.viewbinding.ViewBinding


class TestStepAdapter(
  private val context: Context
): RecyclerView.Adapter<TestStepView.TestStepViewHolder<TestStep, ViewBinding>>() {
  private val typeItemViewMap = mutableMapOf<TestStepView.ViewType, TestStepView<*, *, *>>()
  private val itemViewList = mutableListOf<TestStepView<*, * ,*>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestStepView.TestStepViewHolder<TestStep, ViewBinding> =
    with(TestStepView.ViewType.toViewType(viewType)) {
      typeItemViewMap.getValue(this).newViewHolder(parent) as TestStepView.TestStepViewHolder<TestStep, ViewBinding>
    }

  override fun onBindViewHolder(holderTestStep: TestStepView.TestStepViewHolder<TestStep, ViewBinding>, position: Int) =
    with(getItem(position).testStep) {
      holderTestStep.bind(this)
    }

  private fun getItem(position: Int) = itemViewList[position]

  override fun getItemViewType(position: Int): Int {
    getItem(position).apply {
      typeItemViewMap.getOrPut(getViewType(), { this })
      return getViewType().type
    }
  }

  fun updateDataSet(list: List<TestStepView<*, * ,*>>){
    itemViewList.addAll(list)
    notifyDataSetChanged()
  }

  override fun getItemCount(): Int = itemViewList.size
}