package com.seregaklim.bulletinboard.dialogs

import android.app.Activity
import android.app.AlertDialog
import com.seregaklim.bulletinboard.databinding.ProgressDialogLayoutBinding

object ProgressDialog {

    fun createProgressDialog(act: Activity): AlertDialog {

        val builder = AlertDialog.Builder(act)
        val rootDialogElement = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        val dialog = builder.create()
       //ничего работать не будет, пока крутится диалог
        dialog.setCancelable(false)

        dialog.show()
        return dialog
    }


}