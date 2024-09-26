package media.project.archives.Presentation.AudioPlayerScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import media.project.archives.Data.Model.Song
import media.project.archives.AudioPlayer.AudioPlayer
import media.project.archives.Presentation.Components.DrawAudioButton
import media.project.archives.Presentation.Components.customButton
import media.project.archives.Presentation.Components.topSection
import media.project.archives.Utils.getArt
import media.project.archives.ui.theme.ArchivesTheme


@Composable
fun DrawAudioPlayerScreen(
    navHostController: NavHostController,
    title: String,
    artist: String,
    duration: String,
    url: String,
    id: String,
    archived: Boolean,
    downloadUri: String,
    islocal: Boolean
){
    val audioPlayerViewModel = hiltViewModel<AudioPlayerViewModel>()

    ArchivesTheme {
        val song = Song(artist = artist,duration = duration, id = id)
        song.setTit(title)
        song.setUl(url)
        song.setIsArch(archived)

        Scaffold(topBar = {
            topSection(navController = navHostController)
        }) {paddingvalues ->
            midSectionAudioPlayer(
                paddingValues = paddingvalues,
                song = song,
                audioPlayerViewModel = audioPlayerViewModel)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun midSectionAudioPlayer(
                          paddingValues: PaddingValues,
                          song : Song,
                          audioPlayerViewModel: AudioPlayerViewModel){

    val context = LocalContext.current
    val audioPlayer by remember{
        mutableStateOf(AudioPlayer(context,song))
    }



    val songState by audioPlayer.songState.collectAsState()


    var currentTime = rememberSaveable {
        mutableStateOf(songState.currentTime)
    }

    var totalTime = rememberSaveable {
        mutableStateOf(songState.totalTime)
    }

    val imageUrl = getArt(song.id)





    DisposableEffect(Unit) {
        audioPlayerViewModel.initialize(context = context)
        onDispose {
            audioPlayer.Dispose()
        }
    }


    totalTime.value = audioPlayer.formatTotalTime()
    currentTime.value = audioPlayer.formatCurrentTime()


    if (songState.isPlaying) {
        LaunchedEffect(key1 = Unit) {
            while (true) {
                delay(200)
                audioPlayer.getProgress()
            }
        }
    }


    Column (modifier = Modifier
        .fillMaxSize()
        .padding(
            top = paddingValues.calculateTopPadding(),
            start = 10.dp,
            end = 10.dp,
            bottom = 56.dp
        ),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally){
        
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(50.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            
            Text(text = "Now Playing",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge)

            GlideImage(
                model = imageUrl,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(80.dp),
                contentScale = ContentScale.Fit,
                contentDescription = "Artist image")
            
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = song.title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge)
                Text(text = song.artist,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleMedium)
            }
            
        }
        
        Box (modifier = Modifier.fillMaxWidth()){
            Icon(
                modifier = Modifier
                    .clickable {
                        audioPlayer.PlayPauseSong()
                    }
                    .size(50.dp)
                    .align(Alignment.Center),
                imageVector = if(!songState.isPlaying){Icons.Default.PlayArrow}
                else{Icons.Default.Pause},
                contentDescription = "play/pause")
        }
        
        Column(modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){

                Text(
                    text = currentTime.value,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(text = totalTime.value,
                    style = MaterialTheme.typography.titleSmall)
                
            }


            //progress bar
            Slider(
                value = (songState.currentTimeMs.toFloat()/songState.totalTimeMs.toFloat()),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.tertiary,
                    activeTrackColor = MaterialTheme.colorScheme.tertiary,
                    inactiveTrackColor = Color.DarkGray
                ),
                onValueChangeFinished = {

                },
                onValueChange = {percentage->
                    audioPlayer.ChangePosition((songState.totalTimeMs*percentage).toLong())
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }


}