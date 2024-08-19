package media.project.archives.Domain.RemoteFilesRepo

import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Audio
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.Flow
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item

interface RemoteFileRepo {

    suspend fun saveImage(image : Image)
//    suspend fun saveVideo(video : Video)
//    suspend fun saveAudio(audio : Song)

    suspend fun getArchivedImages() : Flow<Image>
//    suspend fun getArchivedVideos() : Flow<Video>
//    suspend fun getArchivedAudios() : Flow<Song>

    suspend fun DownloadFile(item : Item,context: Context,uri: Uri) : Flow<Boolean>
}