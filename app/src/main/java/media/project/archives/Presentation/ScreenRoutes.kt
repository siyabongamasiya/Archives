package media.project.archives.Presentation

import kotlinx.serialization.Serializable

sealed class ScreenRoutes{
    data class HomeScreen(val route : String = "HomeScreen") : ScreenRoutes()

    @Serializable
    data class HomescreenImages(
        val def : String): ScreenRoutes()

    @Serializable
    data class HomescreenVideos(
        val def : String): ScreenRoutes()

    @Serializable
    data class HomescreenAudio(
        val def : String): ScreenRoutes()

    @Serializable
    data class ImageViewer(
        val url : String,
        val title : String,
        val isArchived : Boolean,
        val downloadUri : String,
        val islocal : Boolean): ScreenRoutes()

    @Serializable
    data class VideoViewer(
        val url : String,
        val title : String,
        val isArchived : Boolean,
        val downloadUri : String,
        val islocal : Boolean): ScreenRoutes()

    @Serializable
    data class AudioPlayer(
        val title : String,
        val artist : String,
        val duration : String,
        val url : String,
        val id : String,
        val isArchived : Boolean,
        val downloadUri : String,
        val islocal : Boolean): ScreenRoutes()
}