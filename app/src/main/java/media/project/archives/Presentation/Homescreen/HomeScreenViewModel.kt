package media.project.archives.Presentation.Homescreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import media.project.archives.Constants.audioDownloadDone
import media.project.archives.Constants.directory
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.imageDownloadDone
import media.project.archives.Constants.videoDownloadDone
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Data.RepositoryImpl.RepositoryImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus
import media.project.archives.Utils.createDir
import media.project.archives.Utils.createUniqueList
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(val repositoryImpl: RepositoryImpl): ViewModel() {

    var _refreshedImages = MutableStateFlow(false)
    val refreshImages = _refreshedImages.asStateFlow()

    var _refreshedVideos = MutableStateFlow(false)
    val refreshVideos = _refreshedVideos.asStateFlow()

    var _refreshedAudios = MutableStateFlow(false)
    val refreshAudios = _refreshedAudios.asStateFlow()

    private var _images = MutableStateFlow<List<Image>>(emptyList())
    val images = _images.asStateFlow()

    private var _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos = _videos.asStateFlow()

    private var _audios = MutableStateFlow<List<Song>>(emptyList())
    val audios = _audios.asStateFlow()

    private var _mixedItem = MutableStateFlow<List<Item>>(mutableListOf())
    val mixedItem = _mixedItem.asStateFlow()

    private var archivedImages = mutableListOf<Image>()
    private var archivedVideos = mutableListOf<Video>()
    private var archivedAudios = mutableListOf<Song>()
    private var combinedArchivedlist = mutableListOf<Item>()


    init {
        collectDownloadState()
        createDir()
    }




    private fun UpdateImagesUi(){
        _refreshedImages.value = true
    }

    private fun UpdateVideosUi(){
        _refreshedVideos.value = true
    }

    private fun UpdateAudiosUi(){
        _refreshedAudios.value = true
    }

    fun MapItems(itemsList: List<Item>) : List<Item>{
        var finalList = mutableListOf<Item>()

        itemsList.forEach { item ->
            if (isArchived(item)){
                item.setIsArch(true)
                finalList.add(item)
            }else{
                finalList.add(item)
            }
        }

        return finalList
    }


    private fun isArchived(unArcheditem: Item) : Boolean{
        var isArchived = false
        try {
            if (unArcheditem is Image) {
                archivedImages.forEach { archeditem ->
                    if (unArcheditem.url.equals(archeditem.url)) {
                        isArchived = true
                    }
                }
            } else if (unArcheditem is Video) {
                archivedVideos.forEach { archeditem ->
                    if (unArcheditem.url.equals(archeditem.url)) {
                        isArchived = true
                    }
                }
            } else {
                archivedAudios.forEach { archeditem ->
                    if (unArcheditem.url.equals(archeditem.url)) {
                        isArchived = true
                    }
                }
            }
        }catch (e : Exception){
            viewModelScope.launch{
                EventBus.sendStatus(errorOccured)
            }
        }

        return isArchived
    }



    private fun collectDownloadState(){
        val coroutine = CoroutineScope(Dispatchers.Default)

        coroutine.launch {
            EventBus.saved.collect{ result ->
                if (result == imageDownloadDone){
                    UpdateImagesUi()
                }else if (result == videoDownloadDone){
                    UpdateVideosUi()
                }else if (result == audioDownloadDone){
                    UpdateAudiosUi()
                }
            }
        }
    }




    suspend fun getImages(context: Context){
        viewModelScope.launch {
            try {
                _images.value = repositoryImpl.getImages(context)
                getArchivedImages()

            }catch (exception : Exception){
                Log.d("getImages", "getImages: ${exception.message}")
            }
        }
    }

    suspend fun getVideos(context: Context){
        viewModelScope.launch {
            try {
                _videos.value = repositoryImpl.getVideos(context)
                _refreshedVideos.value = true
                //getArchivedVideos()
            }catch (exception : Exception){
                Log.d("getvideos", "getvideos: ${exception.message}")
            }
        }
    }

    suspend fun getAudios(context: Context){
        viewModelScope.launch {
            try {
                _audios.value =  repositoryImpl.getAudio(context)
                _refreshedAudios.value = true
                //getArchivedAudios()
            }catch (exception : Exception){
                Log.d("getaudios", "getaudios: ${exception.message}")
            }
        }
    }

    private suspend fun getArchivedImages() {
        archivedImages.clear()
        val flow = repositoryImpl.getArchivedImages()
        viewModelScope.launch {
            flow.collect { image ->
                if (isLocal(image)) image.setIsLoc(true) else image.setIsLoc(false)
                archivedImages.add(image)
                //combine images
                combinedArchivedlist = combinedArchivedlist.plus(archivedImages).toMutableList()
                _mixedItem.value = createUniqueList(combinedArchivedlist)
            }
        }
    }
//    private suspend fun getArchivedVideos() {
//        archivedVideos.clear()
//        val flow = remoteFilesRepoImpl.getArchivedVideos()
//        viewModelScope.launch {
//            flow.collect{video ->
//                if (isLocal(video)) video.setIsLoc(true) else video.setIsLoc(false)
//                archivedVideos.add(video)
//                combinedArchivedlist = combinedArchivedlist.plus(archivedVideos).toMutableList()
//                _mixedItem.value = createUniqueList(combinedArchivedlist)
//            }
//        }
//
//    }
//    private suspend fun getArchivedAudios(){
//        archivedAudios.clear()
//        val flow = remoteFilesRepoImpl.getArchivedAudios()
//        viewModelScope.launch {
//            flow.collect{audio ->
//                if (isLocal(audio)) audio.setIsLoc(true) else audio.setIsLoc(false)
//                archivedAudios.add(audio)
//                combinedArchivedlist = combinedArchivedlist.plus(archivedAudios).toMutableList()
//                _mixedItem.value = createUniqueList(combinedArchivedlist)
//            }
//        }
//
//    }

    suspend fun getArchivedItems(context: Context){
        combinedArchivedlist.clear()
        getImages(context)
        getVideos(context)
        getAudios(context)

        getArchivedImages()
//        getArchivedVideos()
//        getArchivedAudios()
    }

    private fun isLocal(item : Item) : Boolean{
        if (item is Image){
            return _images.value.contains(item)
        }else if (item is Video){
            return _videos.value.contains(item)
        }else if (item is Song){
            return _audios.value.contains(item)
        }else{
            return false
        }
    }


}