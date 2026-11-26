package id.neotica.profile.presentation.about

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AboutViewModel : ViewModel() {
    val state = MutableStateFlow(AboutState()).asStateFlow()
}
