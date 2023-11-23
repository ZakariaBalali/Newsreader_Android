package nl.balali.zakaria.newsreader636655.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast

@SuppressLint("StaticFieldLeak")
object ToastUtil {

    private var context : Context? = null

    fun setup(context: Context){
        this.context = context
    }

    fun shortToast(message: String){
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show()
    }
}