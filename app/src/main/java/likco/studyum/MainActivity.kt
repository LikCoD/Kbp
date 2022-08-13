package likco.studyum

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
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
import com.ldc.kbp.API_URL
import com.ldc.kbp.JOURNAL_URL
import com.ldc.kbp.models.Files
import com.ldc.kbp.models.Groups
import com.ldc.kbp.shortToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import likco.studyum.compose.Drawer
import likco.studyum.compose.JournalView
import likco.studyum.compose.Schedule
import likco.studyum.compose.Settings
import likco.studyum.compose.icons.Journal
import likco.studyum.compose.icons.Schedule
import likco.studyum.models.DrawerItem
import likco.studyum.models.TopBarItem
import likco.studyum.models.User
import likco.studyum.services.UserService

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("preferences", MODE_PRIVATE)
        val isNotificationsConnected = prefs.getBoolean("isNotificationsConnected", true)
        val versionNotifications = prefs.getBoolean("notificationsV2.0", true)

        AndroidThreeTen.init(application)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                shortToast(this, R.string.load_config_error)
                return@addOnCompleteListener
            }

            API_URL = Firebase.remoteConfig.getString("apiUrl")
            JOURNAL_URL = Firebase.remoteConfig.getString("journalUrl")

            setContent {
                val preferences = getPreferences(Context.MODE_PRIVATE)
                UserService.setPreferences(preferences)

                runBlocking(Dispatchers.IO) {
                    Groups.loadTimetable()

                    UserService.load {
                        it.printStackTrace()
                    }
                }

                MaterialTheme(
                    colors = darkColors(
                        onPrimary = Color.White,
                        primary = Color(0xFFE6BA92),
                        secondary = Color(0xFFFCFAF1),
                        surface = Color(0xFF434C5C),
                        background = Color(0xFF434C5C)
                    )
                ) {
                    var user by remember { mutableStateOf(UserService.user) }

                    Surface(modifier = Modifier.fillMaxSize()) {
                        if (user == null) {
                            LoginScreen { user = it }
                            return@Surface
                        }

                        val scaffoldState = rememberScaffoldState()
                        val scope = rememberCoroutineScope()

                        val appName = stringResource(id = R.string.app_name)

                        var topBarTitle by remember { mutableStateOf(appName) }
                        var topBarItems by remember { mutableStateOf(listOf<TopBarItem>()) }

                        var selectedItem by remember { mutableStateOf("Journal") }

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
                                    actions = {
                                        topBarItems.forEach {
                                            IconButton(onClick = it.onClick) {
                                                Icon(
                                                    imageVector = it.icon,
                                                    contentDescription = it.contentDescription
                                                )
                                            }
                                        }
                                    },
                                    backgroundColor = MaterialTheme.colors.primary
                                )
                            },
                            drawerContent = {
                                if (user != null)
                                    Drawer(
                                        user = user!!,
                                        items = listOf(
                                            DrawerItem(
                                                text = "Schedule",
                                                icon = Icons.Default.Schedule,
                                                contentDescription = "schedule item"
                                            ),
                                            DrawerItem(
                                                text = "Journal",
                                                icon = Icons.Default.Journal,
                                                contentDescription = "schedule item"
                                            ),
                                            DrawerItem(
                                                text = "Settings",
                                                icon = Icons.Default.Settings,
                                                contentDescription = "settings item"
                                            ),
                                        ),
                                        bottomItem = DrawerItem(
                                            text = "Log out",
                                            icon = Icons.Default.ExitToApp,
                                            contentDescription = "log out item"
                                        )
                                    ) {
                                        topBarItems = emptyList()
                                        scope.launch { scaffoldState.drawerState.close() }

                                        selectedItem = it.text
                                    }
                            }
                        ) {
                            val setTitle = { title: String -> topBarTitle = title }
                            val setUser = { u: User? -> user = u }
                            val setTopBar = { items: List<TopBarItem> -> topBarItems = items }

                            when (selectedItem) {
                                "Schedule" -> Schedule(setTitle, setTopBar)
                                "Journal" -> JournalView(setTitle, setTopBar)
                                "Settings" -> Settings(setTitle, setTopBar, setUser)
                                "Log out" -> {
                                    UserService.logout()
                                    user = UserService.user
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
                val preferencesEditor = prefs.edit()
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
                val preferencesEditor = prefs.edit()
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
                        UserService.login(UserService.LoginData(login, password)) {
                            it.printStackTrace()
                        }
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