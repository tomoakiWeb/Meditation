package com.example.meditation.view.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.meditation.MyApplication
import com.example.meditation.R
import com.example.meditation.data.ThemeData
import com.example.meditation.viewmodel.MainViewModel
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.theme_detail_card.*

class ThemeSelectAdapter(val themeList: ArrayList<ThemeData>,
                         val viewModel: MainViewModel) : RecyclerView.Adapter<ThemeSelectAdapter.ViewHolder>() {

    private val appContext = MyApplication.appContext

    inner class ViewHolder(override val containerView: View?): RecyclerView.ViewHolder(containerView!!), LayoutContainer{

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.theme_detail_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(VH: ViewHolder, position: Int) {
        VH.apply {
            val themeData = themeList[position]
            txtTheme.text = appContext.resources.getString(themeData.themeNameResId)
            Glide.with(appContext).load(themeData.themeSqPicResId).into(imgTheme)
            containerView?.setOnClickListener {
                viewModel.setTheme(themeData)
            }
        }
    }

    override fun getItemCount(): Int {
        return themeList.size
    }

}

