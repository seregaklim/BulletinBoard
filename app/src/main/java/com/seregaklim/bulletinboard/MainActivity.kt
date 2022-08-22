package com.seregaklim.bulletinboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.adapters.AdsRcAdapter
import com.seregaklim.bulletinboard.data.Ad
import com.seregaklim.bulletinboard.database.DbManager
import com.seregaklim.bulletinboard.database.ReadDataCallbsck
import com.seregaklim.bulletinboard.databinding.ActivityMainBinding
import com.seregaklim.bulletinboard.dialoghelper.DialogConst
import com.seregaklim.bulletinboard.dialoghelper.DialogHelper


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ReadDataCallbsck {

    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var tvAccount: TextView
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()
     val dbManager =DbManager(this)
    val adapter = AdsRcAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        initRecyclerView()
        dbManager.readDataFromDb()
    }

    private fun init (){
        //добавляем свой акшен бар, так как он отключен в манифесте
        setSupportActionBar(binding.mainContent.toolbar)
        //кнопка на ActionBar, запускает  drawerLayout
        val  toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainContent.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //передает навигацию меню кнопок
        binding.navView.setNavigationItemSelectedListener(this)
        //инициализируем Header ,показываем Email (индекс 0,т.к он один)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
    }
    private fun initRecyclerView(){
        binding.apply {
            mainContent.rcView.layoutManager =LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter =adapter
        }

    }


    //включаем менюшку в бар
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    //надуваем меню функционал
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.id_new_ads){
            val i = Intent(this@MainActivity, EditAdsAct::class.java)
            //запуск активити
            startActivity(i)
        }
        return super.onOptionsItemSelected(item)
    }



    //меню drawerLayout
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.id_my_ads -> {
                Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
            }
            R.id.id_car -> {

            }
            R.id.id_pc -> {

            }
            R.id.id_smart -> {

            }
            R.id.id_dm -> {

            }
            R.id.id_remove_ads -> {


            }
            //регистр
            R.id.id_sign_up -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)
            }
            //вход
            R.id.id_sign_in -> {
                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)
            }
            R.id.id_sign_out -> {
                //меняет текст
                uiUpdate(null)
                mAuth.signOut()
                //выход из google аккаунта
                dialogHelper.accHelper.signOutG()
            }
        }
        //закрывает
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //перезапускаем при старте, зарег мы или нет
    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }
    //показываем email или просим зарег.
    fun uiUpdate(user: FirebaseUser?) {
        tvAccount.text =if (user == null) {
            resources.getString(R.string.not_reg)
        } else  {
            user.email
        }
    }
    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Log.d("MyLog", "Api 0")
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("MyLog", "Api error : ${e.message}")
            }
        }

    }
    //передаем список с сервера
    override fun readData(list: List<Ad>) {
       adapter.updateAdapter(list)
    }


}