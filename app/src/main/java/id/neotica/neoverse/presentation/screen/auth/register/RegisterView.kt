package id.neotica.neoverse.presentation.screen.auth.register

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
import com.google.firebase.firestore.FirebaseFirestore
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.neoverse.data.model.User
import id.neotica.neoverse.presentation.navigation.RootScreen

private lateinit var auth: FirebaseAuth
lateinit var firestore : FirebaseFirestore

@Composable
fun RegisterView(navController: NavController, paddingValues: PaddingValues) {
    Column(modifier = Modifier.padding(paddingValues)) {
        val nameValue = remember { mutableStateOf("") }
        val emailValue = remember { mutableStateOf("") }
        val passwordValue = remember { mutableStateOf("") }
        val context = LocalContext.current

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        Text("Register")
        NeoTextField(value = nameValue, label = "Name")
        NeoTextField(value = emailValue, label = "Email")
        PasswordTextField(value = passwordValue, placeholder = "Password")

        Button(onClick = {
            auth.createUserWithEmailAndPassword(emailValue.value, passwordValue.value).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val dataUser = User(
                        userId = user!!.uid,
                        username = nameValue.value,
                        userEmail = emailValue.value
                    )

                    firestore.collection("Users").document(user.uid).set(dataUser)

                    navController.navigate(RootScreen.HomeNav)

                } else {
                    Toast.makeText(context, "Oops not working.", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text(text = "Register")
        }
    }
}