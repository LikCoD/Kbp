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
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ldc.kbp.fragments.SapperFragment
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.models.Schedule
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private var personClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        val isNotificationsConnected = preferences.getBoolean("isNotificationsConnected", true)
        val versionNotifications = preferences.getBoolean("notificationsV2.0", true)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                shortToast(this, R.string.load_config_error)
                return@addOnCompleteListener
            }

            API_URL = Firebase.remoteConfig.getString("apiUrl")

            runBlocking(Dispatchers.IO) {
                Groups.loadTimetable()

                mainSchedule = Schedule.load(config.scheduleInfo.type, config.scheduleInfo.name)
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

        if (isNotificationsConnected) {
            Firebase.messaging.subscribeToTopic("schedule_update").addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    shortToast(this, R.string.notification_schedule_update_error)
                    return@addOnCompleteListener
                }
                val preferencesEditor = preferences.edit()
                preferencesEditor.putBoolean("isNotificationsConnected", false)
                preferencesEditor.apply()
            }
        }

        if (versionNotifications) {
            Firebase.messaging.subscribeToTopic("Version2.0").addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    shortToast(this, R.string.notification_schedule_update_error)
                    return@addOnCompleteListener
                }
                val preferencesEditor = preferences.edit()
                preferencesEditor.putBoolean("notificationsV2.0", false)
                preferencesEditor.apply()
            }
        }

        Files.getConfig(this)
        Files.getHomeworkList(this)

        AndroidThreeTen.init(application)
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