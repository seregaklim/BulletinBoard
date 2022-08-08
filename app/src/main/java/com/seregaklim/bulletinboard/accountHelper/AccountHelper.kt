package com.seregaklim.bulletinboard.accountHelper

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R

class AccountHelper(act:MainActivity) {
    private val act = act
    private lateinit var signInClient: GoogleSignInClient

    // регистр. по почте
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {

            //добавляем слушатель, который сообщит удалось ли зарегистрироваться
            act.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result.user!!)
                       //показываем email в меню
                        act.uiUpdate(task.result?.user)

                    } else {
                        Toast.makeText(
                            act,
                            act.resources.getString(R.string.sign_up_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }


    //письмо для подтверждения регистрации,отправляем письмо user
    private fun sendEmailVerification(user: FirebaseUser) {
        //добавляем слушатель, который сообщит удалось ли отправть письмо
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    act,
                    act.resources.getString(R.string.send_verification_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //входим по почте и pass
    fun signInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {

            //добавляем слушатель, который сообщит удалось ли зарегистрироваться
            act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    act.uiUpdate(task.result?.user)

                } else {
                    Toast.makeText(
                        act,
                        act.resources.getString(R.string.sign_in_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    //получаем аккаунт из нашего смартфона
    private fun getSignInClient():GoogleSignInClient{
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(act.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(act,gso)
    }



    //заходим  по Google
    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        act.googleSignInLauncher.launch(intent)
    }

//если зарег. по мылу и довил аккаунт gooogle
private fun linkEmailToG(email:String, password:String){
    val credential = EmailAuthProvider.getCredential(email,password)
    if(act.mAuth.currentUser != null){
        act.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener {task->
            if(task.isSuccessful){
                Toast.makeText(act, act.resources.getString(R.string.link_done), Toast.LENGTH_LONG).show()
            }

        }
    } else {
        Toast.makeText(act, act.resources.getString(R.string.enter_to_g), Toast.LENGTH_LONG).show()
    }
}
   //выход из Google
    fun signOutG(){
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)

                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(task.result?.user)
                }
        }

    }
}






