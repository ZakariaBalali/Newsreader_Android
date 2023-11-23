package nl.balali.zakaria.newsreader636655.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import nl.balali.zakaria.newsreader636655.MainActivity
import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.LOGIN
import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.USERNAME
import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.AUTH
import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.PREF_NAME
import nl.balali.zakaria.newsreader636655.utilities.Constants.Companion.PRIVATE_MODE

@SuppressLint("StaticFieldLeak")
object SessionManager {
    private var context : Context? = null
    private var preferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    @SuppressLint("CommitPrefEdits", "WrongConstant")
    fun setup(context: Context){
        SessionManager.context = context
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = preferences!!.edit()
    }

    fun createSession(username: String, xauthtoken: String){
        editor!!.putBoolean(LOGIN, true)
        editor!!.putString(USERNAME, username)
        editor!!.putString(AUTH, xauthtoken)
        editor!!.apply()
        reloadMainActivity()
    }

    fun logout(){
        editor!!.clear()
        editor!!.apply()
        reloadMainActivity()
    }

    private fun reloadMainActivity(){
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }

    fun getUserName(): String? {
        return preferences!!.getString(USERNAME, null)
    }

    fun getToken(): String? {
        return preferences!!.getString(AUTH, null)
    }

    fun isLogin(): Boolean {
        return preferences!!.getBoolean(LOGIN, false)
    }





}