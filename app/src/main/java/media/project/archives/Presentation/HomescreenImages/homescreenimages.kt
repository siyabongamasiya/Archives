package media.project.archives.Presentation.HomescreenImages

import android.content.ContentUris
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Presentation.Components.Item
import media.project.archives.Presentation.Components.ItemsList
import media.project.archives.Presentation.Homescreen.HomeScreenViewModel
import media.project.archives.Presentation.ScreenRoutes
import media.project.archives.Utils.getArt
import media.project.archives.ui.theme.ArchivesTheme
import kotlin.system.exitProcess


@Composable
fun DrawHomeScreenImages(navHostController: NavHostController,homeScreenViewModel: HomeScreenViewModel){
    ArchivesTheme {
        requestPermission {
            Scaffold {paddingValues ->
                midSectionHomeScreenImages(navController = navHostController,
                    paddingValues = paddingValues,
                    homeScreenViewModel)
            }
        }

    }
}

@Composable
fun midSectionHomeScreenImages(navController: NavController,
                               paddingValues: PaddingValues,
                               homeScreenViewModel: HomeScreenViewModel){


    val images = homeScreenViewModel.images.collectAsState()
    val refreshed = homeScreenViewModel._refreshedImages.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        homeScreenViewModel.getImages(context)
    }

    if (refreshed.value) {
        ItemsList(
            items = images.value,
            paddingValues = paddingValues,
            navController = navController,
            homeScreenViewModel = homeScreenViewModel
        )
    }else{
        Box (modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)){

            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.tertiary
            )

        }
    }
}



@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun requestPermission(content : @Composable()() -> Unit){
    val context = LocalContext.current
    val storagepermission = rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)
    val writestoragepermission = rememberPermissionState(permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val requestPermissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {isGranted ->
        if(isGranted){
            Toast.makeText(context,"Permission granted!!",Toast.LENGTH_SHORT).show()
        }else{
            exitProcess(0)
        }
    }

    LaunchedEffect(key1 = storagepermission) {
        if (!storagepermission.status.isGranted){
            requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    LaunchedEffect(key1 = writestoragepermission) {
        if (!writestoragepermission.status.isGranted){
            requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    if (storagepermission.status.isGranted && writestoragepermission.status.isGranted){
        content.invoke()
    }

}