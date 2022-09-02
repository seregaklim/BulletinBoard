package com.seregaklim.bulletinboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.accountHelper.AccountHelper
import com.seregaklim.bulletinboard.act.DescriptionActivity
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.adapters.AdsRcAdapter
import com.seregaklim.bulletinboard.databinding.ActivityMainBinding
import com.seregaklim.bulletinboard.dialoghelper.DialogConst
import com.seregaklim.bulletinboard.dialoghelper.DialogHelper
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,AdsRcAdapter.Listener {

    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    //очищаем данные при скроле
    private var clearUpdate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        initRecyclerView()
        initViewModel()
   
        bottomMenuOnClick()
        scrollListener()
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
        //инициализируем Header ,показываем аватарку (индекс 0,т.к он один)
        imAccount = binding.navView.getHeaderView(0).findViewById(R.id.imAccountImage)

        navViewSettings()
        onActivityResult()
    }

    //при возврте на @MainActivity высвечивалась иконка на нижнем тулбаре
    override fun onResume() {
        super.onResume()
        binding.mainContent.bNavView.selectedItemId = R.id.id_home
        // binding.mainContent.adView2.resume()
    }


    private fun onActivityResult() {
      //когда выбрали аккаунт
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

    private fun initViewModel(){
        //следит за обновлением данных
        firebaseViewModel.liveAdsData.observe(this) {
            //значит не очищаем список
            if (!clearUpdate) {adapter.updateAdapter(it)
        }else{
            //тогда очищаем список
            adapter.updateAdapterWithClear(it)
        }

            //если в адаптере пусто , выводим сообщение
            binding.mainContent.tvEmpty.visibility = if (adapter.itemCount ==0) View.VISIBLE else View.GONE

        }
    }

    //нижний тулбар
    private fun bottomMenuOnClick() = with(binding){
        // mainContent.bNavView.menu.setGroupCheckable(0, false, false)
        mainContent.bNavView.setOnItemSelectedListener { item ->
            //очищвем старый список, загр. новый при скроле
             clearUpdate = true
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
                     firebaseViewModel.loadMyFavs()
                }
                R.id.id_home -> {

                    firebaseViewModel.loadAllAds(lastTime = "0")

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
       //очищвем старый список, загр. новый
        clearUpdate=true
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
                // если mAuth.currentUser ==null
                if (mAuth.currentUser?.isAnonymous ==true){
                    //закрывает
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
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

    //показываем email и аватарку  или гость - зарег мы или нет
    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            //заходим как гость
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    tvAccount.setText(R.string.sign_anonim)
                      imAccount.setImageResource(R.drawable.ic_account_def)
                }
            })
        } else if (user.isAnonymous){
            tvAccount.setText(R.string.sign_anonim)
            imAccount.setImageResource(R.drawable.ic_account_def)
        }else if (!user.isAnonymous){
           tvAccount.text= user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
    }

    //меняем цвет фидера (меню)
    private fun navViewSettings() = with(binding){
        val menu = navView.menu
        val adsCat = menu.findItem(R.id.adsCat)
        //SpannableString позволяет менять цвет,шрифт по буквам
        val spanAdsCat = SpannableString(adsCat.title)
        spanAdsCat.setSpan(ForegroundColorSpan(
            ContextCompat.getColor(this@MainActivity, R.color.color_red)),
            //откуда красим и до куда
            0, adsCat.title.length, 0)
        //передаем покрашенный текст
        adsCat.title = spanAdsCat

        //красим по аналогии следующий элемент
        val accCat = menu.findItem(R.id.accCat)
        val spanAccCat = SpannableString(accCat.title)
        spanAccCat.setSpan(ForegroundColorSpan(
            ContextCompat.getColor(this@MainActivity, R.color.green_main)),
            0, accCat.title.length, 0)
        accCat.title = spanAccCat
    }

    //удаление
    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }
    //счетчик
    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        val  i =Intent (this, DescriptionActivity::class.java)
        i.putExtra("AD",ad)
        // переходим в другой активити
        startActivity(i)

    }
    //лайк(дизлайк)
    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }


    //загрузка объявления частями
    private fun scrollListener() = with(binding.mainContent){
        rcView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recView, newState)
                //если не может  скролиться RecyclerView вниз
                if(!recView.canScrollVertically(SCROLL_DOWN)
                    //и находится в состоянии покоя
                    && newState == RecyclerView.SCROLL_STATE_IDLE){
                 //  Log.d("MyLog", "Не может скролиться!")
                    //не очищаем старый список, при скроле, тогда к нему прибаляются новые
                   clearUpdate=false
                   val adsList =firebaseViewModel.liveAdsData.value!!
                    //когда доскролим до конца делаем проверку. тк в конце нет объявлений
                    if (adsList.isNotEmpty()) {
                        //берем время последнего объявления
                        adsList[adsList.size-1].let { firebaseViewModel.loadAllAds(it.time) }

                    }
                }
            }
        })
    }

    //константы MainActivity
    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        //если -1 не может скролиться вверх
        const val SCROLL_DOWN = 1
    }
}