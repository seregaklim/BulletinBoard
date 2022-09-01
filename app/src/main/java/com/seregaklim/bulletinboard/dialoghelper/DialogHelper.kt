package com.seregaklim.bulletinboard.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.accountHelper.AccountHelper
import com.seregaklim.bulletinboard.databinding.SignDialogBinding


class DialogHelper(act: MainActivity) {
    private val act =act
    val accHelper= AccountHelper(act)

    fun createSignDialog(index:Int){

        val builder = AlertDialog.Builder(act)
        //binding
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)

        //меню
        setDialogState(index, rootDialogElement)

        val dialog = builder.create()

        //регистрация
        rootDialogElement.btSignUpIn.setOnClickListener{
            setOnClickSignUpIn(index, rootDialogElement, dialog)
        }

        //востановление пароля
        rootDialogElement.btForgetP.setOnClickListener{
            setOnClickResetPassword(rootDialogElement, dialog)
        }
        // зайти по гугл
        rootDialogElement.btGoogleSignIn.setOnClickListener{
          accHelper.signInWithGoogle()
            dialog.dismiss()
        }

        dialog.show()
    }

    //меню
    private fun setDialogState(index: Int, rootDialogElement: SignDialogBinding) {
        if (index==DialogConst.SIGN_UP_STATE){
            //регистрация
            rootDialogElement.tvSignTitle.text=act.resources.getString(R.string.ac_sign_up)
            //Зарегистрироваться
            rootDialogElement.btSignUpIn.text=act.resources.getString(R.string.sign_up_action)
        }
        else{
            //Вход
            rootDialogElement.tvSignTitle.text=act.resources.getString(R.string.ac_sign_in)
            //Войти
            rootDialogElement.btSignUpIn.text=act.resources.getString(R.string.sign_in_action)
            //забыли пароль
            rootDialogElement.btForgetP.visibility = View.VISIBLE
        }
    }

    //востановление пароля
    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {

        if(rootDialogElement.edSignEmail.text.isNotEmpty()){
            act.mAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString()).addOnCompleteListener { task->
               //проверяем успешно ли был отправленно востановление
                if(task.isSuccessful){
                    Toast.makeText(act, R.string.email_reset_password_was_sent, Toast.LENGTH_LONG).show()
                }
            }
            //закрываем окно регисстрации
            dialog?.dismiss()
        } else {
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
        }

    }

    //нажимаем регистрироваться
    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        //закрываем окно регисстрации
        dialog?.dismiss()
        if(index == DialogConst.SIGN_UP_STATE){

            accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())

        } else {

            accHelper.signInWithEmail(rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString())
        }
    }
}