package com.ldc.kbp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.jakewharton.threetenabp.AndroidThreeTen
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import likco.studyum.compose.Drawer
import likco.studyum.compose.Schedule
import likco.studyum.models.DrawerItem
import likco.studyum.models.User
import likco.studyum.services.UserService

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences("preferences", MODE_PRIVATE)
        val isNotificationsConnected = preferences.getBoolean("isNotificationsConnected", true)
        val versionNotifications = preferences.getBoolean("notificationsV2.0", true)

        AndroidThreeTen.init(application)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                shortToast(this, R.string.load_config_error)
                return@addOnCompleteListener
            }

            API_URL = Firebase.remoteConfig.getString("apiUrl")
            JOURNAL_URL = Firebase.remoteConfig.getString("journalUrl")

            runBlocking(Dispatchers.IO) {
                Groups.loadTimetable()

                UserService.load()
            }

            setContent {
                MaterialTheme(
                    colors = darkColors(
                        onPrimary = Color.White,
                        primary = Color(0xFFE6BA92),
                        surface = Color(0xFF434C5C)
                    )
                ) {
                    var user by remember { mutableStateOf(UserService.user) }

                    if (user == null) LoginScreen { user = it }
                    else Surface {
                        val scaffoldState = rememberScaffoldState()
                        val scope = rememberCoroutineScope()

                        val appName = stringResource(id = R.string.app_name)

                        var topBarTitle by remember { mutableStateOf(appName) }
                        var topBarActions by remember {
                            mutableStateOf<@Composable RowScope.() -> Unit>({})
                        }

                        var selectedItem by remember { mutableStateOf("Schedule") }

                        Scaffold(
                            scaffoldState = scaffoldState,
                            topBar = {
                                TopAppBar(
                                    title = {
                                        Text(text = topBarTitle)
                                    },
                                    navigationIcon = {
                                        IconButton(onClick = {
                                            scope.launch { scaffoldState.drawerState.open() }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Menu,
                                                contentDescription = "menu"
                                            )
                                        }
                                    },
                                    actions = topBarActions,
                                    backgroundColor = MaterialTheme.colors.primary
                                )
                            },
                            drawerContent = {
                                Drawer(
                                    user = user!!,
                                    items = listOf(
                                        DrawerItem(
                                            text = "Schedule",
                                            icon = Icons.Default.DateRange,
                                            contentDescription = "schedule item"
                                        ),
                                    ),
                                    bottomItem = DrawerItem(
                                        text = "Log out",
                                        icon = Icons.Default.ExitToApp,
                                        contentDescription = "log out item"
                                    )
                                ) {
                                    selectedItem = it.text
                                }
                            }
                        ) {
                            when (selectedItem) {
                                "Schedule" -> {
                                    topBarActions = Schedule { topBarTitle = it }
                                }
                            }
                        }
                    }
                }
            }

            lifecycleScope.launch(Dispatchers.IO) {
                Groups.loadGroupsFromJournal()
                Groups.loadTeachersFromJournal()
            }
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
    }


    @Composable
    fun LoginScreen(userSet: (User?) -> Unit) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .size(width = 250.dp, height = 225.dp)
                    .align(Alignment.Center)
            ) {
                val coroutineScope = rememberCoroutineScope()

                var login by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = login,
                    onValueChange = { login = it },
                    label = { Text(text = "Email") }
                )

                Spacer(Modifier.weight(1f))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.weight(1f))

                val context = LocalContext.current
                Button(onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        UserService.login(login, password)
                        if (UserService.user == null) return@launch

                        Files.saveConfig(context)
                        userSet(UserService.user)
                    }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Continue")
                }

                val uriHandler = LocalUriHandler.current
                TextButton(
                    onClick = { uriHandler.openUri("https://studyum.herokuapp.com/signup") },
                    contentPadding = PaddingValues(),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .height(20.dp)
                ) {
                    Text(text = "Sign up")
                }
            }
        }
    }
}