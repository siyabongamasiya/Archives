package media.project.archives.Data.RemoteFilesRepoImpl

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import media.project.archives.Constants.AudiosReferencePath
import media.project.archives.Constants.ImagesReferencePath
import media.project.archives.Constants.VideosReferencePath
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.imageDownloadDone
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Constants.storageRootRef
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item
import media.project.archives.Domain.RemoteFilesRepo.RemoteFileRepo
import media.project.archives.Utils.DecodeUrl
import media.project.archives.Utils.DownLoadFile
import media.project.archives.Utils.EventBus
import media.project.archives.Utils.SaveImage
import media.project.archives.Utils.createNewImage
import media.project.archives.Utils.getArchivedImage


class RemoteFilesRepoImpl : RemoteFileRepo {
    override suspend fun saveImage(image: Image) {
        FirebaseStorage.getInstance().SaveImage(image,{scope ->
            scope.launch {
                EventBus.sendSavingStatus(errorOccured)
            }
        }){scope ->
            scope.launch {
                EventBus.sendSavingStatus(savedSuccessfully)
            }
        }
    }


    override suspend fun getArchivedImages() = callbackFlow {
        FirebaseStorage.getInstance().getArchivedImage(this)
        awaitClose {
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                EventBus.sendSavingStatus(imageDownloadDone)
            }
        }
    }


    override suspend fun DownloadFile(item: Item,context: Context,uri: Uri) = callbackFlow{
        FirebaseStorage.getInstance().DownLoadFile(item,context,uri,this){coroutine ->
            coroutine.launch {
                PostNewItem(createNewImage(uri))
            }
        }

        awaitClose{

        }
    }

    private suspend fun PostNewItem(item: Item){
        if (item is Image){
            saveImage(item)
        }else if (item is Video){
            //saveVideo(item)
        }else if( item is Song){
            //saveAudio(item)
        }
    }

}
