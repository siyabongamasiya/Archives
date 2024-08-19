package media.project.archives.Domain.LocalFilesRepo

import android.content.Context
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video

interface LocalFilesRepo {
    suspend fun getImages(context: Context) : List<Image>
    suspend fun getVideos(context: Context) : List<Video>
    suspend fun getAudio(context: Context) : List<Song>

}