package com.ldc.kbp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ldc.kbp.fragments.SapperFragment
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Timetable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var personClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Files.getConfig(this)
        Files.getHomeworkList(this)

        AndroidThreeTen.init(application)

        runBlocking(Dispatchers.IO) {
            Groups.loadTimetable()

            mainTimetable = Timetable.loadTimetable(config.timetableInfo)
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        lifecycleScope.launch(Dispatchers.IO) {
            Groups.loadGroupsFromJournal()
            Groups.loadTeachersFromJournal()
        }

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_timetable,
                R.id.nav_diary,
                R.id.nav_journal,
                R.id.nav_empty_room,
                R.id.nav_statement,
                R.id.nav_settings
            ),
            drawer_layout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun onImgClick(view: View?) {
        if (view == null) return

        personClickCount++

        if (personClickCount == 5) {
            supportFragmentManager.beginTransaction().let {
                it.replace(R.id.nav_host_fragment, SapperFragment())
                it.commit()
            }

            personClickCount = 0
        }
    }
}