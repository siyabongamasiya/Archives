package media.project.archives.Utils

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow

object EventBus {
    private var _saved = MutableSharedFlow<String>()
    val saved = _saved.asSharedFlow()

    suspend fun sendSavingStatus(status : String){
        _saved.emit(status)
    }
}