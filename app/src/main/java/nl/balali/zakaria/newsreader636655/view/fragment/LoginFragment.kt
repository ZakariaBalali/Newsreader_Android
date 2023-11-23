package nl.balali.zakaria.newsreader636655.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.logic.AuthorizationLogic
import nl.balali.zakaria.newsreader636655.logic.response.LoginResponse
import nl.balali.zakaria.newsreader636655.logic.Retrofit
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.utilities.ToastUtil
import nl.balali.zakaria.newsreader636655.view.RegisterActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private val authorizationLogic: AuthorizationLogic = Retrofit.authorizationLogic
    private lateinit var usernameField : EditText
    private lateinit var passwordField : EditText
    private lateinit var loginButton : Button
    private lateinit var signUpLabel : TextView
    private lateinit var errorLabel : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameField = view.findViewById(R.id.usernameField)
        passwordField = view.findViewById(R.id.passwordField)
        loginButton = view.findViewById(R.id.loginButton)
        signUpLabel = view.findViewById(R.id.signUpLabel)
        errorLabel = view.findViewById(R.id.errorLabel)

        loginButton.setOnClickListener {
            loggingIn()
        }

        signUpLabel.setOnClickListener{
            val intent = Intent(this.context, RegisterActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this.context?.startActivity(intent)
        }
    }

    private fun loggingIn(){

        if(usernameField.text.isNotEmpty() && passwordField.text.isNotEmpty()) {

            val username: String = usernameField.text.toString()
            val password: String = passwordField.text.toString()

            authorizationLogic.login(username, password).enqueue(object :
                Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    when (response.code().toString()) {
                        "200" -> response.body()?.AuthToken?.let { successLogin(username, it) }
                        "401" -> unauthorizedLogin()
                        "400" ->badRequestLogin()
                        else -> {
                            elseLogin()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    showError("Error: ${t.message.toString()}.")
                }
            })
        }else{
            showError(getString(R.string.requiredFields))
        }
    }

    fun successLogin(username: String, authToken: String){
        (SessionManager::createSession)(username, authToken)
    }

    fun badRequestLogin(){
        (ToastUtil::shortToast)(getString(R.string.requiredFields))
        showError(getString(R.string.requiredFields))
    }

    fun unauthorizedLogin(){
        (ToastUtil::shortToast)(getString(R.string.wrongInput))
        showError(getString(R.string.wrongInput))
    }

    fun elseLogin(){
        (ToastUtil::shortToast)(getString(R.string.unexpectedError))
        showError(getString(R.string.unexpectedError))
    }

    private fun showError(message: String){
        errorLabel.visibility = View.VISIBLE
        errorLabel.text = message
    }
}