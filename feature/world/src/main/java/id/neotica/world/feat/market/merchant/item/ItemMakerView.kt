package id.neotica.world.feat.market.merchant.item

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import id.neotica.domain.ApiResult
import id.neotica.domain.model.Games
import id.neotica.domain.model.market.Item
import id.neotica.domain.model.market.ItemPrice
import id.neotica.droidcore.component.textfield.NeoTextField
import id.neotica.droidcore.component.textfield.NumberField
import id.neotica.ui.components.BasicScaffold
import id.neotica.ui.components.NeoButton
import id.neotica.world.feat.market.merchant.MerchantViewModel
import id.neotica.world.utils.compressUri
import org.koin.androidx.compose.koinViewModel

@Composable
fun ItemMakerView(
    navController: NavController,
    paddingValues: PaddingValues,
    contentInsets: WindowInsets,
    viewModel: MerchantViewModel = koinViewModel()
) {
    var currentAuras by remember { mutableStateOf<Games?>(null) }
    var onClick by remember { mutableStateOf(false) }

    val name = remember { mutableStateOf("") }
    val power = remember { mutableLongStateOf(0) }
    val description = remember { mutableStateOf("") }
    val auraPrice = remember { mutableLongStateOf(0) }
    val coalPrice = remember { mutableLongStateOf(0) }

    var isLoading by remember { mutableStateOf(false) }
    var uploadLoading by remember { mutableStateOf(false) }
    var sendResult by remember { mutableStateOf("") }

    var uriCurrent by remember { mutableStateOf("") }

    var selectedImages by remember {
        mutableStateOf<List<Uri?>>(emptyList())
    }
    val context = LocalContext.current
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                val compressedImageUri = compressUri(uri, context)
                selectedImages = listOf(compressedImageUri)
                uriCurrent = compressedImageUri.toString()
                viewModel.uploadItemPic(name.value, compressedImageUri.toString())
                onClick = true
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.totalAuras.collect { result ->
            when(result) {
                is ApiResult.Loading -> {}
                is ApiResult.Success -> {
                    currentAuras = result.data
                }
                is ApiResult.Error -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.merchant.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    isLoading = true
                }
                is ApiResult.Success -> {
                    isLoading = false
                    result.data?.let {
                        sendResult = it
                        navController.navigateUp()
                    }
                }
                is ApiResult.Error -> {
                    isLoading = false
                }
            }
        }
    }

    LaunchedEffect(onClick) {
        viewModel.uploadState.collect { result ->
            when(result) {
                is ApiResult.Loading -> {
                    onClick = false
                    uploadLoading = true
                    Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
                }
                is ApiResult.Success -> {
                    onClick = false
                    uploadLoading = false
                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                    val heroInput = Item(
                        name = name.value,
                        power = power.longValue,
                        price = ItemPrice(
                            aura = auraPrice.longValue,
                            coal = coalPrice.longValue
                        ),
                        description = description.value,
                        imageUrl = ""
                    )
                    viewModel.addItem(heroInput)
                }
                is ApiResult.Error -> {
                    onClick = false
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()
                    uploadLoading = false
                }
            }
        }
    }

    fun launchPhotoPicker() {
        singlePhotoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    BasicScaffold("Item Maker", contentInset = contentInsets) {
        LazyColumn(contentPadding = PaddingValues(bottom = paddingValues.calculateBottomPadding())) {
            item {
                Text("Your Aura: ${currentAuras?.aura ?: ""}")
                Text("Main")
                NeoTextField(name, label = "Name")
                NumberField(power, label = "Power")
                NumberField(auraPrice, label = "Aura Price")
                NumberField(coalPrice, label = "Coal Price")

                val enabledState = name.value.isNotEmpty() && power.longValue.toInt() != 0 //&& auraPrice.intValue != 0

                NeoButton("Add Item", enabled = enabledState) {
                    launchPhotoPicker()
                }
            }
        }
    }
}