## Description
Animated TimeLineView for stepped tasks.

## Demo

![android](https://user-images.githubusercontent.com/17815721/221888038-964b169e-7c45-4933-8e1a-510e113f1b59.gif)


## Usage
### 1. Integration
Min API level is: `API 21` [![](https://jitpack.io/v/mecoFarid/TimeLineView.svg)](https://jitpack.io/#mecoFarid/TimeLineView)

**Step 1.** Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**Step 2.** Add the dependency
```
dependencies {
    ...  
    implementation 'com.github.mecoFarid:TimeLineView:1.0'
}
```

### 3. Code example
After integrating the library to your app you can use the `TimelineView` in your xml as below:

```
<com.mecofarid.timelineview.TimelineView
    android:id="@+id/timeline"
    android:layout_width="16dp"
    android:layout_height="0dp"
    android:layout_marginStart="30dp"
    android:layout_marginEnd="50dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:activeLineColor="#0089CA"
    app:activeMarkerColor="#0089CA"
    app:activeMarkerInnerRadius="4dp"
    app:activeMarkerRingInnerRadius="7dp"
    app:activeMarkerRingStroke="2dp"
    app:inactiveLineColor="#E3E4E4"
    app:inactiveMarkerColor="#E3E4E4"
    app:inactiveMarkerInnerRadius="6dp"
    app:inactiveMarkerRingStroke="2dp"
    app:lineStroke="2dp"
    app:markerTopSpacing="40dp"
    app:stepInAnimationDuration="100"
    app:stepOutAnimationDuration="300" />
```
**Note:** You can find implementation in [Demo App](https://github.com/mecoFarid/TimeLineView/tree/master/app)
