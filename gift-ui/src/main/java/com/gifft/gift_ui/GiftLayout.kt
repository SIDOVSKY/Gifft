package com.gifft.gift_ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.graphics.withTranslation
import androidx.core.view.children
import kotlinx.android.parcel.Parcelize
import kotlin.math.max
import kotlin.properties.Delegates

class GiftLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val scaleMatrix = Matrix()
    private var scaleFactor = 1f
    private var touchStartX = 0f

    private var touchStartY = 0f
    private var capOffset = 0
    private var maxCapOffset = 0

    private val strokePaint = Paint().apply {
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val capPathSource = Path().apply {
        val leftBowPath = Path().apply {
            moveTo(15f, 20f)
            cubicTo(-10f, -10f, 50f, 0f, 50f, 20f)
        }

        val mirrorXMatrix = Matrix().apply {
            setScale(-1f, 1f, 50f, 0f)
        }

        val rightBowPath = Path(leftBowPath).apply {
            transform(mirrorXMatrix)
        }

        addPath(leftBowPath)
        addPath(rightBowPath)
        addRect(0f, 20f, 100f, 40f, Path.Direction.CW)
        addRect(40f, 20f, 60f, 40f, Path.Direction.CCW)
    }

    private val boxPathSource = Path().apply {
        addRect(10f, 0f, 90f, 60f, Path.Direction.CW)
        addRect(40f, 0f, 60f, 60f, Path.Direction.CCW) // Why not drawn with CW??
    }

    private val capSourceSize = Size(100, 40)
    private val boxSourceSize = Size(100, 60)

    private val capPath = Path()
    private val boxPath = Path()

    private var capSize = Size()
    private var boxSize = Size()

    private val closedChildren = mutableListOf<View>()

    private val initialOpened: Boolean

    var isInteractive: Boolean

    init {
        isSaveEnabled = true

        val ta =
            context.obtainStyledAttributes(attrs, R.styleable.GiftLayout, 0, 0)
        try {
            initialOpened = ta.getBoolean(R.styleable.GiftLayout_opened, false)
            isInteractive = ta.getBoolean(R.styleable.GiftLayout_interactive, true)
            strokePaint.color = ta.getColor(R.styleable.GiftLayout_giftColor, Color.BLACK)
        } finally {
            ta.recycle()
        }
    }

    var isOpened: Boolean by Delegates.observable(initialOpened) { _, old, new ->
        if (new != old) {
            onOpenedChanged(new)
        }
    }

    var giftColor: Int
        get() = strokePaint.color
        set(value) {
            strokePaint.color = value
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        var height = MeasureSpec.getSize(heightMeasureSpec)
        var width = MeasureSpec.getSize(widthMeasureSpec)

        val giftSourceHeight = capSourceSize.height + boxSourceSize.height
        val giftRatio = max(capSourceSize.width, boxSourceSize.width).toFloat() / giftSourceHeight

        val giftWidth: Int
        val giftHeight: Int

        if (!isOpened) {
            closedChildren.addAll(children)
            removeAllViewsInLayout()
        } else {
            closedChildren.forEachIndexed { index, view ->
                addViewInLayout(view, index, view.layoutParams, true)
            }
            closedChildren.clear()
        }

        when {
            widthMode == MeasureSpec.EXACTLY -> {
                giftWidth =
                    if (width.toFloat() / height > giftRatio) (height * giftRatio).toInt()
                    else width

                giftHeight = (giftWidth / giftRatio).toInt()

                val maxChildrenSize = measureAllChildren(
                    MeasureSpec.makeMeasureSpec(width, widthMode),
                    MeasureSpec.makeMeasureSpec(height - giftHeight, heightMode)
                )

                if (heightMode == MeasureSpec.AT_MOST) {
                    height = giftHeight + maxChildrenSize.height
                }
            }
            widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST -> {
                val maxChildrenSize = measureAllChildren(
                    MeasureSpec.makeMeasureSpec(width, widthMode),
                    MeasureSpec.makeMeasureSpec(height, heightMode)
                )

                val availableGiftHeight = height - maxChildrenSize.height

                if (availableGiftHeight == 0 || width / availableGiftHeight > giftRatio) {
                    giftHeight = availableGiftHeight
                    //giftWidth = (availableGiftHeight * giftRatio).toInt()
                } else {
                    giftWidth = maxChildrenSize.width
                    giftHeight = (giftWidth / giftRatio).toInt()
                }

                height = giftHeight + maxChildrenSize.height
                width = maxChildrenSize.width
            }
            widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY -> {
                val maxChildrenSize = measureAllChildren(
                    MeasureSpec.makeMeasureSpec(width, widthMode),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
                )

                val availableGiftHeight = height - maxChildrenSize.height

                giftWidth =
                    if (width.toFloat() / availableGiftHeight > giftRatio)
                        (availableGiftHeight * giftRatio).toInt()
                    else
                        width

                giftHeight = (giftWidth / giftRatio).toInt()

                width = max(giftWidth, maxChildrenSize.width)
            }
            else -> throw IllegalStateException("Unsupported measure modes")
        }

        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(width, widthMode),
            MeasureSpec.makeMeasureSpec(height, heightMode)
        )

        scaleFactor = giftHeight / giftSourceHeight.toFloat()
        scaleMatrix.setScale(scaleFactor, scaleFactor)

        capSize.width = (capSourceSize.width * scaleFactor).toInt()
        capSize.height = (capSourceSize.height * scaleFactor).toInt()

        boxSize.width = (boxSourceSize.width * scaleFactor).toInt()
        boxSize.height = (boxSourceSize.height * scaleFactor).toInt()

        capPathSource.transform(scaleMatrix, capPath)
        boxPathSource.transform(scaleMatrix, boxPath)

        maxCapOffset = height - (capSize.height + boxSize.height)
        capOffset = if (isOpened) maxCapOffset else 0
    }


    /**
     * @return max size among children
     */
    private fun measureAllChildren(widthMeasureSpec: Int, heightMeasureSpec: Int): Size {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)

        var childrenMaxWidth = 0
        var childrenMaxHeight = 0

        children
            .plus(closedChildren.asSequence())
            .filter { it.visibility != View.GONE }
            .forEach {
                val lp = it.layoutParams as MarginLayoutParams

                measureChild(
                    it,
                    MeasureSpec.makeMeasureSpec(
                        width - lp.leftMargin - lp.rightMargin,
                        widthMode
                    ),
                    MeasureSpec.makeMeasureSpec(
                        height - lp.topMargin - lp.bottomMargin,
                        heightMode
                    )
                )

                childrenMaxHeight =
                    max(childrenMaxHeight, it.measuredHeight + lp.topMargin + lp.bottomMargin)
                childrenMaxWidth =
                    max(childrenMaxWidth, it.measuredWidth + lp.leftMargin + lp.rightMargin)
            }

        return Size(childrenMaxWidth, childrenMaxHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val childrenAreaTop = capSize.height
        val childrenAreaBottom = bottom - top - boxSize.height
        val childrenAreaHeight = childrenAreaBottom - childrenAreaTop

        children
            .filter { it.visibility != View.GONE }
            .forEach {
                val childTop =
                    childrenAreaTop + (childrenAreaHeight - it.measuredHeight) / 2

                val childLeft = (right - left - it.measuredWidth) / 2

                it.layout(
                    childLeft,
                    childTop,
                    childLeft + it.measuredWidth,
                    childTop + it.measuredHeight
                )
            }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (capOffset == maxCapOffset) {
            super.dispatchDraw(canvas)
        }

        canvas.apply {
            scale(0.99f, 0.99f, width / 2f, height / 2f) // To fit edge strokes

            withTranslation(
                (width - capSize.width) / 2f,
                (height - boxSize.height - capSize.height - capOffset).toFloat()
            ) {
                drawPath(capPath, strokePaint)
            }

            withTranslation((width - boxSize.width) / 2f, (height - boxSize.height).toFloat()) {
                drawPath(boxPath, strokePaint)
            }
        }
    }

    override fun checkLayoutParams(p: LayoutParams) = p is MarginLayoutParams

    override fun generateLayoutParams(attrs: AttributeSet) = MarginLayoutParams(context, attrs)

    override fun generateLayoutParams(p: LayoutParams) = MarginLayoutParams(p)

    override fun removeView(view: View?) {
        if (!isOpened) {
            closedChildren.remove(view)
        } else {
            super.removeView(view)
        }
    }

    override fun removeAllViews() {
        if (!isOpened) {
            closedChildren.clear()
        } else {
            super.removeAllViews()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isInteractive)
            return false

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStartX = event.x
                touchStartY = event.y
                true
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = touchStartY - event.y

                capOffset = (
                        if (isOpened) maxCapOffset + dy
                        else dy
                        )
                    .toInt()
                    .coerceIn(0..maxCapOffset)

                invalidate()
                true
            }
            MotionEvent.ACTION_UP -> {
                positionCap()
                true
            }
            else -> false
        }
    }

    override fun onSaveInstanceState(): Parcelable? =
        SavedState(super.onSaveInstanceState(), isOpened, isInteractive)

    override fun onRestoreInstanceState(state: Parcelable?) {
        (state as? SavedState)?.let {
            super.onRestoreInstanceState(it.superSavedState)
            isOpened = it.isOpened
            isInteractive = it.isInteractive
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        super.dispatchSaveInstanceState(container)

        closedChildren.forEach { it.saveHierarchyState(container) }
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        super.dispatchRestoreInstanceState(container)

        closedChildren.forEach { it.restoreHierarchyState(container) }
    }


    private fun positionCap() {
        val shouldOpen = capOffset > maxCapOffset / 3

        val newCapOffset = if (shouldOpen) maxCapOffset else 0

        ValueAnimator().apply {
            setIntValues(capOffset, newCapOffset)
            duration = 300
            addUpdateListener { animation ->
                capOffset = animation.animatedValue as Int
                invalidate()
            }
            start()
            doOnEnd {
                isOpened = shouldOpen
            }
        }
    }

    private fun onOpenedChanged(opened: Boolean) {
        if (opened) {
            positionCap()
            requestLayout()
        } else {
            requestLayout()
            positionCap()
        }
    }

    @Parcelize
    private class SavedState(
        val superSavedState: Parcelable?,
        val isOpened: Boolean,
        val isInteractive: Boolean
    ) : Parcelable

}

data class Size(var width: Int = 0, var height: Int = 0)
