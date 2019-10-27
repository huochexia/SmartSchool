package com.goldenstraw.restaurant.goodsmanager.ui.supplier

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivitySupplierApplyBinding
import com.owner.basemodule.base.view.activity.BaseActivity
import org.kodein.di.Copy
import org.kodein.di.Kodein

class SupplierApplyActivity : BaseActivity<ActivitySupplierApplyBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_supplier_apply

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}