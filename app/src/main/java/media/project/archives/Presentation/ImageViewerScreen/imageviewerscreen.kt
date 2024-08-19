package media.project.archives.Presentation.ImageViewerScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kotlinx.coroutines.launch
import media.project.archives.Constants.AddtoArchive
import media.project.archives.Constants.DownloadItem
import media.project.archives.Data.Model.Image
import media.project.archives.Presentation.Components.DrawImageButton
import media.project.archives.Presentation.Components.customButton
import media.project.archives.Presentation.Components.topSection
import media.project.archives.ui.theme.ArchivesTheme


@Composable
fun DrawImageViewerScreen(
    navController: NavHostController,
    imageViewerViewModel: ImageViewerViewModel,
    url: String,
    title: String,
    archived: Boolean,
    downloadUri: String,
    islocal: Boolean
){
    ArchivesTheme {
        Scaffold(topBar = {
            topSection(navController = navController)
        }) {paddingvalues ->

            val image = Image()
            image.setTit(title)
            image.setUl(url)
            image.setIsArch(archived)
            image.setDownloadUr(downloadUri)
            image.setIsLoc(islocal)

            midSectionImageViewer(navController = navController,
                paddingValues = paddingvalues,
                image,
                imageViewerViewModel)
        }
    }

}



@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun midSectionImageViewer(navController: NavController,
                          paddingValues: PaddingValues,
                          image : Image,
                          imageViewerViewModel: ImageViewerViewModel){
    val context = LocalContext.current
    val coroutine = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        imageViewerViewModel.initialize(context)
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

        GlideImage(
            model = if (image.isLocal) image.url else image.downloaduri,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.9f),
            contentScale = ContentScale.Fit,
            contentDescription = "Viewed image")

        DrawImageButton(image = image, modifier = Modifier.weight(0.1f), imageViewerViewModel = imageViewerViewModel)


    }


}

