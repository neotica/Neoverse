package id.neotica.profile.presentation.deleteaccount

data class DeleteAccountState(
    val confirmation: String = "",
    val isLoading: Boolean = false,
    val toastMessage: String? = null,
) {
    val canConfirm: Boolean get() = confirmation == "delete account" && !isLoading
}

sealed interface DeleteAccountEvent {
    data object AccountDeleted : DeleteAccountEvent
}
