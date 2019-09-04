package com.goldenstraw.restaurant.ui

import android.graphics.Color
import android.view.Menu
import android.view.Window
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.navigation.fragment.NavHostFragment
import com.goldenstraw.restaurant.R
import com.goldenstraw.restaurant.databinding.ActivityOrderManagerBinding
import com.owner.basemodule.base.view.activity.BaseActivity
import kotlinx.android.synthetic.main.activity_order_manager.*
import org.kodein.di.Copy
import org.kodein.di.Kodein

class OrderManagerActivity : BaseActivity<ActivityOrderManagerBinding>() {

    override val kodein: Kodein = Kodein.lazy {
        extend(parentKodein, copy = Copy.All)
    }

    override val layoutId: Int
        get() = R.layout.activity_order_manager

    override fun initView() {
        super.initView()
        setSupportActionBar(toolbar)
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.order_Manager_Fragment) as NavHostFragment? ?: return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search_goods, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        val icon =searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_button)
        icon.setColorFilter(Color.WHITE)
        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.WHITE)
        val icon2 = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        icon2.setColorFilter(Color.WHITE)

        return super.onCreateOptionsMenu(menu)
    }
}
