package com.owner.basemodule.room.dao

import androidx.room.*
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import com.owner.basemodule.room.entities.ShoppingCartGoods
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * 本地数据库的商品管理,
 * Created by Administrator on 2019/9/5 0005
 */
@Dao
interface GoodsManagerDao {

    /**
    查询数据:查询返回的结果可以是多种类型如LiveData ,DataSource,Flowable.
    类型的选择根据业务的需要，如果是用于直接显示在UI上，可选择LiveData;
    如果显示内容较多，则可以选择DataSource(分页）；如果用于其他操作可以选择Flowable
     */
    //模糊查询商品
    @Query("SELECT * FROM GOODS WHERE goods_name LIKE '%' || :name || '%'")
    fun searchGoods(name: String): Flowable<MutableList<Goods>>

    //获取购物车中商品
    @Query("SELECT * FROM shoppingcartgoods")
    fun getShoppingCartAllGoods(): Flowable<MutableList<ShoppingCartGoods>>

    //通过编号查类别
    @Query("SELECT * FROM goodscategory WHERE code = :code")
    fun getCategory(code: String): Flowable<GoodsCategory>

    //关联查询，查类别的时候同时得到它的商品.
    @Query("SELECT * FROM GoodsCategory")
    fun loadCategory(): Flowable<MutableList<CategoryAndAllGoods>>

    //更新购物车商品的数量
    @Query("UPDATE  shoppingcartgoods SET quantity = :orderQuantity WHERE goods_name = :name " )
    fun updateOrderQuantity(name: String,orderQuantity: Float): Completable

    /**
     *插入数据,也可以用于修改，因为具体修改哪个属性不能确定，所以采用覆盖式插入
     */
    //查入商品
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoods(goods: Goods): Completable

    //查入类别
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: GoodsCategory): Completable

    //加入购物车
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertShoppingCartGoods(shoppingCartGoods: ShoppingCartGoods): Completable

    /**
     * 修改数据
     */
    @Update
    fun updateGoods(goods: Goods): Completable

    @Update
    fun updateCategory(category: GoodsCategory): Completable

    @Update
    fun updateShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable

    /**
    删除数据
     */
    //从购物车中删除商品
    @Delete
    fun moveShoppingCart(shoppingCartGoods: ShoppingCartGoods): Completable

    //从商品列表中删除商品
    @Delete
    fun deleteGoods(goods: Goods): Completable

    //从类别列表中删除类别
    @Delete
    fun deleteCategory(goodsCategory: GoodsCategory): Completable

}