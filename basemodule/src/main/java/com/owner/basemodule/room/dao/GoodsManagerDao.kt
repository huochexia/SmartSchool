package com.owner.basemodule.room.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import androidx.room.*
import com.owner.basemodule.room.entities.Goods
import com.owner.basemodule.room.entities.GoodsCategory
import io.reactivex.Flowable

/**
 * 本地数据库的商品管理,
 * Created by Administrator on 2019/9/5 0005
 */
@Dao
interface GoodsManagerDao {

    /*
    查询数据:查询返回的结果可以是多种类型如LiveData ,DataSource,Flowable.
            类型的选择根据业务的需要，如果是用于直接显示在UI上，可选择LiveData;
            如果显示内容较多，则可以选择DataSource(分页）；如果用于其他操作可以选择Flowable
     */
    @Query("SELECT * FROM goods")
    fun getAllGoods(): DataSource<String, List<Goods>>

    //条件查询,购物车中商品，isChecked == true
    @Query("SELECT * FROM goods WHERE isChecked = 'true' ")
    fun getShoppingCartAllGoods(): DataSource<String, List<Goods>>

    //通过编号查类别
    @Query("SELECT * FROM goodscategory WHERE code = :code")
    fun getCategory(code: String): Flowable<GoodsCategory>

    //关联查询，查类别的时候同时得到它的商品.
    @Query("SELECT * FROM GoodsCategory")
    fun loadCategory(): Flowable<List<CategoryAndAllGoods>>

    @Query("SELECT * FROM GoodsCategory WHERE code = :code")
    fun loadCategoryAndGoods(code: String): DataSource<String, List<CategoryAndAllGoods>>

    /*
     插入数据
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoods(goods: Goods): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: GoodsCategory): Long

    /*
    修改数据
     */
    /*
    删除数据
     */


}