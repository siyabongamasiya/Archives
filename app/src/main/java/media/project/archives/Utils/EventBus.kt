package media.project.archives.Utils

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private var _saved = MutableSharedFlow<String>()
    val saved = _saved.asSharedFlow()

    suspend fun sendStatus(status : String){
        _saved.emit(status)
    }
}