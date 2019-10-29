package com.goldenstraw.restaurant.goodsmanager.ui.record

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityRecordOrderBinding
import com.owner.basemodule.base.view.activity.BaseActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * Created by Administrator on 2019/10/29 0029
 */
class RecordOrdersActivity : BaseActivity<ActivityRecordOrderBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_record_order

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}