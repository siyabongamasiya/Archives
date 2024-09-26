package media.project.archives.Presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.isSent
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Presentation.AudioPlayerScreen.DrawAudioPlayerScreen
import media.project.archives.Presentation.Homescreen.DrawHomeScreen
import media.project.archives.Presentation.ImageViewerScreen.DrawImageViewerScreen
import media.project.archives.Presentation.VideoViewerScreen.DrawVideoViewerScreen
import media.project.archives.Utils.EventBus
import media.project.archives.ui.theme.ArchivesTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArchivesTheme {
                DrawApp()
            }
        }
    }

}

@Composable
fun DrawApp(){
    val navHostController = rememberNavController()

    Scaffold(bottomBar = {
        var sendStatus by remember{
            mutableStateOf(isSent)
        }
        val coroutine = rememberCoroutineScope()
        val context = LocalContext.current

        LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
            coroutine.launch {
                EventBus.saved.collect{ result ->
                    if (result == savedSuccessfully){
                        Toast.makeText(context, savedSuccessfully, Toast.LENGTH_SHORT).show()
                        sendStatus = isSent
                    }else if (result == inProgress){
                        sendStatus = inProgress
                    }else if(result == errorOccured){
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                        sendStatus = errorOccured
                    }

                }
            }
        }



        Box (modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)){

            if (sendStatus == inProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

        }
    }) {paddingValues ->
        paddingValues

        NavHost(navController = navHostController,
            ScreenRoutes.HomeScreen().route,
            modifier = Modifier.fillMaxSize()) {
            composable(ScreenRoutes.HomeScreen().route) {
                DrawHomeScreen(navHostController)
            }
            composable<ScreenRoutes.ImageViewer> {
                val args = it.toRoute<ScreenRoutes.ImageViewer>()
                DrawImageViewerScreen(
                    navHostController,
                    args.url,
                    args.title,
                    args.isArchived,
                    args.downloadUri,
                    args.islocal
                )
            }
            composable<ScreenRoutes.VideoViewer> {
                val args = it.toRoute<ScreenRoutes.VideoViewer>()
                DrawVideoViewerScreen(
                    navHostController,
                    args.url,
                    args.title,
                    args.isArchived,
                    args.downloadUri,
                    args.islocal
                )
            }
            composable<ScreenRoutes.AudioPlayer> {
                val args = it.toRoute<ScreenRoutes.AudioPlayer>()
                DrawAudioPlayerScreen(
                    navHostController,
                    args.title,
                    args.artist,
                    args.duration,
                    args.url,
                    args.id,
                    args.isArchived,
                    args.downloadUri,
                    args.islocal
                )
            }
        }
    }


}

