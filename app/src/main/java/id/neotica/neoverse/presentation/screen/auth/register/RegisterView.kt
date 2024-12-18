package id.neotica.neoverse.presentation.screen.auth.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.PasswordTextField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.LoadingIndicatorBool
import id.neotica.ui.components.NeoButton
import id.neotica.ui.navigation.Screen
import id.neotica.ui.theme.DarkPrimary
import id.neotica.ui.theme.NegativePrimary
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
fun RegisterView(
    navController: NavController,
    viewModel: RegisterViewModel = koinViewModel()
    ) {
    val nameValue = remember { mutableStateOf("") }
    val nameNoWhiteSpace = remember(nameValue.value) {
        !nameValue.value.contains(Regex("\\s"))
    }
    val blockedWord = listOf(
        "aa5u", "a5uu", "aasu", "ajg", "alay", "ampas", "anus", "anjeng", "anjing", "anjir", "antek", "asem",
        "asing", "ateis", "autis", "ayam kampus", "awuk", "b1ch", "bab1", "babi", "bacot", "bajang", "bajingan",
        "banci", "bandot", "bangkai", "bangsat", "bani", "bani kotak", "bego", "bejat", "bencong", "berak",
        "berengsek", "bgsd", "bgst", "bispak", "bisu", "bisyar", "bjat", "bods", "bokep", "bokong", "bong",
        "bodoh", "budek", "burik", "burit", "butt", "cacat", "cawk", "cct", "cebong",
        "celeng", "cerewet", "cipok", "cl1t", "cntz", "cocot", "congor", "crut", "crot", "cukimak",
        "cums", "cunt", "culun", "cungkring", "cupu", "dick", "d1ck", "d4mn", "damn", "dancok", "dongok", "dunguk",
        "dungu", "dyke", "edan", "eewe", "elek", "entot", "f4rt", "fags", "fagz", "fcuk", "fukr",
        "fukk", "fuck", "gay", "gays", "gayz", "gawk", "gauk", "gawu", "gelud", "gembel", "gembrot", "gendut",
        "genjit", "genjek", "geblek", "gila", "gblk", "g1la", "germo", "goblok", "hina", "homo",
        "idiot", "jablay", "jamban", "jancok", "jancuk", "jemb", "jembud", "jembut", "jijik", "jilmek", "jiss",
        "jizz", "jlek", "kacrut", "kafir", "kancut", "kampang", "kampret", "kampungan", "kent", "kere", "kejam",
        "keparat", "kimak", "kimax", "kimboknya", "kl1t", "knob", "kntl", "kont0l", "kontol", "k0nt0l",
        "k0ntol", "koreng", "kotl", "krempeng", "kunti", "kunyuk", "lengser", "leec", "lesbi",
        "lgbt", "lonte", "mani", "mampus", "mat1", "mbut", "me2k", "mek1", "meki", "memek", "mesum", "modar",
        "monyet", "mucikari", "mulyono", "munafik", "n1gr", "najis", "naz1", "ncuk", "ndas", "nenen", "nete", "ngaceng",
        "ngewe", "ngewek", "ngentot", "nista", "noob", "nt1l", "ntut", "nthu", "nyepong", "onta", "p1ss",
        "panasbung", "panastak", "pantat", "pantek", "pasukan nasi", "pcun", "pecun", "pekok", "peler", "pel1",
        "pelacur", "peju", "p3ju", "perek", "picek", "pler", "porno", "puki", "pukimak", "pus1", "puss", "pups",
        "rejin", "rezim", "sampah", "sange", "sarap", "sepong", "seponk", "s3pong", "serbet", "seks",
        "silit", "sinting", "sipit", "sitip", "slut", "songong", "sompret", "sontoloyo", "stfu", "tai", "taek",
        "taik", "taplak", "temp", "tempik", "tepos", "terkutuk", "tetek", "titit", "t1ts", "titt", "tobrut",
        "toket", "tol", "tolol", "transgendertuyul", "twat", "udik", "up1l", "utek", "vags", "wank", "wdus",
        "xhamster.com"
        ).toSet().toList()
    val emailValue = remember { mutableStateOf("") }
    val passwordValue = remember { mutableStateOf("") }
    var isButtonPressed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var isUserExist by remember { mutableStateOf(false) }
    var onValueChange by remember { mutableStateOf(false) }
    var userExistLoading by remember { mutableStateOf(false) }


    val context = LocalContext.current

    LaunchedEffect(isButtonPressed) {
        if (isButtonPressed) {
            viewModel.registerStatus.collect { result ->
                when (result) {
                    is ApiResult.Loading -> isLoading = true
                    is ApiResult.Success -> {
                        isLoading = false
                        navController.navigate(Screen.VerifyEmailScreen)
                        Toast.makeText(
                            context,
                            "Success registered ${nameValue.value}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ApiResult.Error -> {
                        isLoading = false
                        Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.isUserExist.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    userExistLoading = true
                    isUserExist = false
                }
                is ApiResult.Success -> {
                    userExistLoading = false
                    val user = result.data
                    user?.let {
                        isUserExist = !it
                    }
                }
                is ApiResult.Error -> {
                    userExistLoading = false
                    isUserExist = false
                }
            }
        }
    }

    LaunchedEffect(onValueChange) {
        viewModel.checkUsername(nameValue.value)
    }

    BasicScaffold(
        "Register",
        true,
        navController = navController
    ) {
        Box(Modifier.padding(vertical = 4.dp, horizontal = 16.dp)) {
            Column {
                LoadingIndicatorBool(isLoading = isLoading) {
                    Box(Modifier.weight(1f)) {
                        Column {
                            NeoTextField(
                                value = nameValue, label = "Username",
                                onValueChange = {
                                    onValueChange = !onValueChange
                                }
                            )
                            when {
                                blockedWord.any {
                                    nameValue.value.contains(it, ignoreCase = true)
                                } -> Text(
                                    "Username contains prohibited words.",
                                    color = NegativePrimary
                                )
                                !isUserExist -> if (!userExistLoading) Text("Username is already used.")
                                else -> {}
                            }
                            NeoTextField(value = emailValue, label = "Email")
                            Spacer(Modifier.padding(4.dp))
                            PasswordTextField(value = passwordValue, placeholder = "Password")
                            Spacer(Modifier.padding(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    color = DarkPrimary,
                                    fontSize = 14.sp,
                                    text = "Privacy Policy",
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.PrivacyPolicyScreen)
                                    }
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    color = DarkPrimary,
                                    fontSize = 14.sp,
                                    text = "Terms of Service",
                                    modifier = Modifier.clickable {
                                        navController.navigate(Screen.TermsOfServiceScreen)
                                    }
                                )
                            }
                            Spacer(Modifier.padding(16.dp))
                            Text("By registering, you are already accepting the Terms and Service and Privacy Policy of this app.")
                        }
                    }
                    Row(
                        Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Column {
                            when {
                                nameValue.value.isEmpty() -> Text("Please fill all the fields.")
                                !isUserExist -> Text("Username is already used.")
                            }
                            NeoButton(
                                "Register",
                                maxWidth = true,
                                enabled = nameValue.value.isNotEmpty()
                                        && emailValue.value.isNotEmpty()
                                        && passwordValue.value.isNotEmpty()
                                        && nameNoWhiteSpace
                                        && isUserExist,
                            ) {
                                isButtonPressed = true
                                viewModel.register(nameValue.value.replaceFirstChar {
                                    it.lowercase(
                                        Locale.getDefault()
                                    )
                                }, emailValue.value, passwordValue.value)
                            }
                        }
                    }
                }
            }
        }
    }
}