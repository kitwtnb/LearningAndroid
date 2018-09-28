package com.kitwtnb.droidkaigi2018contributors.view.contributors

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import com.kitwtnb.droidkaigi2018contributors.R
import com.kitwtnb.droidkaigi2018contributors.databinding.ActivityContributorsBinding
import com.kitwtnb.droidkaigi2018contributors.ext.observeNonNull
import org.koin.android.viewmodel.ext.android.viewModel

class ContributorsActivity : AppCompatActivity() {
    companion object {
        fun createIntent(context: Context) = Intent(context, ContributorsActivity::class.java)
    }

    private val viewModel: ContributorsViewModel by viewModel()
    private val binding: ActivityContributorsBinding by lazy {
        DataBindingUtil.setContentView<ActivityContributorsBinding>(this, R.layout.activity_contributors)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val controller = ContributorController()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.pullToRefresh.setOnRefreshListener { viewModel.onRefresh() }
        binding.contributors.let {
            it.layoutManager = GridLayoutManager(this, 2)
            it.adapter = controller.adapter
        }

        viewModel.contributors.observeNonNull(this) {
            controller.setData(it)
        }
        viewModel.isRefreshing.observeNonNull(this) {
            binding.pullToRefresh.isRefreshing = it
        }

        viewModel.onCreate()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}