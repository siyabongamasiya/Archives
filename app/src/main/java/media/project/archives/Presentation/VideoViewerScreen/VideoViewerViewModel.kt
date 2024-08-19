package media.project.archives.Presentation.VideoViewerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.LocalFilesRepoImpl.LocalFilesRepoImpl
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Video
import media.project.archives.Data.RemoteFilesRepoImpl.RemoteFilesRepoImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus
import java.util.Collections

class VideoViewerViewModel : ViewModel() {
    val remoteFileRepo = RemoteFilesRepoImpl()
    val localFilesRepoImpl = LocalFilesRepoImpl()

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
        viewModelScope.launch {
            val flow = remoteFileRepo.DownloadFile(item,context,uri)
            flow.collect{isSuccessful ->

            }
        }
    }
}