package com.owner.basemodule.util.calender

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView

/**
 * 简单的带事务下标的月视图
 *
 */

class CustomMonthView(context: Context) : MonthView(context) {

    private var mRadius: Int = 0

    /**
     * 自定义标记的文本画笔
     */
    private val mTextPaint = Paint()

    /**
     * 今天的背景色
     */
    private val mCurrentDayPaint = Paint()

    init {
        mTextPaint.color = -0x1
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER

        mCurrentDayPaint.isAntiAlias = true
        mCurrentDayPaint.style = Paint.Style.FILL
        mCurrentDayPaint.color = -0x151516

    }

    override fun onPreviewHook() {
        mTextPaint.textSize = mCurMonthLunarTextPaint.textSize
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5
    }

    /**
     * 选择日期绘制一个圆，返回true，则同时会调用onDrawScheme绘制标记
     */

    override fun onDrawSelected(canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2

        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
        return true
    }

    /**
     * 在日期下方绘制事务标记
     */
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        val cx = x + mItemWidth / 2

        val isSelected = isSelected(calendar)
        if (isSelected) {
            mTextPaint.color = Color.WHITE
        } else {
            mTextPaint.color = calendar.schemeColor
        }

        canvas.drawText(
            calendar.scheme, cx.toFloat(), mTextBaseLine + y.toFloat() + (mItemHeight / 10).toFloat(),
            mTextPaint
        )
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        val top = y - mItemHeight / 6

        if (calendar.isCurrentDay && !isSelected) {
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mCurrentDayPaint)
        }
        //当然可以换成其它对应的画笔就不麻烦，
        if (calendar.isWeekend && calendar.isCurrentMonth) {
            mCurMonthTextPaint.color = -0xb76201
            mOtherMonthTextPaint.color = -0xb76201
        } else {
            mCurMonthTextPaint.color = -0xcccccd
            mOtherMonthTextPaint.color = -0x1e1e1f
        }
        when {
            isSelected -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    mSelectTextPaint
                )
            }
            hasScheme -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    if (calendar.isCurrentMonth) mSchemeTextPaint else mOtherMonthTextPaint
                )
            }
            else -> {
                canvas.drawText(
                    calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                    when {
                        calendar.isCurrentDay -> mCurDayTextPaint
                        calendar.isCurrentMonth -> mCurMonthTextPaint
                        else -> mOtherMonthTextPaint
                    }
                )
            }
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private fun dipToPx(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
