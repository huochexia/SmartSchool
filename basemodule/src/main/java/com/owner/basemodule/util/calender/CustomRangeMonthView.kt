package com.owner.basemodule.util.calender

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import com.haibin.calendarview.Calendar
import com.haibin.calendarview.RangeMonthView

/**
 * 范围选择月视图
 *
 */

class CustomRangeMonthView(context: Context) : RangeMonthView(context) {

    private var mRadius: Int = 0


    override fun onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2
    }

    override fun onDrawSelected(
        canvas: Canvas, calendar: Calendar, x: Int, y: Int, hasScheme: Boolean,
        isSelectedPre: Boolean, isSelectedNext: Boolean
    ): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadius).toFloat(),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
            } else {//最后一个，the last
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadius).toFloat(),
                    cx.toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
                canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(
                    cx.toFloat(),
                    (cy - mRadius).toFloat(),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
            }
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
            //
        }

        return false
    }

    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int, isSelected: Boolean) {

    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2

        val isInRange = isInRange(calendar)
        val isEnable = !onCalendarIntercept(calendar)
        //当然可以换成其它对应的画笔就不麻烦，
        if (calendar.isWeekend && calendar.isCurrentMonth) {
            mCurMonthTextPaint.color = -0xb76201
            mOtherMonthTextPaint.color = -0xb76201
        } else {
            mCurMonthTextPaint.color = -0xcccccd
            mOtherMonthTextPaint.color = -0x1e1e1f
        }
        when {
            isSelected -> canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                mSelectTextPaint
            )
            else -> canvas.drawText(
                calendar.day.toString(), cx.toFloat(), baselineY,
                if (calendar.isCurrentDay)
                    mCurDayTextPaint
                else if (calendar.isCurrentMonth && isInRange && isEnable) mCurMonthTextPaint else mOtherMonthTextPaint
            )
        }
    }
}
