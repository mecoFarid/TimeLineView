package com.mecofarid.timelineview.demo

import javax.microedition.khronos.egl.EGL10

import android.opengl.GLES20
import android.view.Surface
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.egl.EGLSurface

private val ATTRIB_LIST = intArrayOf(
  EGL10.EGL_RED_SIZE, 8,
  EGL10.EGL_GREEN_SIZE, 8,
  EGL10.EGL_BLUE_SIZE, 8,
  EGL10.EGL_ALPHA_SIZE, 8,
  EGL10.EGL_NONE
)

// http://stackoverflow.com/a/21564236/2681195
fun Surface?.clear() {
  val egl = EGLContext.getEGL() as EGL10
  val display: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)
  val version = IntArray(2)
  egl.eglInitialize(display, version)
  val configs: Array<EGLConfig?> = arrayOfNulls<EGLConfig>(1)
  val numConfig = IntArray(1)
  egl.eglChooseConfig(display, ATTRIB_LIST, configs, 1, numConfig)
  val surface: EGLSurface = egl.eglCreateWindowSurface(display, configs[0], this, null)
  val context: EGLContext = egl.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, null)
  egl.eglMakeCurrent(display, surface, surface, context)
  GLES20.glClearColor(0f, 0f, 0f, 0f)
  GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
  egl.eglSwapBuffers(display, surface)
  egl.eglMakeCurrent(display, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT)
  egl.eglDestroyContext(display, context)
  egl.eglDestroySurface(display, surface)
}
