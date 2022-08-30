package com.seregaklim.bulletinboard.accountHelper

import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.seregaklim.bulletinboard.MainActivity
import com.seregaklim.bulletinboard.R
import com.seregaklim.bulletinboard.constants.FirebaseAuthConstants

class AccountHelper(val act:MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    // регистр. по почте
    fun signUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
           //удаляем анонимного юзера
            act.mAuth.currentUser?.delete()?.addOnCompleteListener{
                    task->
                if(task.isSuccessful){
                    //добавляем слушатель, который сообщит удалось ли зарегистрироваться
                    act.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            singUnWithEmailSuccessful(task.result.user!!)
                        } else {
                            signUpWithEmailException(task.exception!!, email, password)
                        }
                    }
                }
            }
        }
    }


    //регистрация по email
    private fun singUnWithEmailSuccessful(user: FirebaseUser){
        sendEmailVerification(user)
        //показываем email в меню
        act.uiUpdate(user)
    }
    //регистрация по email -ошибки
    private fun signUpWithEmailException(e: Exception, email: String, password: String){
        if (e is FirebaseAuthUserCollisionException) {
            val exception = e as FirebaseAuthUserCollisionException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToG(email, password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            val exception = e as FirebaseAuthInvalidCredentialsException
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            //Log.d("MyLog","Exception : ${e.errorCode}")
            if (e.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(act, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show()
            }
        }
        //FirebaseAuthWeakPasswordException
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
          //удаляем анонимного пользователя
            act.mAuth.currentUser?.delete()?.addOnCompleteListener{
                    task->
                if(task.isSuccessful){
                    //добавляем слушатель, который сообщит удалось ли зарегистрироваться
                    act.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            //показываем email в меню
                            act.uiUpdate(task.result?.user)
                        } else {
                            signInWithEmailException(task.exception!!, email, password)
                        }
                    }
                }
            }
        }
    }

    //Ошибки - входим по почте и pass
    private fun signInWithEmailException(e: Exception, email: String, password: String){
        //Log.d("MyLog", "Exception : ${e}")
        if (e is FirebaseAuthInvalidCredentialsException) {
            //Log.d("MyLog","Exception : ${task.exception}")
            val exception = e as FirebaseAuthInvalidCredentialsException
            // Log.d("MyLog","Exception 2 : ${exception.errorCode}")
            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG
                ).show()
            } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(
                    act,
                    FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG
                ).show()
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

        //удаляем из базы анонимного пользователя
        act.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful){

               //после чего проходим Google регистрацию
                act.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(act, "Sign in done", Toast.LENGTH_LONG).show()
                        act.uiUpdate(task.result?.user)
                    }else{
                        Log.d("MyLog", "Google Sign In Exception : ${task.exception}")
                    }
                }
            }
        }
    }

    //вход анонимного пользователя
    fun signInAnonymously(listener: Listener){
        act.mAuth.signInAnonymously().addOnCompleteListener{
                task ->
            if(task.isSuccessful){
                listener.onComplete()
                Toast.makeText(act, "Вы вошли как Гость", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(act, "Не удалось войти как Гость", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface Listener{
        fun onComplete()
    }
}






