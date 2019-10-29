package com.goldenstraw.restaurant.goodsmanager.ui.record

import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentRecordSelectDateBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import org.kodein.di.Kodein

/**
 * Created by Administrator on 2019/10/29 0029
 */
class RecordSelectDateFragment : BaseFragment<FragmentRecordSelectDateBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_record_select_date

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein)
    }

}