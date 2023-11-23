package nl.balali.zakaria.newsreader636655.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import nl.balali.zakaria.newsreader636655.R
import nl.balali.zakaria.newsreader636655.utilities.SessionManager

class LogoutFragment() : Fragment() {

    private lateinit var accountName : TextView
    private lateinit var logoutButton : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.logout_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accountName = view.findViewById(R.id.accountName)
        logoutButton = view.findViewById(R.id.logoutButton)
        accountName.text = (SessionManager::getUserName)()
        logoutButton.setOnClickListener {
            (SessionManager::logout)()
        }
    }
}