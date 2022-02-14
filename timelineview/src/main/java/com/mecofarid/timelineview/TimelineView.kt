package com.mecofarid.timelineview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd

private const val LINE_STROKE = 5f
private const val ANIMATION_DURATION =  250
private const val MARKER_START_SPACING = 100f
private const val INACTIVE_MARKER_INNER_RADIUS = 15f
private const val ACTIVE_MARKER_INNER_RADIUS = 15f
private const val ACTIVE_MARKER_RING_INNER_RADIUS = 25f

class TimelineView(
  context: Context,
  attrs: AttributeSet?,

) : View(context, attrs) {

  private val activeLinePaint = Paint(ANTI_ALIAS_FLAG)
  private val inactiveLinePaint = Paint(ANTI_ALIAS_FLAG)
  private val markerPaint = Paint(ANTI_ALIAS_FLAG)
  private var horizontalCenter = 0f
  private var activeLineEndPoint = 0f
    set(value) {
      field = value
      postInvalidateOnAnimation()
    }
  private var type: Type = Type.START
  private var state: State = State.INACTIVE
  private var viewLength: Float = 0f
  private var animationEnded = false
  private var markerStartSpacing = MARKER_START_SPACING
  private var animationDuration = ANIMATION_DURATION

  // Active marker
  private val activeMarkerCenter = lazy { PointF(horizontalCenter, markerStartSpacing + activeMarkerRingInnerRadius) }
  private var activeMarkerColor: Int = Color.BLUE
  private var activeMarkerInnerRadius: Float = ACTIVE_MARKER_INNER_RADIUS
  private var activeMarkerRingInnerRadius: Float = ACTIVE_MARKER_RING_INNER_RADIUS
  private var activeMarkerRingStroke: Float = LINE_STROKE

  // InActive marker
  private val inactiveMarkerCenter = lazy { PointF(horizontalCenter, markerStartSpacing + inactiveMarkerInnerRadius) }
  private var inactiveMarkerColor = Color.GRAY
  private var inactiveMarkerInnerRadius = INACTIVE_MARKER_INNER_RADIUS
  private var inactiveMarkerRingStroke: Float = LINE_STROKE

  init {
    val styleSet = context.obtainStyledAttributes(attrs, R.styleable.TimelineView)
    initActiveLinePaint(styleSet)
    initInactiveLinePaint(styleSet)
    initMarkers(styleSet)
    initAnimationValues(styleSet)
    styleSet.recycle()
  }

  private fun initActiveLinePaint(styleSet: TypedArray){
    activeLinePaint.apply {
      strokeWidth = styleSet.getDimension(R.styleable.TimelineView_lineStroke, LINE_STROKE)
      style = Paint.Style.STROKE
      color = styleSet.getColor(R.styleable.TimelineView_activeLineColor, Color.BLUE)
    }
  }

  private fun initInactiveLinePaint(styleSet: TypedArray){
    inactiveLinePaint.apply {
      strokeWidth = styleSet.getDimension(R.styleable.TimelineView_lineStroke, LINE_STROKE)
      style = Paint.Style.STROKE
      color = styleSet.getColor(R.styleable.TimelineView_inActiveLineColor, Color.GRAY)
    }
  }

  private fun initMarkers(styleSet: TypedArray){
    markerStartSpacing = styleSet.getDimension(R.styleable.TimelineView_markerTopSpacing, MARKER_START_SPACING)
    initActiveMarkerStyle(styleSet)
    initInactiveMarkerStyle(styleSet)
  }

  private fun initActiveMarkerStyle(styleSet: TypedArray){
    activeMarkerColor = styleSet.getColor(R.styleable.TimelineView_activeMarkerColor, Color.BLUE)
    activeMarkerInnerRadius = styleSet.getDimension(R.styleable.TimelineView_activeMarkerInnerRadius, ACTIVE_MARKER_INNER_RADIUS)
    activeMarkerRingInnerRadius = styleSet.getDimension(R.styleable.TimelineView_activeMarkerRingInnerRadius, ACTIVE_MARKER_RING_INNER_RADIUS)
    activeMarkerRingStroke = styleSet.getDimension(R.styleable.TimelineView_activeMarkerRingStroke, LINE_STROKE)

  }

  private fun initInactiveMarkerStyle(styleSet: TypedArray){
    inactiveMarkerColor = styleSet.getColor(R.styleable.TimelineView_inactiveMarkerColor, Color.GRAY)
    inactiveMarkerInnerRadius = styleSet.getDimension(R.styleable.TimelineView_inactiveMarkerInnerRadius, INACTIVE_MARKER_INNER_RADIUS)
    inactiveMarkerRingStroke = styleSet.getDimension(R.styleable.TimelineView_inactiveMarkerRingStroke, LINE_STROKE)
  }

  private fun initAnimationValues(styleSet: TypedArray){
    animationDuration = styleSet.getInteger(R.styleable.TimelineView_animationDuration, ANIMATION_DURATION)
  }

  fun setState(state: State) {
    this.state = state
    restartAnimation()
  }

  fun setType(type: Type) {
    this.type = type
  }

  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    viewLength = h.toFloat()
    horizontalCenter = w / 2f
    startAnimation()
  }

  private fun restartAnimation(){
    animationEnded = false
    postInvalidateOnAnimation()
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
      State.STEP_OUT -> animationDuration.toLong()
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
      Type.START -> markerStartSpacing
      Type.MIDDLE,
      Type.END -> 0f
    }

  private fun getLineToMarkerFinalLength(): Float =
    when (type) {
      Type.START,
      Type.MIDDLE,
      Type.END -> markerStartSpacing
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
      Type.END -> markerStartSpacing + getMarkerOuterDiameter()
    }
  }

  private fun getLineFromMarkerFinalLength(): Float {
    return when (type) {
      Type.START,
      Type.MIDDLE -> viewLength
      Type.END -> markerStartSpacing + getMarkerOuterDiameter()
    }
  }

  private fun getMarkerOuterDiameter(): Float = 2 * getMarkerOuterRadius()

  private fun getMarkerOuterRadius(): Float =
    if (state == State.INACTIVE)
      inactiveMarkerInnerRadius
    else
      activeMarkerRingInnerRadius

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
    activeLinePaint.also {
      drawHorizontalCenteredLine(getLineInitialLength(), activeLineEndPoint, paint = it)
      if (isActiveMarkerEnabled()) {
        drawHorizontalCenteredLine(getLineToMarkerInitialLength(), getLineToMarkerFinalLength(), paint = it)
      }
    }
  }

  private fun Canvas.drawInactiveLine() {
    inactiveLinePaint.also {
      drawHorizontalCenteredLine(getLineToMarkerInitialLength(), getLineToMarkerFinalLength(), paint = it)
      drawHorizontalCenteredLine(getLineFromMarkerInitialLength(), getLineFromMarkerFinalLength(), paint = it)
    }
  }

  private fun Canvas.drawActiveStateMarker() {
    markerPaintAsActiveStateInnerRadius()
    activeMarkerCenter.value.apply {
      drawCircle(x, y, activeMarkerInnerRadius, markerPaint)
    }
    markerPaintAsActiveStateOuterRadius()
    activeMarkerCenter.value.apply {
      drawCircle(x, y, activeMarkerRingInnerRadius, markerPaint)
    }
  }

  private fun Canvas.drawInactiveStateMarker() {
    markerPaintAsInactiveState()
    inactiveMarkerCenter.value.apply {
      drawCircle(x, y, inactiveMarkerInnerRadius, markerPaint)
    }
  }

  private fun isInactiveState() = state == State.INACTIVE

  private fun isActiveMarkerEnabled() = animationEnded || state == State.STEP_IN_FINISHED || isOutState()

  private fun isOutState() = state == State.STEP_OUT || state == State.STEP_OUT_FINISHED

  private fun Canvas.drawHorizontalCenteredLine(startY: Float, stopY: Float, paint: Paint) =
    drawLine(horizontalCenter, startY, horizontalCenter, stopY, paint)

  private fun markerPaintAsInactiveState() =
    markerPaint.apply {
      color = inactiveMarkerColor
      style = Paint.Style.STROKE
      strokeWidth = inactiveMarkerRingStroke
    }

  private fun markerPaintAsActiveStateInnerRadius() =
    markerPaint.apply {
      color = activeMarkerColor
      style = Paint.Style.FILL
    }

  private fun markerPaintAsActiveStateOuterRadius() =
    markerPaint.apply {
      color = activeMarkerColor
      style = Paint.Style.STROKE
      strokeWidth = activeMarkerRingStroke
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