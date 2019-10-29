package com.goldenstraw.restaurant.goodsmanager.ui.record

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordSelectSupplierBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * Created by Administrator on 2019/10/29 0029
 */
class RecordSelectSupplierFragment : BaseFragment<FragmentRecordSelectSupplierBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_record_select_supplier

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}