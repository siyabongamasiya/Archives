package media.project.archives.AudioPlayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import media.project.archives.Data.Model.Song
import media.project.archives.Domain.Model.SongStatus

class AudioPlayer(context : Context, song: Song) {
    private val exoPlayer = ExoPlayer.Builder(context).build()
    private var _songState = MutableStateFlow(SongStatus(totalTimeMs = song.duration.toLong()))
    val songState = _songState.asStateFlow()

    init {
        val mediaitem = MediaItem.fromUri(song.url)
        val listener = object : Player.Listener{
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _songState.value =_songState.value.copy(
                    isPlaying = isPlaying
                )
            }
        }

        exoPlayer.setMediaItem(mediaitem)
        exoPlayer.addListener(listener)
        exoPlayer.repeatMode = ExoPlayer.REPEAT_MODE_ONE
        exoPlayer.prepare()
    }


    fun PlayPauseSong(){
        if(exoPlayer.isPlaying){
            exoPlayer.pause()
        }else{
            exoPlayer.play()
        }
    }

    fun ChangePosition(position : Long){
        exoPlayer.seekTo(position)
    }

    fun Dispose(){
        exoPlayer.release()
    }

    fun  getProgress(){
        _songState.value = _songState.value.copy(
            currentTimeMs = exoPlayer.currentPosition + 1000
        )
    }

    fun formatTotalTime() : String{
        //calculate total time
        val Ttotalsecs = _songState.value.totalTimeMs/1000
        val TMinutes = Ttotalsecs/60
        val Tsecs = Ttotalsecs%60

        //formating
        var Tminutesformatted = ""
        var Tsecsformatted  = ""

        //adding zeros if less than 9
        if (TMinutes <= 9){
            Tminutesformatted = "0${TMinutes}"
        }else{
            Tminutesformatted = "${TMinutes}"
        }

        if (Tsecs <= 9){
            Tsecsformatted = "0${Tsecs}"
        }else{
            Tsecsformatted = "${Tsecs}"
        }

        return "$Tminutesformatted : $Tsecsformatted"
    }

    fun formatCurrentTime() : String{
        //calculate current time
        val Ctotalsecs = _songState.value.currentTimeMs/1000
        val CMinutes = Ctotalsecs/60
        val Csecs = Ctotalsecs%60

        //formating
        var Cminutesformatted = ""
        var Csecsformatted  = ""

        //adding zeros if less than 9
        if (CMinutes <= 9){
            Cminutesformatted = "0${CMinutes}"
        }else{
            Cminutesformatted = "${CMinutes}"
        }

        if (Csecs <= 9){
            Csecsformatted = "0${Csecs}"
        }else{
            Csecsformatted = "${Csecs}"
        }

        return "$Cminutesformatted : $Csecsformatted"
    }
}