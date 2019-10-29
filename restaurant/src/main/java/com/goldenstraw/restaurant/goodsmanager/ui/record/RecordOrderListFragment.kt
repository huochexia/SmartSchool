package com.goldenstraw.restaurant.goodsmanager.ui.record

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordOrderListBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Copy
import org.kodein.di.Kodein

/**
 * 列示某供应商的某日验货或记帐的清单
 * Created by Administrator on 2019/10/29 0029
 */
class RecordOrderListFragment : BaseFragment<FragmentRecordOrderListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_record_order_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

}