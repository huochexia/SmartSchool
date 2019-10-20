package com.goldenstraw.restaurant.goodsmanager.repositories.goods_order

import com.goldenstraw.restaurant.goodsmanager.http.entities.NewCategory
import com.goldenstraw.restaurant.goodsmanager.http.entities.NewGoods
import com.owner.basemodule.base.repository.BaseRepositoryBoth
import com.owner.basemodule.network.ApiException
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.GoodsOfShoppingCart
import com.owner.basemodule.room.entities.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

/**
 * 商品数据源，需要处理来自本地和远程的数据，所以它要继承同时拥有两个数据源的类
 */
class GoodsRepository(
    private val remote: IRemoteGoodsDataSource,
    private val local: ILocalGoodsDataSource
) : BaseRepositoryBoth<IRemoteGoodsDataSource, ILocalGoodsDataSource>(remote, local) {

    /**
     * 增加
     */
    //1、 增加商品到远程数据库,成功后取出objectId，赋值给Goods对象，然后保存本地库

    fun addGoods(goods: NewGoods): Observable<Goods> {
        return remote.addGoods(goods).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                val newGoods = Goods(
                    objectId = it.objectId!!,
                    goodsName = goods.goodsName,
                    unitOfMeasurement = goods.unitOfMeasurement,
                    categoryCode = goods.categoryCode,
                    unitPrice = goods.unitPrice
                )
                newGoods
            }

    }


    //2、增加商品类别到远程数据库，传入类别名称，成功后得到新类别的objectId,使用objectId,创建新类别，然后保存本地库

    fun addGoodsCategory(category: NewCategory): Observable<GoodsCategory> {
        return remote.addCategory(category).toObservable()
            .map {
                if (!it.isSuccess()) {
                    throw ApiException(it.code)
                }
                val newCategory = GoodsCategory(it.objectId!!, categoryName = category.categoryName)
                newCategory
            }
    }

    /*
      将全部商品加入到本地
     */
    fun addGoodsListToLocal(list: MutableList<Goods>): Completable {
        return local.addGoodsAll(list)
    }

    fun insertGoodsToLocal(goods: Goods): Completable {
        return local.insertNewGoodsToLocal(goods)
    }

    fun insertSupplierToLocal(userList: MutableList<User>): Completable {
        return local.insertSupplierToLocal(userList)
    }

    /*
      将所有商品类别加入本地
     */
    fun addCategoryListToLocal(list: MutableList<GoodsCategory>): Completable {
        return local.addCategoryAll(list)
    }

    fun insertCategoryToLocal(category: GoodsCategory): Completable {
        return local.insertCategoryToLocal(category)
    }

    /*
     *将所选择商品保存在本地购物车当中
     *
     */
    fun addGoodsToShoppingCart(goodsList: MutableList<GoodsOfShoppingCart>): Completable {
        return local.addShoppingCartAll(goodsList)
    }
    fun addSupplierTolocal(userList: MutableList<User>):Completable{
        return local.insertSupplierToLocal(userList)
    }
    /*
     *获取购物车内商品数量
     */
    fun getShoppingCartOfCount(): Single<Int> = local.getShoppingCartCount()


    /**
     * 更新
     */
    //1、更新远程数据
    fun updateGoods(goods: Goods): Completable {
        val updateGoods = NewGoods(
            goodsName = goods.goodsName,
            unitOfMeasurement = goods.unitOfMeasurement,
            categoryCode = goods.categoryCode,
            unitPrice = goods.unitPrice
        )
        return remote.updateGoods(updateGoods, goods.objectId)

    }

    //2、更新类别
    fun updateCategory(category: GoodsCategory): Completable {
        val updateCategory = NewCategory(
            categoryName = category.categoryName
        )
        return remote.updateCategory(updateCategory, category.objectId)
    }

    //3、更新购物车内的商品
    fun updateShoppingCart(shoppingCart: GoodsOfShoppingCart): Completable {
        return local.insertShoppingCart(shoppingCart)
    }

    /*
      获取所有商品类别, 这里应该考虑从远程获取，然后保存于本地，最后从本地获得结果。
      先暂时实现从本地直接获取
     */
    fun getAllCategoryFromLocal(): Observable<MutableList<GoodsCategory>> {
        return local.getAllCategory()
    }

    fun getAllCategoryFromNetwork(): Observable<MutableList<GoodsCategory>> {
        return remote.getAllCategory()
    }

    fun getAllGoodsFromNetwork(): Observable<MutableList<Goods>> {
        return remote.getAllGoods()
    }

    fun getAllSupplierFromRemote(): Observable<MutableList<User>> {
        return remote.getAllSupplier()
    }

    /*
      按类别查询商品,此处只从本地数据源中获取。
     */
    fun queryGoodsFromLocal(category: GoodsCategory): Observable<MutableList<Goods>> {

        return local.getGoodsOfCategory(category.objectId)

    }

    /*
      根据名字模糊查找商品
     */
    fun findByName(name: String): Observable<MutableList<Goods>> {
        return local.findByName(name)
    }

    /**
     * 删除
     */
    fun deleteGoodsFromLocal(goods: Goods): Completable {
        return local.deleteGoodsFromLocal(goods)
    }

    fun deleteCategoryFromLocal(categroy: GoodsCategory): Completable {
        return local.deleteCategoryFromLocal(categroy)
    }

    fun deleteGoodsFromRemote(goods: Goods): Completable {
        return remote.deleteGoods(goods)
    }

    fun deleteCategoryFromRemote(category: GoodsCategory): Completable {
        return remote.deleteCategory(category)
    }

    fun deletShoppingCartList(goodslist: MutableList<GoodsOfShoppingCart>): Completable {
        return local.deleteShoppingCartList(goodslist)
    }

    /**
     * 清空本地内容，主要是为了同步做准备。
     */
    fun clearAllData(): Completable {
        return local.clearGoodsAll().andThen(local.clearCategoryAll()).andThen(local.clearUserAll())
    }
}