package media.project.archives.Presentation.Components

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.VideoFrameDecoder
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.launch
import media.project.archives.Constants.AddtoArchive
import media.project.archives.Constants.DownloadItem
import media.project.archives.Constants.typeImage
import media.project.archives.Constants.typeVideo
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item
import media.project.archives.Presentation.AudioPlayerScreen.AudioPlayerViewModel
import media.project.archives.Presentation.Homescreen.HomeScreenViewModel
import media.project.archives.Presentation.ImageViewerScreen.ImageViewerViewModel
import media.project.archives.Presentation.ScreenRoutes
import media.project.archives.Presentation.VideoViewerScreen.VideoViewerViewModel
import media.project.archives.Utils.getArt
import kotlin.system.exitProcess

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun Item(
    modifier: Modifier,
    video : Video?,
    image : Image?,
    audio : Song?,
    icon : ImageVector?){
    val context = LocalContext.current

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .fillMaxSize()
            .weight(0.9f)){
            if (video != null){
                val imageLoader = ImageLoader.Builder(context)
                    .components {
                        add(VideoFrameDecoder.Factory())
                    }.crossfade(true)
                    .build()

                val painter = rememberAsyncImagePainter(model = video.url,imageLoader= imageLoader)

                Image(modifier = Modifier.fillMaxSize(),
                    painter = painter,
                    contentScale = ContentScale.Crop,
                    contentDescription = "video thumbnail")
            }else if (image != null){
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    model = image.url,
                    contentScale = ContentScale.Crop,
                    contentDescription = "item image")
            }else if (audio != null){
                val imageUrl = getArt(audio.id)
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    model = imageUrl,
                    contentDescription = "item song")
            }



            if(icon != null){
                Icon(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onTertiary,
                    imageVector = icon,
                    contentDescription = "item icon")
            }
        }

        val title : String
        if(image != null){
            title = image.title
        }else if(video != null){
            title = video.title
        }else if(audio != null){
            title = audio.title
        }else{
            title = "No title"
        }

        Text(
            modifier = Modifier
                .weight(0.1f)
                .background(Color.Black)
                .fillMaxWidth(),
            text = title,
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleSmall)
    }

}

@Composable
fun topSection(navController: NavController){
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.secondary)
        .padding(10.dp)){
        Icon(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable {
                    navController.navigateUp()
                },
            imageVector = Icons.Default.ArrowBackIosNew,
            tint = MaterialTheme.colorScheme.onSecondary,
            contentDescription = "back")
    }
}

@Composable
fun ItemsList(items : List<Item>,
              paddingValues: PaddingValues,
              navController: NavController,
              homeScreenViewModel: HomeScreenViewModel){

    val mappedList = homeScreenViewModel.MapItems(items)

    LazyVerticalGrid(modifier = Modifier
        .fillMaxSize()
        .padding(top = paddingValues.calculateTopPadding()),
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        items(mappedList.reversed()){item ->

            if (item is Image) {
                DrawImageItem(image = item, navController = navController)
            }else if (item is Video) {
                DrawVideoItem(video = item, navController = navController)
            }else if (item is Song) {
                DrawAudioItem(audio = item, navController = navController)
            }
        }
    }
}

@Composable
fun DrawImageItem(image : Image,navController: NavController){
    val modifier: Modifier = if (image.isArchived && image.isLocal) {
        Modifier.border(width = 5.dp, color = MaterialTheme.colorScheme.tertiary)
    }else if (!image.isLocal && image.isArchived) {
        Modifier.border(width = 5.dp, color = Color.Red)
    }else{
        Modifier
    }

    Item(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .width(80.dp)
            .height(200.dp)
            .clickable {
                navController.navigate(
                    ScreenRoutes.ImageViewer(
                        image.url,
                        image.title,
                        image.isArchived,
                        image.downloaduri,
                        image.isLocal
                    )
                )
            },
        image = image,
        audio = null,
        video = null,
        icon = Icons.Default.Image
    )
}

@Composable
fun DrawVideoItem(video: Video,navController: NavController){
    val modifier: Modifier = if (video.isArchived && video.isLocal) {
        Modifier.border(width = 5.dp, color = MaterialTheme.colorScheme.tertiary)
    }else if (!video.isLocal && video.isArchived) {
        Modifier.border(width = 5.dp, color = Color.Red)
    }else{
        Modifier
    }

    Item(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .width(80.dp)
            .height(200.dp)
            .clickable {
                navController.navigate(
                    ScreenRoutes.VideoViewer(
                        video.url,
                        video.title,
                        video.isArchived,
                        video.downloaduri,
                        video.isLocal
                    )
                )
            },
        image = null,
        audio = null,
        video = video,
        icon = Icons.Default.Videocam
    )
}

@Composable
fun DrawAudioItem(audio: Song,navController: NavController){
    val modifier: Modifier = if (audio.isArchived && audio.isLocal) {
        Modifier.border(width = 5.dp, color = MaterialTheme.colorScheme.tertiary)
    }else if (!audio.isLocal && audio.isArchived) {
        Modifier.border(width = 5.dp, color = Color.Red)
    }else{
        Modifier
    }

    Item(
        modifier = modifier
            .background(MaterialTheme.colorScheme.secondary)
            .width(80.dp)
            .height(200.dp)
            .clickable {
                navController.navigate(
                    ScreenRoutes.AudioPlayer(
                        audio.title,
                        audio.artist,
                        audio.duration,
                        audio.url,
                        audio.id,
                        audio.isArchived,
                        audio.downloaduri,
                        audio.isLocal
                    )
                )
            },
        image = null,
        audio = audio,
        video = null,
        icon = null
    )
}

@Composable
fun customButton(modifier: Modifier,text : String,onclick : () -> Unit){
    ElevatedButton(
        onClick = {
            onclick.invoke()
        }, colors = ButtonDefaults.buttonColors(
            containerColor = if (text == AddtoArchive) MaterialTheme.colorScheme.tertiary else Color.Red
        )) {
        Text(text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onTertiary)
    }
}

@Composable
fun DrawImageButton(image: Image,modifier: Modifier,imageViewerViewModel: ImageViewerViewModel){
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    if (!image.isArchived) {
        customButton(modifier = modifier, text = AddtoArchive ) {
            coroutine.launch {
                imageViewerViewModel.saveImageArchives(image)
            }

        }
    }

    if (!image.isLocal){
        val launcher = rememberLauncherForActivityResult(contract = CreateDocument("image/jpeg")) { uri ->
            coroutine.launch {
                imageViewerViewModel.DownloadFile(image,context, uri!!)
            }
        }

        customButton(modifier = modifier, text = DownloadItem) {
            launcher.launch(image.title)
        }
    }
}

@Composable
fun DrawVideoButton(video: Video,modifier: Modifier,videoViewerViewModel: VideoViewerViewModel){
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    if (!video.isArchived) {
        customButton(modifier = modifier, text = AddtoArchive ) {
            coroutine.launch {
                //videoViewerViewModel.saveVideoArchives(video = video)
            }

        }
    }

    if (!video.isLocal){
        val launcher = rememberLauncherForActivityResult(contract = CreateDocument("video/mp4")) { uri ->
            coroutine.launch {
                videoViewerViewModel.DownloadFile(video,context, uri!!)
            }
        }

        customButton(modifier = modifier, text = DownloadItem) {
            coroutine.launch {
                launcher.launch(video.title)
            }

        }
    }
}

@Composable
fun DrawAudioButton(audio: Song,modifier: Modifier,audioPlayerViewModel: AudioPlayerViewModel){
    val coroutine = rememberCoroutineScope()
    val context = LocalContext.current

    if (!audio.isArchived) {
        customButton(modifier = modifier, text = AddtoArchive ) {
            coroutine.launch {
                //audioPlayerViewModel.saveAudioArchives(audio)
            }

        }
    }

    if (!audio.isLocal){
        val launcher = rememberLauncherForActivityResult(contract = CreateDocument("audio/mpeg")) { uri ->
            coroutine.launch {
                audioPlayerViewModel.DownloadFile(audio,context, uri!!)
            }
        }
        customButton(modifier = modifier, text = DownloadItem) {


            coroutine.launch {
                launcher.launch(audio.title)
            }

        }
    }
}