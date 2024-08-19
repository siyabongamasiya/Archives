package media.project.archives.Presentation.AudioPlayerScreen

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import media.project.archives.Data.LocalFilesRepoImpl.LocalFilesRepoImpl
import media.project.archives.Data.Model.Song
import media.project.archives.Data.RemoteFilesRepoImpl.RemoteFilesRepoImpl
import media.project.archives.AudioPlayer.AudioPlayer
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Domain.Model.Item
import media.project.archives.Utils.EventBus

class AudioPlayerViewModel : ViewModel() {
    val localFilesRepoImpl = LocalFilesRepoImpl()
    val remoteFileRepo = RemoteFilesRepoImpl()

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
        viewModelScope.launch {
            getArchivedAudios(context)
        }
    }
//    suspend fun saveAudioArchives(song: Song){
//        archivedAudios.add(song)
//        remoteFileRepo.saveAudio(song)
//    }

    suspend fun DownloadFile(item : Item,context: Context,uri: Uri){
        viewModelScope.launch {
            val flow = remoteFileRepo.DownloadFile(item,context,uri)
            flow.collect{isSuccessful ->

            }
        }
    }
}