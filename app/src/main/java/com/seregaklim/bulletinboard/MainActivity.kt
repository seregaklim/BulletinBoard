package com.seregaklim.bulletinboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.adapters.AdsRcAdapter
import com.seregaklim.bulletinboard.databinding.ActivityMainBinding
import com.seregaklim.bulletinboard.dialoghelper.DialogConst
import com.seregaklim.bulletinboard.dialoghelper.DialogHelper
import com.seregaklim.bulletinboard.viewmodel.FirebaseViewModel


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var tvAccount: TextView
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth

    val adapter = AdsRcAdapter(mAuth)

    private val firebaseViewModel: FirebaseViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        initRecyclerView()
        initViewModel()

        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
    }

    //при возврте на @MainActivity высвечивалась иконка на нижнем тулбаре
    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
        // binding.mainContent.adView2.resume()
    }


    private fun initViewModel(){
        //следит за обновлением данных
        firebaseViewModel.liveAdsData.observe(this,{
            adapter.updateAdapter(it)
        })
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

    //нижний тулбар
    private fun bottomMenuOnClick() = with(binding){
        // mainContent.bNavView.menu.setGroupCheckable(0, false, false)
        mainContent.bNavView.setOnItemSelectedListener { item ->
            // clearUpdate = true
            when (item.itemId) {
                R.id.id_new_ad -> {
//                    if (mAuth.currentUser != null) {
//                        if (!mAuth.currentUser?.isAnonymous!!) {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java)
                    startActivity(i)
//                        } else {
//                            showToast("Гость не может публиковать объявления!")
//                        }
//                    } else {
//                        showToast("Ошибка регистрации")
//                    }
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favs -> {
                    // firebaseViewModel.loadMyFavs()
                }
                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()

                    //                   currentCategory = getString(R.string.def)
//                   firebaseViewModel.loadAllAdsFirstPage(filterDb)
                    mainContent.toolbar.title = getString(R.string.def)
                }
            }
            true
        }
    }

    private fun initRecyclerView(){
        binding.apply {
            mainContent.rcView.layoutManager =LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter =adapter
        }

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
}