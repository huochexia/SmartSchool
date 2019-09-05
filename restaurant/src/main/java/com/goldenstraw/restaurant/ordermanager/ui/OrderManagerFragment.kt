package com.goldenstraw.restaurant.ordermanager.ui

import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.FragmentGoodsListBinding
import com.owner.basemodule.base.view.fragment.BaseFragment
import kotlinx.android.synthetic.main.activity_order_manager.*
import kotlinx.android.synthetic.main.fragment_goods_list.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class OrderManagerFragment : BaseFragment<FragmentGoodsListBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_goods_list

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }



}