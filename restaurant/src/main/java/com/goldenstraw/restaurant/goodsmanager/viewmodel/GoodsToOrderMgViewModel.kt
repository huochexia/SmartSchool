package com.goldenstraw.restaurant.goodsmanager.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.goldenstraw.restaurant.goodsmanager.repositories.goods_order.GoodsRepository
import com.kennyc.view.MultiStateView
import com.owner.basemodule.base.viewmodel.BaseViewModel
import com.owner.basemodule.ext.livedata.switchMap
import com.owner.basemodule.network.parserResponse
import com.owner.basemodule.room.entities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext


/**
 * 订单管理的ViewModel
 */
class GoodsToOrderMgViewModel(
    val repository: GoodsRepository
) : BaseViewModel() {

    //列表
    var categoryList = mutableListOf<GoodsCategory>()//商品类别列表
    var goodsList = mutableListOf<Goods>() //商品列表
    var searchGoodsResultList = mutableListOf<Goods>() //根据商品名称搜索结果列表

    //加载数据过程的状态显示
    val categoryLoadState = ObservableField<Int>()
    val goodsLoadState = ObservableField<Int>()

    //观察是否刷新商品类别列表
    private val _isRefresh = MutableLiveData<Boolean>()
    val isRefresh = _isRefresh
    fun setRefresh(boolean: Boolean) {
        _isRefresh.postValue(boolean)
    }

    var selected = MutableLiveData<GoodsCategory>() //当前选择的商品类别


    /*********************************************************
     * 使用Flow方式得到数据并转化为LiveData
     ********************************************************/

    val currentCategory = MutableLiveData<String>()

    fun updateCurrentCategory(categoryId: String) {
        currentCategory.postValue(categoryId)
    }

    @ExperimentalCoroutinesApi
    val goodsListFlow = currentCategory.switchMap {
        repository.getGoodsOfCategoryFromLocalFlow(it)
            .onStart {
                goodsLoadState.set(MultiStateView.VIEW_STATE_LOADING)
            }.catch {
                goodsLoadState.set(MultiStateView.VIEW_STATE_ERROR)
            }
            .asLiveData()
    }

    @ExperimentalCoroutinesApi
    val categoryListFlow = repository.categoryFlowFromLocal
        .onStart {
            categoryLoadState.set(MultiStateView.VIEW_STATE_LOADING)
        }.catch {
            categoryLoadState.set(MultiStateView.VIEW_STATE_ERROR)
        }
        .asLiveData()

    /*
    根据商品名称进行模糊查询
     */
    private val searchName = MutableLiveData<String>()
    val searchResult = searchName.switchMap { repository.findByName(it).asLiveData() }
    fun searchGoodsFromName(name: String) {
        searchName.postValue(name)
    }

    /********************************************************
     * 编辑（增改删）数据
     *******************************************************/
    /*
     * 删除商品信息或类别
     */
    fun deleteGoods(goods: Goods) {
        launchUI {
            parserResponse(repository.deleteGoodsFromRemote(goods)) {
                repository.deleteGoodsFromLocal(goods)
            }
        }

    }

    fun deleteCategory(category: GoodsCategory) {
        launchUI {
            parserResponse(repository.deleteCategoryFromRemote(category)) {
                repository.deleteCategoryFromLocal(category)
            }
        }
    }

    /*
     * 修改信息
     */
    fun updateGoods(goods: Goods) {
        launchUI {
            parserResponse(repository.updateGoodsToRemote(goods)) {
                //远程数据操作成功后，才对本地数据进行操作
                repository.addOrUpdateGoodsToLocal(goods)
            }
        }
    }

    fun updateCategory(category: GoodsCategory) {
        launchUI {
            parserResponse(repository.updateCategoryToRemote(category)) {
                repository.addOrUpdateCategoryToLocal(category)
            }
        }
    }


    /*
      保存新增类别到数据库中，先保存到网络，成功后利用返回的objectId，形成新商品保存本地
     */
    fun addCategory(categoryName: String) {
        val newCategory = NewCategory(categoryName)
        launchUI {
            val defferd = async {
                repository.addCategoryToRemote(newCategory)
            }
            val result = defferd.await()
            parserResponse(result) {
                repository.addOrUpdateCategoryToLocal(
                    GoodsCategory(
                        it, categoryName
                    )
                )
            }

        }
    }

    /*
    保存新增加商品到数据库中,先保存到网络，成功后利用返回的objectId，形成新商品保存本地
     */
    fun addGoods(goods: NewGoods) {
        launchUI {
            val defferd = async { repository.addGoodsToRemote(goods) }
            val result = defferd.await()
            parserResponse(result) {
                val newGoods = Goods(
                    objectId = it,
                    goodsName = goods.goodsName,
                    unitOfMeasurement = goods.unitOfMeasurement,
                    categoryCode = goods.categoryCode,
                    unitPrice = goods.unitPrice
                )
                repository.addOrUpdateGoodsToLocal(newGoods)
            }
        }
    }


    /***********************************************************
     * 购物车部分
     ***********************************************************/


    /*
     * 将所选择商品转换成原材料，加入购物车后，从当前列表中清除已选择的商品，
     * 仅仅列表清除，为的是避免重复选择
     */
    fun addGoodsToShoppingCar(list: MutableList<Goods>) {
        val selectedGoods = mutableListOf<MaterialOfShoppingCar>()
        selectedGoods.addAll(list.map {
            goodsToShoppingCar(it)
        })
        launchUI {
            //创建一个通用的食物
            val common = FoodOfShoppingCar("common", "", "通用", "")
            repository.addFoodAndMaterialsToShoppingCar(common, selectedGoods)
            goodsList.removeAll(list)
        }

    }

    /*
     * 从每日菜单当中获取的原材料，将其加入购物车
     */
    fun getFoodOfDailyToShoppingCar(where: String) {
        launchUI {
            withContext(Dispatchers.IO) {
                //第一步：获取每日菜单
                val dailyMeal = repository.getDailyMealOfDate(where)
                if (dailyMeal.isSuccess()) {
                    //第二步：遍历每日菜单将其转换为购物车食物和原材料
                    dailyMeal.results?.forEach { meal ->
                        val food = FoodOfShoppingCar(
                            foodId = meal.cookBook.objectId,
                            foodName = meal.cookBook.foodName,
                            foodCategory = meal.cookBook.foodCategory,
                            foodTime = meal.mealTime,
                            isOfTearcher = meal.isOfTeacher
                        )

                        val materials =
                            repository.getCookBookWithMaterials(meal.cookBook.objectId).materials
                        val materialList = mutableListOf<MaterialOfShoppingCar>()
                        materials.forEach { m ->
                            materialList.add(materialToShoppingCar(m))
                        }
                        repository.addFoodAndMaterialsToShoppingCar(food, materialList)
                    }
                }
            }
        }
    }

    /****************************************************
     * 同步类别和商品信息
     ***************************************************/
    fun syncAllData() {

        launchUI {
                repository.clearLocalData()
                repository.syncCategory()
                repository.syncGoods()
        }

    }

}