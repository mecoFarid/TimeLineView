package com.mecofarid.timelineview.demo

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    ko {
      println("Kopper")
    }
  }

  private lateinit var kon: () -> Unit
  private fun ko(kon: () -> Unit = {}){
    this.kon = kon

    this.kon()
  }
}