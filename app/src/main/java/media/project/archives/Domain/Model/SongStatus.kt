package media.project.archives.Domain.Model

data class SongStatus(
    var isPlaying : Boolean = false,
    var currentTimeMs : Long = 0,
    var totalTimeMs : Long = 0,
    var currentTime : String = "",
    var totalTime : String = ""
)
