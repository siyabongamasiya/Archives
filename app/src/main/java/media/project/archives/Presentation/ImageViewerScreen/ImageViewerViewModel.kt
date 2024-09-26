package media.project.archives.Presentation.ImageViewerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Data.RepositoryImpl.RepositoryImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus
import javax.inject.Inject

@HiltViewModel
class ImageViewerViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    private var archivedImages = mutableListOf<Image>()


    fun initialize(context : Context){
        try {
            viewModelScope.launch {
                getArchivedImages(context)
            }
        }catch (e : Exception){
            viewModelScope.launch{
                EventBus.sendStatus(errorOccured)
            }
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
        try {
            archivedImages.add(image)
            repositoryImpl.saveImage(image)
        }catch (e : Exception){
            viewModelScope.launch{
                EventBus.sendStatus(errorOccured)
            }
        }
    }

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