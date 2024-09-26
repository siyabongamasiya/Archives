package media.project.archives.Presentation.HomescreenArchived

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import media.project.archives.Presentation.Components.Item
import media.project.archives.Presentation.Components.ItemsList
import media.project.archives.Presentation.Homescreen.HomeScreenViewModel
import media.project.archives.Presentation.HomescreenImages.midSectionHomeScreenImages
import media.project.archives.Presentation.ScreenRoutes

@Composable
fun DrawHomeScreenArchived(navHostController: NavHostController){
    val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()

    Scaffold {paddingValues ->
        midSectionHomeScreenArchived(navController = navHostController,
            paddingValues = paddingValues,
            homeScreenViewModel)
    }
}

@Composable
fun midSectionHomeScreenArchived(navController: NavController,
                                 paddingValues: PaddingValues,
                                 homeScreenViewModel: HomeScreenViewModel
){

    val mixeditems = homeScreenViewModel.mixedItem.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        homeScreenViewModel.getArchivedItems(context)
    }


    ItemsList(items = mixeditems.value,
        paddingValues = paddingValues,
        navController = navController,
        homeScreenViewModel = homeScreenViewModel)
}