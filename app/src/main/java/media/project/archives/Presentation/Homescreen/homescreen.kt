package media.project.archives.Presentation.Homescreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import media.project.archives.Presentation.HomescreenArchived.DrawHomeScreenArchived
import media.project.archives.Presentation.HomescreenAudio.DrawHomeScreenAudios
import media.project.archives.Presentation.HomescreenImages.DrawHomeScreenImages
import media.project.archives.Presentation.HomescreenVideos.DrawHomeScreenVideos
import media.project.archives.ui.theme.ArchivesTheme

@Composable
fun DrawHomeScreen(navHostController: NavHostController){
    val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()

    ArchivesTheme {
        Scaffold {paddingvalues ->
            midSectionHomescreen(navHostController = navHostController,
                paddingValues = paddingvalues,homeScreenViewModel)
        }
    }
}

@Composable
fun midSectionHomescreen(navHostController: NavHostController,
                         paddingValues: PaddingValues,
                         homeScreenViewModel: HomeScreenViewModel){

    var pagerState = rememberPagerState {
        4
    }

    val coroutine = rememberCoroutineScope()

    Column (modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.primary)
        .padding(top = paddingValues.calculateTopPadding())){

        ScrollableTabRow(
            modifier = Modifier
                .fillMaxWidth(),
            selectedTabIndex = pagerState.currentPage,
            indicator = {
                if(pagerState.currentPage < pagerState.pageCount){
                    TabRowDefaults.Indicator(modifier = Modifier.tabIndicatorOffset(it[pagerState.currentPage]),
                        color = MaterialTheme.colorScheme.tertiary)
                }
            }) {

            Tabitem(text = "Images", isSelected = pagerState.currentPage == 0) {
                coroutine.launch {
                    pagerState.scrollToPage(0)
                }

            }

            Tabitem(text = "Videos", isSelected = pagerState.currentPage == 1) {
                coroutine.launch {
                    pagerState.scrollToPage(1)
                }
            }

            Tabitem(text = "Audio", isSelected = pagerState.currentPage == 2) {
                coroutine.launch {
                    pagerState.scrollToPage(2)
                }
            }

            Tabitem(text = "Archived Images", isSelected = pagerState.currentPage == 3) {
                coroutine.launch {
                    pagerState.scrollToPage(3)
                }
            }

        }

        HorizontalPager(state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)) {index ->
            when(index){
                0 -> {
                    DrawHomeScreenImages(navHostController)
                }

                1 -> {
                    DrawHomeScreenVideos(navHostController)
                }

                2 -> {
                    DrawHomeScreenAudios(navHostController)
                }

                3 -> {
                    DrawHomeScreenArchived(navHostController = navHostController)
                }
            }
        }
    }
}



@Composable
fun Tabitem(text : String,isSelected : Boolean,onclick : () -> Unit){

    Tab(
        selected = isSelected,
        modifier = Modifier.padding(5.dp),
        onClick = {
                  onclick.invoke()
        },
    ) {

        val textcolor : Color

        if (isSelected){
            textcolor = MaterialTheme.colorScheme.tertiary
        }else{
            textcolor = MaterialTheme.colorScheme.onPrimary
        }

        Text(text = text,
            style = MaterialTheme.typography.titleMedium,
            color = textcolor,
            modifier = Modifier
                .padding(10.dp))
    }
}
