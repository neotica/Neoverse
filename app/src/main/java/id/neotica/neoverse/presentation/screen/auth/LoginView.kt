package id.neotica.neoverse.presentation.screen.auth

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.neoverse.presentation.navigation.RootScreen
import id.neotica.neoverse.presentation.navigation.Screen

private lateinit var auth: FirebaseAuth

@Composable
fun LoginView(navController: NavController, paddingValues: PaddingValues) {
    Column(modifier = Modifier.padding(paddingValues)) {
        val emailValue = remember { mutableStateOf("") }
        val passwordValue = remember { mutableStateOf("") }

        auth = FirebaseAuth.getInstance()

        val context = LocalContext.current
        Text("Login")
        NeoTextField(value = emailValue, label = "Email")
        PasswordTextField(value = passwordValue)
        Button(onClick = {
            auth.signInWithEmailAndPassword(emailValue.value, passwordValue.value).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Succeed!", Toast.LENGTH_SHORT).show()
                    navController.navigate(RootScreen.HomeNav.route)
                } else {
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                when (exception) {
                    is FirebaseAuthInvalidUserException -> Toast.makeText(context, "Invalid credential!", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(context, "Just Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Login")
        }
        Button(onClick = { navController.navigate(Screen.Register.route) }) {
            Text("Register")
        }
    }
}