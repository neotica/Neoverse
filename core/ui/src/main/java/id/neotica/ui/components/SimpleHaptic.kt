package id.neotica.ui.components

import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

fun simpleHaptic(haptic: HapticFeedback) {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
}