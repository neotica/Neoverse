package id.neotica.profile.presentation.accountsettings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountSettingViewModel : ViewModel() {
    val state = MutableStateFlow(AccountSettingState()).asStateFlow()
}
