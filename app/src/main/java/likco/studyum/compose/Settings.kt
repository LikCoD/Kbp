package likco.studyum.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import likco.studyum.models.TopBarItem
import likco.studyum.models.User
import likco.studyum.services.UserService

@Composable
fun Settings(
    setTitle: (String) -> Unit,
    topBar: (List<TopBarItem>) -> Unit,
    setUser: (User?) -> Unit
) {
    setTitle("Settings")

    var user by remember { mutableStateOf(UserService.user ?: return) }

    var login by remember { mutableStateOf(user.login) }
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .size(250.dp, 450.dp)
                .align(Alignment.Center)
        ) {
            OutlinedTextField(
                value = login,
                onValueChange = { login = it },
                label = { Text(text = "Login") }
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(text = "Name") }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(text = "Email") }
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(text = "Password") }
            )

            OutlinedTextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = { Text(text = "Confirm password") }
            )

            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        UserService.edit(
                            UserService.UserEditData(
                                login,
                                name,
                                email,
                                password
                            )
                        ) { it.printStackTrace() }
                        user = UserService.user ?: return@launch
                        setUser(UserService.user)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Confirm")
            }

            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        UserService.revokeToken { it.printStackTrace() }
                        setUser(UserService.user)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Revoke token")
            }
        }
    }
}