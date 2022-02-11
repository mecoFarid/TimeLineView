package com.mecofarid.timelineview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

private const val LINE_WIDTH = 5f
private const val INACTIVE_LINE_ALPHA = 40
private const val ACTIVE_LINE_ALPHA = 100
private const val ANIMATION_DURATION =  500L
private const val MARKER_START_SPACING = 100f
private const val INACTIVE_MARKER_RADIUS = 15f
private const val ACTIVE_MARKER_OUTER_RADIUS = 20f
private const val ACTIVE_MARKER_INNER_RADIUS = 10f

class TimelineView(
  context: Context?,
  attrs: AttributeSet?
) : View(context, attrs) {

  private val linePaint = lazy {
    Paint(ANTI_ALIAS_FLAG).apply {
      strokeWidth = LINE_WIDTH
      style = Paint.Style.STROKE
    }
  }
  private val markerPaint = lazy { Paint(ANTI_ALIAS_FLAG) }
  private var horizontalCenter = 0f
  private var activeLineEndPoint = 0f
    set(value) {
      field = value
      postInvalidateOnAnimation()
    }
  private lateinit var type: Type
  private lateinit var state: State
  private var viewLength: Float = 0f
  private val inactiveStateMarkerCenter = lazy {
    PointF(
      horizontalCenter,
      MARKER_START_SPACING + INACTIVE_MARKER_RADIUS
    )
  }
  private val activeStateMarkerCenter = lazy {
    PointF(
      horizontalCenter,
      MARKER_START_SPACING + ACTIVE_MARKER_OUTER_RADIUS
    )
  }
  private var animationEnded = false

  fun setState(state: State) {
    this.state = state
  }

  fun setType(type: Type) {
    this.type = type
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    viewLength = h.toFloat()
    horizontalCenter = w / 2f
    startAnimation()
  }

  private fun startAnimation(){
    if (isInactiveState())
      return

    getValueAnimator().apply {
      addUpdateListener { activeLineEndPoint = it.animatedValue as Float }
      doOnEnd { animationEnded = true }
      duration = getAnimationDuration()
      interpolator = LinearInterpolator()
    }.start()
  }

  private fun getAnimationDuration(): Long =
    when (state) {
      State.INACTIVE,
      State.STEP_IN,
      State.STEP_OUT -> ANIMATION_DURATION
      State.STEP_IN_FINISHED,
      State.STEP_OUT_FINISHED -> 0
    }

  private fun getValueAnimator() =
    ValueAnimator.ofFloat(
      getLineInitialLength(),
      getLineFinalLength()
    )

  private fun getLineInitialLength(): Float =
    when (state) {
      State.INACTIVE,
      State.STEP_IN,
      State.STEP_IN_FINISHED -> getLineToMarkerInitialLength()
      State.STEP_OUT,
      State.STEP_OUT_FINISHED -> getLineFromMarkerInitialLength()
    }

  private fun getLineToMarkerInitialLength(): Float =
    when (type) {
      Type.START -> MARKER_START_SPACING
      Type.MIDDLE,
      Type.END -> 0f
    }

  private fun getLineToMarkerFinalLength(): Float =
    when (type) {
      Type.START,
      Type.MIDDLE,
      Type.END -> MARKER_START_SPACING
    }

  private fun getLineFinalLength(): Float =
    when (state) {
      State.INACTIVE,
      State.STEP_IN,
      State.STEP_IN_FINISHED -> getLineToMarkerFinalLength()
      State.STEP_OUT,
      State.STEP_OUT_FINISHED -> getLineFromMarkerFinalLength()
    }

  private fun getLineFromMarkerInitialLength(): Float {
    return when (type) {
      Type.START,
      Type.MIDDLE,
      Type.END -> MARKER_START_SPACING + getMarkerOuterDiameter()
    }
  }

  private fun getLineFromMarkerFinalLength(): Float {
    return when (type) {
      Type.START,
      Type.MIDDLE -> viewLength
      Type.END -> MARKER_START_SPACING + getMarkerOuterDiameter()
    }
  }

  private fun getMarkerOuterDiameter(): Float = 2 * getMarkerOuterRadius()

  private fun getMarkerOuterRadius(): Float =
    if (state == State.INACTIVE)
      INACTIVE_MARKER_RADIUS
    else
      ACTIVE_MARKER_OUTER_RADIUS

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.drawInactiveState()
    canvas.drawActiveState()
  }

  private fun Canvas.drawActiveState(){
    if (isInactiveState())
      return

    drawActiveLine()
    if (isActiveMarkerEnabled())
      drawActiveStateMarker()
  }

  private fun Canvas.drawInactiveState(){
    drawInactiveLine()
    if (!isActiveMarkerEnabled())
      drawInactiveStateMarker()
  }

  private fun Canvas.drawActiveLine() {
    linePaintAsActiveState()
    linePaint.value.also {
      drawHorizontalCenteredLine(getLineInitialLength(), activeLineEndPoint, linePaint.value)
      if (isActiveMarkerEnabled()) {
        drawHorizontalCenteredLine(getLineToMarkerInitialLength(), getLineToMarkerFinalLength(), paint = it)
      }
    }
  }

  private fun Canvas.drawInactiveLine() {
    linePaintAsInactiveState()
    linePaint.value.also {
      drawHorizontalCenteredLine(getLineToMarkerInitialLength(), getLineToMarkerFinalLength(), paint = it)
      drawHorizontalCenteredLine(getLineFromMarkerInitialLength(), getLineFromMarkerFinalLength(), paint = it)
    }
  }

  private fun Canvas.drawActiveStateMarker() {
    markerPaintAsActiveStateInnerRadius()
    activeStateMarkerCenter.value.apply {
      drawCircle(x, y, ACTIVE_MARKER_INNER_RADIUS, markerPaint.value)
    }
    markerPaintAsActiveStateOuterRadius()
    activeStateMarkerCenter.value.apply {
      drawCircle(x, y, ACTIVE_MARKER_OUTER_RADIUS, markerPaint.value)
    }
  }

  private fun Canvas.drawInactiveStateMarker() {
    markerPaintAsInactiveState()
    inactiveStateMarkerCenter.value.apply {
      drawCircle(x, y, INACTIVE_MARKER_RADIUS, markerPaint.value)
    }
  }

  private fun isInactiveState() = state == State.INACTIVE

  private fun isActiveMarkerEnabled() = animationEnded || state == State.STEP_IN_FINISHED || isOutState()

//  private fun isTypeMiddleAndOutState() = type == Type.MIDDLE && isOutState()

  private fun isOutState() = state == State.STEP_OUT || state == State.STEP_OUT_FINISHED

  private fun Canvas.drawHorizontalCenteredLine(startY: Float, stopY: Float, paint: Paint) =
    drawLine(horizontalCenter, startY, horizontalCenter, stopY, paint)

  private fun linePaintAsActiveState() =
    linePaint.value.apply {
      color = Color.BLUE
      alpha = ACTIVE_LINE_ALPHA
    }

  private fun linePaintAsInactiveState() =
    linePaint.value.apply {
      color = Color.GRAY
      alpha = INACTIVE_LINE_ALPHA
    }

  private fun markerPaintAsInactiveState() =
    markerPaint.value.apply {
      color = Color.GRAY
      style = Paint.Style.STROKE
      strokeWidth = LINE_WIDTH
    }

  private fun markerPaintAsActiveStateInnerRadius() =
    markerPaint.value.apply {
      color = Color.BLUE
      style = Paint.Style.FILL
    }

  private fun markerPaintAsActiveStateOuterRadius() =
    markerPaint.value.apply {
      color = Color.BLUE
      style = Paint.Style.STROKE
      strokeWidth = LINE_WIDTH
    }

  enum class State {
    INACTIVE,
    STEP_IN,
    STEP_OUT,
    STEP_IN_FINISHED,
    STEP_OUT_FINISHED,
  }

  enum class Type {
    START,
    MIDDLE,
    END
  }
}