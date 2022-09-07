package com.seregaklim.bulletinboard

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.accountHelper.AccountHelper
import com.seregaklim.bulletinboard.act.DescriptionActivity
import com.seregaklim.bulletinboard.act.EditAdsAct
import com.seregaklim.bulletinboard.act.FilterActivity
import com.seregaklim.bulletinboard.adapters.AdsRcAdapter
import com.seregaklim.bulletinboard.databinding.ActivityMainBinding
import com.seregaklim.bulletinboard.dialoghelper.DialogConst
import com.seregaklim.bulletinboard.dialoghelper.DialogHelper
import com.seregaklim.bulletinboard.model.Ad
import com.seregaklim.bulletinboard.utils.AppMainState
import com.seregaklim.bulletinboard.utils.BillingManager
import com.seregaklim.bulletinboard.utils.FilterManager
import com.seregaklim.bulletinboard.viewmodel.FirebaseViewModel
import com.squareup.picasso.Picasso



class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdsRcAdapter.Listener{

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
    //категория (каждый раз записываем категорию, которую нажали)
    private var currentCategory: String? = null
    lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private var filter: String = "empty"
    private var filterDb: String = ""
    private var pref: SharedPreferences? = null
    //если оплатил покупку
    private var isPremiumUser = false
    private var bManager: BillingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        //если оплатил покупку
        isPremiumUser = pref?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!

        if (!isPremiumUser) {
            //стартовая реклама
//            (application as AppMainState).showAdIfAvailable(this) {
//            }
            initAds()
        } else {
            binding.mainContent.adView2.visibility = View.GONE
        }


        initAds()
        init()
        initRecyclerView()
        initViewModel()

        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()


    }

    private fun onActivityResultFilter(){
        filterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK){
                 //передаем в FiterActivity
                filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
                //Log.d("MyLog","Filter: $filter")
                //Log.d("MyLog","getFilter: ${FilterManager.getFilter(filter)}")
               filterDb = FilterManager.getFilter(filter)
             //если RESULT_CANCELED , очищаем фильтр
            } else if(it.resultCode == RESULT_CANCELED){
                filterDb = ""
                filter= "empty"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==R.id.id_filter){
            val i = Intent(this@MainActivity,FilterActivity::class.java).apply {
                putExtra(FilterActivity.FILTER_KEY, filter)
            }
            filterLauncher.launch(i)
        }

        return super.onOptionsItemSelected(item)
    }
    private fun init (){
         //заполняем разными обьявлениями
         currentCategory=getString(R.string.def)
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
        binding.mainContent.adView2.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mainContent.adView2.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mainContent.adView2.destroy()
        //закрываем окно покупок
        bManager?.closeConnection()
    }

    //загружаю рекламу
    private  fun initAds(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.mainContent.adView2.loadAd(adRequest)
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
            val list=getAdsByCategory(it)
            //значит не очищаем список
            if (!clearUpdate) {adapter.updateAdapter(list)
        }else{
            //тогда очищаем список
            adapter.updateAdapterWithClear(list)
        }

            //если в адаптере пусто , выводим сообщение
            binding.mainContent.tvEmpty.visibility = if (adapter.itemCount ==0) View.VISIBLE else View.GONE

        }
    }
    //взять объявления по категории, возвращаем перевернутый список
    private fun getAdsByCategory(list: ArrayList<Ad>): ArrayList<Ad>{
        val tempList = ArrayList<Ad>()
        //перегружаем весь список
        tempList.addAll(list)
        if(currentCategory != getString(R.string.def)){
            tempList.clear()
            list.forEach {
                if(currentCategory == it.category)tempList.add(it)
            }
        }
        //переворачиваем
        tempList.reverse()
        return tempList
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

                    //разные объявления
                    currentCategory = getString(R.string.def)
                   firebaseViewModel.loadAllAdsFirstPage(filterDb)
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
           //моя страничка
            R.id.id_my_ads -> {
                Toast.makeText(this, "Pressed id_my_ads", Toast.LENGTH_LONG).show()
            }
            R.id.id_car -> {
                getAdsFromCat(getString(R.string.ad_car))
            }
            R.id.id_pc -> {
                getAdsFromCat(getString(R.string.ad_pc))

            }
            R.id.id_smart -> {
                getAdsFromCat(getString(R.string.ad_smartphone))
            }
            R.id.id_dm -> {
                getAdsFromCat(getString(R.string.ad_dm))
            }
            //удаляем рекламу
            R.id.id_remove_ads -> {
                //покупаем продукт
                bManager = BillingManager(this)
                bManager?.startConnection()

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

    //фильтруем по категориям
    private fun getAdsFromCat(cat: String){
       //на какой категории мы находимся
        currentCategory = cat

        firebaseViewModel.loadAllAdsFromCat(cat,filterDb)

      //  firebaseViewModel.loadAllAdsFromCat(cat, filterDb)
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


    //загрузка объявления частями при скроле
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

                        getAdsFromCat(adsList)

                    }
                }
            }
        })
    }
     //загружаем или разные оббъявления или по категориям
    private fun getAdsFromCat(adsList: ArrayList<Ad>) {
        //берем время от первого объявления
           adsList[0].let {

               //если категория разное
               if (currentCategory == getString(R.string.def)) {
                   firebaseViewModel.loadAllAdsNextPage(it.time,filterDb)
               } else {

                   firebaseViewModel.loadAllAdsFromCatNextPage(it.category!!,it.time,filterDb)
               }
           }
    }

    //константы MainActivity
    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        //если -1 не может скролиться вверх
        const val SCROLL_DOWN = 1
    }
}













