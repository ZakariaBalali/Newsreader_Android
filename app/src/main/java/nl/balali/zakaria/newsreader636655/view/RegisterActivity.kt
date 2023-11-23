package nl.balali.zakaria.newsreader636655.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.logic.AuthorizationLogic
import nl.balali.zakaria.newsreader636655.logic.response.LoginResponse
import nl.balali.zakaria.newsreader636655.logic.response.RegisterResponse
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private val authorizationLogic: AuthorizationLogic = Retrofit.authorizationLogic
    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText
    private lateinit var registerButton: Button
    private lateinit var errorLabel : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameField = findViewById(R.id.usernameField)
        passwordField = findViewById(R.id.passwordField)
        registerButton = findViewById(R.id.registerButton)
        errorLabel = findViewById(R.id.errorLabel)

        registerButton.setOnClickListener {
            registerEvent()
        }
    }

    private fun registerEvent(){

        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authorizationLogic.createUser(username, password).enqueue(object :
                Callback<RegisterResponse> {

                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {

                    if(response.code().toString() == "200" && response.body() != null){
                        val responseValue = response.body()

                        if(!responseValue!!.Success){
                            showError(getString(R.string.registerError))
                        }else{

                            loggingIn(username, password)
                        }
                    }else{
                        showError(getString(R.string.registerError))
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {

                    showError("Error ${t.message.toString()}")
                }
            })
        }else{
            showError(getString(R.string.requiredFields))
        }
    }

    private fun loggingIn(username: String, password: String){

        authorizationLogic.login(username, password).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                when (response.code().toString()) {
                    "200" -> response.body()?.AuthToken?.let {
                        (SessionManager::createSession)(username, it)
                    }
                    else -> {
                        (SessionManager::logout)()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                (SessionManager::logout)()
            }
        })
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }


    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }

}