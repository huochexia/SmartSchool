package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.lifecycle.LiveData
import com.goldenstraw.restaurant.goodsmanager.repositories.GoodsRepository
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.room.entities.GoodsCategory

class CategoryViewModel(
   var category: GoodsCategory
) : BaseViewModel() {


}