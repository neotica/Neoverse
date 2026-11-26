package id.neotica.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import id.neotica.auth.domain.AuthRepository
import id.neotica.domain.ApiResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepo: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onUsernameChange(value: String) {
        _state.update { it.copy(username = value, usernameError = validateUsername(value)) }
    }

    fun onEmailChange(value: String) {
        _state.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onAgreedChange(value: Boolean) {
        _state.update { it.copy(agreedToTerms = value) }
    }

    fun onTogglePasswordVisibility() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun register() {
        if (!_state.value.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, toastMessage = null) }
            val current = _state.value
            when (val result = authRepo.register(
                username = current.username.lowercase(),
                email = current.email,
                password = current.password,
            )) {
                is ApiResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    _events.send(RegisterEvent.Registered(current.email))
                }
                is ApiResult.Error -> {
                    Logger.e { "registervm: ${result.errorMessage}" }
                    _state.update {
                        it.copy(isLoading = false, toastMessage = result.errorMessage ?: "Registration failed")
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onToastDismissed() {
        _state.update { it.copy(toastMessage = null) }
    }

    private fun validateUsername(username: String): String? = when {
        username.isBlank() -> null
        blockedWord.any { username.contains(it, ignoreCase = true) } -> "Username contains prohibited words."
        username.length < 3 -> "Username must be at least 3 characters."
        username.contains(" ") -> "Username cannot contain spaces."
        else -> null
    }

    val blockedWord = listOf(
        "nigga", "nigger", "nigguh", "neoverse", "neotica",
        "aa5u", "a5uu", "aasu", "ajg", "alay", "ampas", "anus", "anjeng", "anjing", "anjir", "antek", "asem",
        "asing", "autis", "ayamkampus", "awuk", "b1ch", "bab1", "babi", "bacot", "bajang", "bajingan",
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
        "xhamster.com", "itil"
    ).toSet().toList()
}
