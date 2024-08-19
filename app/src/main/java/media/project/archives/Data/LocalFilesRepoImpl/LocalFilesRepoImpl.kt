package media.project.archives.Data.LocalFilesRepoImpl

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import media.project.archives.Constants.audiosPreference
import media.project.archives.Constants.imagesPreference
import media.project.archives.Constants.prefernceDatabaseName
import media.project.archives.Constants.typeImage
import media.project.archives.Constants.typeVideo
import media.project.archives.Constants.videosPreference
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.LocalFilesRepo.LocalFilesRepo
import media.project.archives.Presentation.MainActivity
import media.project.archives.Utils.getListOfAudios
import media.project.archives.Utils.getListOfImages
import media.project.archives.Utils.getListOfVideos
import java.io.FileOutputStream

class LocalFilesRepoImpl : LocalFilesRepo {
    override suspend fun getImages(context: Context) : List<Image> {
        return context.contentResolver.getListOfImages()
    }

    override suspend fun getVideos(context: Context) : List<Video> {
        return context.contentResolver.getListOfVideos()
    }

    override suspend fun getAudio(context: Context) : List<Song> {
        return context.contentResolver.getListOfAudios()
    }

}