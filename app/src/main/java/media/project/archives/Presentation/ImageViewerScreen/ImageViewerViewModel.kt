package media.project.archives.Presentation.ImageViewerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.LocalFilesRepoImpl.LocalFilesRepoImpl
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Data.RemoteFilesRepoImpl.RemoteFilesRepoImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus

class ImageViewerViewModel : ViewModel() {

    val localFilesRepoImpl = LocalFilesRepoImpl()
    val remoteFileRepo = RemoteFilesRepoImpl()

    private var archivedImages = mutableListOf<Image>()


    fun initialize(context : Context){
        viewModelScope.launch {
            getArchivedImages(context)
        }
    }

    private suspend fun getArchivedImages(context: Context) {
//        archivedImages.clear()
//        val archImgs = remoteFileRepo.getArchivedImages()
//        archImgs.forEach {
//            archivedImages.add(it)
//        }
    }

    suspend fun saveImageArchives(image: Image){
        archivedImages.add(image)
        remoteFileRepo.saveImage(image)
    }

    suspend fun DownloadFile(item : Item,context: Context,uri: Uri){
        viewModelScope.launch {
            val flow = remoteFileRepo.DownloadFile(item,context,uri)
            flow.collect{isSuccessful ->

            }
        }
    }




}