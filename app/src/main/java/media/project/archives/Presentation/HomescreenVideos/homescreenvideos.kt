package media.project.archives.Presentation.HomescreenVideos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import media.project.archives.Data.Model.Image
import media.project.archives.Presentation.Components.Item
import media.project.archives.Presentation.Components.ItemsList
import media.project.archives.Presentation.Homescreen.HomeScreenViewModel
import media.project.archives.Presentation.ScreenRoutes
import media.project.archives.ui.theme.ArchivesTheme

@Composable
fun DrawHomeScreenVideos(navHostController: NavHostController,homeScreenViewModel: HomeScreenViewModel){
    ArchivesTheme {
        Scaffold {paddingValues ->
            midSectionHomeScreenVideos(navController = navHostController,
                paddingValues = paddingValues,
                homeScreenViewModel)
        }
    }
}

@Composable
fun midSectionHomeScreenVideos(navController: NavController,
                               paddingValues: PaddingValues,
                               homeScreenViewModel: HomeScreenViewModel){
    val videos = homeScreenViewModel.videos.collectAsState()
    val context = LocalContext.current
    val refreshed = homeScreenViewModel._refreshedVideos.collectAsState()

    LaunchedEffect(key1 = videos) {
        homeScreenViewModel.getVideos(context)
    }

    if (refreshed.value) {
        ItemsList(
            items = videos.value,
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