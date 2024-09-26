package media.project.archives.Data.RepositoryImpl

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.imageDownloadDone
import media.project.archives.Constants.savedSuccessfully
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item
import media.project.archives.Domain.Repository.Repository
import media.project.archives.Utils.DownLoadFile
import media.project.archives.Utils.EventBus
import media.project.archives.Utils.SaveImage
import media.project.archives.Utils.createNewImage
import media.project.archives.Utils.getArchivedImage
import media.project.archives.Utils.getListOfAudios
import media.project.archives.Utils.getListOfImages
import media.project.archives.Utils.getListOfVideos
import javax.inject.Inject

class RepositoryImpl @Inject constructor() : Repository {
    override suspend fun getImages(context: Context) : List<Image> {
        return context.contentResolver.getListOfImages()
    }

    override suspend fun getVideos(context: Context) : List<Video> {
        return context.contentResolver.getListOfVideos()
    }

    override suspend fun getAudio(context: Context) : List<Song> {
        return context.contentResolver.getListOfAudios()
    }


    override suspend fun saveImage(image: Image) {
        FirebaseStorage.getInstance().SaveImage(image,{ scope ->
            scope.launch {
                EventBus.sendStatus(errorOccured)
            }
        }){scope ->
            scope.launch {
                EventBus.sendStatus(savedSuccessfully)
            }
        }
    }


    override suspend fun getArchivedImages() = callbackFlow {
        FirebaseStorage.getInstance().getArchivedImage(this)
        awaitClose {
            val coroutineScope = CoroutineScope(Dispatchers.Default)
            coroutineScope.launch {
                EventBus.sendStatus(imageDownloadDone)
            }
        }
    }


    override suspend fun DownloadFile(item: Item, context: Context, uri: Uri) = callbackFlow{
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