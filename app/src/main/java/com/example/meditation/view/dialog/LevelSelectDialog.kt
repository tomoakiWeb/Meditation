package com.example.meditation.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.meditation.R
import com.example.meditation.viewmodel.MainViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LevelSelectDialog: DialogFragment() {

    var selectedItemId = 0

    //private lateinit var viewModel: MainViewModel
    private val viewModel: MainViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        val dialog = AlertDialog.Builder(activity!!).apply {
            setTitle(R.string.select_level)
            setSingleChoiceItems(R.array.level_list, selectedItemId) { dialog, which ->
                selectedItemId = which
                //TODO 選択したレベルをSharedPreferenceに保存する
                viewModel.setLevel(selectedItemId)
                dialog.dismiss()
            }
        }.create()
        return dialog


    }
}