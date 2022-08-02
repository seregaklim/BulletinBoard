package com.seregaklim.bulletinboard.accountHelper

import android.widget.Toast
import com.google.firebase.auth.FirebaseUser
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R

class AccountHelper(act:MainActivity) {
    private val act = act

// регистр. по почте
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            act.mAuth.currentUser?.delete()?.addOnCompleteListener{
                    task->
                if(task.isSuccessful){
                  //добавляем слушатель, который сообщит удалось ли зарегистрироваться
                    act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            sendEmailVerification(task.result.user!!)

                        } else {
                            Toast.makeText(act, act.resources.getString(R.string.sign_up_error), Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
//письмо для подтверждения регистрации,отправляем письмо user
    private fun sendEmailVerification(user: FirebaseUser){
    //добавляем слушатель, который сообщит удалось ли отправть письмо
    user.sendEmailVerification().addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(act, act.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()
            } else{
                Toast.makeText(act, act.resources.getString(R.string.send_verification_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}