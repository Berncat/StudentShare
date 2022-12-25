package ie.wit.studentshare.ui.nav

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseUser
import ie.wit.studentshare.R
import ie.wit.studentshare.databinding.NavHeaderNavigationBinding
import ie.wit.studentshare.databinding.NavigationBinding
import ie.wit.studentshare.ui.auth.LoggedInViewModel
import ie.wit.studentshare.ui.auth.Login
import timber.log.Timber

class Navigation : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: NavigationBinding
    private lateinit var loggedInViewModel: LoggedInViewModel
    private lateinit var navHeaderBinding: NavHeaderNavigationBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Navigation Started")

        binding = NavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawerLayout = binding.drawerLayout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_navigation)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.searchFragment, R.id.listingsFragment, R.id.listingsFragment, R.id.addFragment,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        val navView: NavigationView = binding.navView
        navView.setupWithNavController(navController)
        initNavHeader()
    }

    public override fun onStart() {
        super.onStart()
        loggedInViewModel = ViewModelProvider(this).get(LoggedInViewModel::class.java)
        loggedInViewModel.liveFirebaseUser.observe(this, Observer { firebaseUser ->
            if (firebaseUser != null) {
                updateNavHeader(firebaseUser)
            }
        })
        loggedInViewModel.loggedOut.observe(this, Observer { loggedout ->
            if (loggedout) {
                startActivity(Intent(this, Login::class.java))
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_navigation)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initNavHeader() {
        Timber.i("Init Nav Header")
        headerView = binding.navView.getHeaderView(0)
        navHeaderBinding = NavHeaderNavigationBinding.bind(headerView)
    }

    private fun updateNavHeader(currentUser: FirebaseUser) {
        Timber.i("Updating Nav Header")
        navHeaderBinding.navHeaderEmail.text = currentUser.email
        if (currentUser.displayName != null) {
            navHeaderBinding.navHeaderName.text = currentUser.displayName
        }
    }

    fun signOut(item: MenuItem) {
        Timber.i("Signout Selected")
        loggedInViewModel.logOut()
        val intent = Intent(this, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}