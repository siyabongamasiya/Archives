package media.project.archives.Presentation.AudioPlayerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import media.project.archives.Data.Model.Song
import media.project.archives.AudioPlayer.AudioPlayer
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.RepositoryImpl.RepositoryImpl
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus
import javax.inject.Inject

@HiltViewModel
class AudioPlayerViewModel @Inject constructor(val repositoryImpl: RepositoryImpl) : ViewModel() {

    private var archivedAudios = mutableListOf<Song>()

    private var _progress = MutableStateFlow(0L)
    val progress = _progress.asStateFlow()



    private suspend fun getArchivedAudios(context: Context) {
//        archivedAudios.clear()
//        val archImgs = remoteFileRepo.getArchivedAudios()
//        archImgs.forEach {
//            archivedAudios.add(it)
//        }
    }
    fun initialize(context : Context){
        try {
            viewModelScope.launch {
                getArchivedAudios(context)
            }
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