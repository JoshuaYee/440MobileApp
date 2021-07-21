package com.flexedev.twobirds_onescone.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.flexedev.twobirds_onescone.RecyclerViewAdapter
import com.flexedev.twobirds_onescone.viewModel.SconeViewModel

class SwipeToDelete(var adapter: RecyclerViewAdapter, viewModel: SconeViewModel) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    var view: SconeViewModel = viewModel

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.absoluteAdapterPosition
        adapter.deleteItem(pos)

    }
}

