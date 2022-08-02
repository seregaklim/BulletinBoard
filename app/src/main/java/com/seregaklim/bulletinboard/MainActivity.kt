package com.seregaklim.bulletinboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.seregaklim.bulletinboard.databinding.ActivityMainBinding
import com.seregaklim.bulletinboard.dialoghelper.DialogConst
import com.seregaklim.bulletinboard.dialoghelper.DialogHelper


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
  // val mAuth = FirebaseAuth.getInstance()
  val mAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
    }

    private fun init (){

        //кнопка на ActionBar, запускает  drawerLayout
        val  toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainContent.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        //передает навигацию меню кнопок
        binding.navView.setNavigationItemSelectedListener(this)
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

            }
        }
        //закрывает
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}