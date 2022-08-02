package com.seregaklim.bulletinboard.dialoghelper

import android.app.AlertDialog
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.accountHelper.AccountHelper
import com.seregaklim.bulletinboard.databinding.SignDialogBinding


class DialogHelper(act: MainActivity) {
    private val act =act
    private  val accHelper= AccountHelper(act)

    fun createSignDialog(index:Int){

        val builder = AlertDialog.Builder(act)
       //binding
        val rootDialogElement = SignDialogBinding.inflate(act.layoutInflater)
        val view = rootDialogElement.root


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
        }

        //нажимаем регистрироваться
        rootDialogElement.btSignUpIn.setOnClickListener{
          if (index==DialogConst.SIGN_UP_STATE){

                accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(),
                    rootDialogElement.edSignPassword.text.toString())

            }
            else{

            }

        }



        builder.setView(view)
        builder.show()
    }



}