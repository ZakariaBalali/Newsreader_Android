package nl.balali.zakaria.newsreader636655

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import nl.balali.zakaria.newsreader636655.utilities.SessionManager
import nl.balali.zakaria.newsreader636655.utilities.ToastUtil
import nl.balali.zakaria.newsreader636655.view.fragment.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav : BottomNavigationView
    private lateinit var mainFragment : Fragment
    private lateinit var likedNewsFragment : Fragment
    private lateinit var logoutFragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNav = findViewById(R.id.bottomNav)
        (SessionManager::setup)(applicationContext)
        (ToastUtil::setup)(applicationContext)
        mainFragment = MainFragment()

        if((SessionManager::isLogin)()){
            logoutFragment = LogoutFragment()
            likedNewsFragment = LikedNewsFragment()
            makeCurrentFragment(logoutFragment)
        }else{
            logoutFragment = LoginFragment()
            likedNewsFragment = logoutFragment
            makeCurrentFragment(mainFragment)
        }

        bottomNav.setOnItemSelectedListener{
            when(it.itemId){
                R.id.navMain -> makeCurrentFragment(mainFragment)
                R.id.navLikedNews -> makeCurrentFragment(likedNewsFragment)
                R.id.navLogOut -> makeCurrentFragment(logoutFragment)
            }
            true
        }
    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayoutWrapper, fragment)
            commit()
        }
}