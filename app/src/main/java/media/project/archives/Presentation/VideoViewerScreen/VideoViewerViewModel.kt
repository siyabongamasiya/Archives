package media.project.archives.Presentation.VideoViewerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Video
import media.project.archives.Data.RepositoryImpl.RepositoryImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class VideoViewerViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    private var archivedVideos = mutableListOf<Video>()

    fun initialize(context : Context){
        viewModelScope.launch {
            getArchivedVideos(context)
        }
    }
    private suspend fun getArchivedVideos(context: Context) {
//        archivedVideos.clear()
//        val archVids = remoteFileRepo.getArchivedVideos()
//        archVids.forEach {
//            archivedVideos.add(it)
//        }
    }

//    suspend fun saveVideoArchives(video : Video){
//        archivedVideos.add(video)
//        remoteFileRepo.saveVideo(video)
//    }

    suspend fun DownloadFile(item : Item,context: Context,uri: Uri){
        try {
            viewModelScope.launch {
                val flow = repositoryImpl.DownloadFile(item, context, uri)
                flow.collect { isSuccessful ->

                }
            }
        }catch (e : Exception){
            viewModelScope.launch{
                EventBus.sendStatus(errorOccured)
            }
        }
    }
}