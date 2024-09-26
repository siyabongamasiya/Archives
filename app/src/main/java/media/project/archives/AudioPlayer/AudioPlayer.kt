package media.project.archives.AudioPlayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import media.project.archives.Constants.errorOccured
import media.project.archives.Data.Model.Song
import media.project.archives.Domain.Model.SongStatus
import media.project.archives.Utils.EventBus
import java.util.concurrent.TimeUnit

class AudioPlayer(context : Context, song: Song) {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    private var _songState = MutableStateFlow(SongStatus(totalTimeMs = song.duration.toLong()))
    val songState = _songState.asStateFlow()

    init {
        try {
            val mediaitem = MediaItem.fromUri(song.url)
            val listener = object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _songState.value = _songState.value.copy(
                        isPlaying = isPlaying
                    )
                }
            }

            exoPlayer.setMediaItem(mediaitem)
            exoPlayer.addListener(listener)
            exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
            exoPlayer.prepare()
        }catch (e : Exception){
            val coroutine = CoroutineScope(Dispatchers.Default)
            coroutine.launch {
                EventBus.sendStatus(errorOccured)
            }
        }
    }


    fun PlayPauseSong(){
        try {
            if (exoPlayer.isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
        }catch (e : Exception){
            val coroutine = CoroutineScope(Dispatchers.Default)
            coroutine.launch {
                EventBus.sendStatus(errorOccured)
            }
        }
    }

    fun ChangePosition(position : Long){
        try {
            exoPlayer.seekTo(position)
        }catch (e : Exception){
            val coroutine = CoroutineScope(Dispatchers.Default)
            coroutine.launch {
                EventBus.sendStatus(errorOccured)
            }
        }
    }

    fun Dispose(){
        try {
            exoPlayer.release()
        }catch (e : Exception){
            val coroutine = CoroutineScope(Dispatchers.Default)
            coroutine.launch {
                EventBus.sendStatus(errorOccured)
            }
        }
    }

    fun  getProgress(){
        try {
            _songState.value = _songState.value.copy(
                currentTimeMs = exoPlayer.currentPosition + 1000
            )
        }catch (e : Exception){
            val coroutine = CoroutineScope(Dispatchers.Default)
            coroutine.launch {
                EventBus.sendStatus(errorOccured)
            }
        }
    }

    fun formatTotalTime() : String {
        return convertSecondsToHMmSs(exoPlayer.duration)
    }

    fun formatCurrentTime() : String {
        return convertSecondsToHMmSs(exoPlayer.currentPosition)
    }

    private fun convertSecondsToHMmSs(miliSeconds: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(miliSeconds).toInt() % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(miliSeconds).toInt() % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(miliSeconds).toInt() % 60
        return when {
            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, seconds)
            seconds > 0 -> String.format("00:%02d", seconds)
            else -> {
                "00:00"
            }
        }
    }
}