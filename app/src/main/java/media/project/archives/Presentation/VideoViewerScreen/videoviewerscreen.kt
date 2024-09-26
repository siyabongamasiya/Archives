package media.project.archives.Presentation.VideoViewerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import media.project.archives.Data.Model.Video
import media.project.archives.Presentation.Components.DrawVideoButton
import media.project.archives.Presentation.Components.customButton
import media.project.archives.Presentation.Components.topSection
import media.project.archives.Presentation.Homescreen.HomeScreenViewModel
import media.project.archives.ui.theme.ArchivesTheme

@Composable
fun DrawVideoViewerScreen(
    navController: NavHostController,
    url: String,
    title: String,
    archived: Boolean,
    downloadUri: String,
    islocal: Boolean
){
    val videoViewerViewModel = hiltViewModel<VideoViewerViewModel>()

    ArchivesTheme {
        Scaffold(topBar = {
            topSection(navController = navController)
        }) {paddingvalues ->
            val video = Video()
            video.setTit(title)
            video.setUl(url)
            video.setIsArch(archived)
            video.setDownloadUr(downloadUri)
            video.setIsLoc(islocal)

            midSectionVideoViewer(navController = navController,
                paddingValues = paddingvalues,
                videoViewerViewModel = videoViewerViewModel,
                video = video)
        }
    }

}


@Composable
fun midSectionVideoViewer(navController: NavController,
                          paddingValues: PaddingValues,
                          videoViewerViewModel: VideoViewerViewModel,
                          video : Video){
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    val exoPlayer = ExoPlayer.Builder(context).build()
    val mediaitem = if(video.isLocal) MediaItem.fromUri(video.url) else MediaItem.fromUri(video.downloaduri)

    LaunchedEffect(key1 = Unit) {
        videoViewerViewModel.initialize(context)
    }

    LaunchedEffect(Unit) {
        exoPlayer.setMediaItem(mediaitem)
        exoPlayer.prepare()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(
            top = paddingValues.calculateTopPadding(),
            bottom = 56.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f)
        )

    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}