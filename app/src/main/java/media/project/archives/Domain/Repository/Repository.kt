package media.project.archives.Domain.Repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.flow.Flow
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item

interface Repository {
    suspend fun getImages(context: Context) : List<Image>
    suspend fun getVideos(context: Context) : List<Video>
    suspend fun getAudio(context: Context) : List<Song>

    suspend fun saveImage(image : Image)

    suspend fun getArchivedImages() : Flow<Image>

    suspend fun DownloadFile(item : Item, context: Context, uri: Uri) : Flow<Boolean>
}