package com.mecofarid.timelineview.demo

abstract class TestStepView<T: TestStep> {
  abstract class ViewHolder<T>{
    abstract fun bind(t: T)
  }
}