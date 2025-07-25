package id.neotica.home.updateapp

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import id.neotica.res.MR
import id.neotica.ui.shared.components.BasicScaffold
import id.neotica.ui.shared.components.NeoButton
import id.neotica.ui.shared.theme.FontSize
import androidx.core.net.toUri

@Composable
fun UpdateAppView() {
    BasicScaffold(
        topBarTitle = "Update Screen",
        topBarVisibility = false
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(MR.images.logo_neoverse),
                contentScale = ContentScale.Crop,
                contentDescription = "")
            Text(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                fontSize = FontSize.Big,
                textAlign = TextAlign.Center,
                text = "Update Neoverse"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                fontSize = FontSize.Normal,
                textAlign = TextAlign.Center,
                text = "This version of Neoverse has expired. Please go to the Play Store and update it."
            )
            Spacer(Modifier.weight(1f))
            NeoButton(
                text = "Update Neoverse"
            ) {
                val intent = Intent(Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=id.neotica.neoverse".toUri())
                context.startActivity(intent)
            }
        }
    }
}