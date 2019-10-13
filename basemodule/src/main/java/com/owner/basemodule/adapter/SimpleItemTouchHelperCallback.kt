package com.owner.basemodule.adapter

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 定义RecyclerView拖动和侧滑辅助类，以及拖拽或侧滑时执行的接口方法
 */
class SimpleItemTouchHelperCallback(val listener: OnMoveAndSwipeListener) :
    ItemTouchHelper.Callback() {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (recyclerView.layoutManager is LinearLayoutManager) {
            //设置拖拽方向上下
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            //设置侧滑方向从左到右和从右到左都可以
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            makeMovementFlags(dragFlags, swipeFlags)
        } else {
            //设置拖拽方向上下左右
            val dragFlag =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            //不支持侧滑
            val swipeFlags = 0
            makeMovementFlags(dragFlag, swipeFlags)
        }
    }

    /**
     * 拖动时调用此方法
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        //如果两个item不是一个类型的，不可以拖拽
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }
        //回调adapter中的onItemMove方法
        listener.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        //回调侧滑时发生的方法
        listener.onItemDismiss(viewHolder.adapterPosition)
    }
}

/**
 * Adapter要实现这个接口，它是Adapter与ItemTouchHelper联系的纽带.由Adapter实现当拖拽或滑动时发生的具体行为
 */
interface OnMoveAndSwipeListener {
    fun onItemMove(frmPosition: Int, toPosition: Int): Boolean
    fun onItemDismiss(position: Int)
}
