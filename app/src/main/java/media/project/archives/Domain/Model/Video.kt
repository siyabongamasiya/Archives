package media.project.archives.Data.Model

import media.project.archives.Constants.typeVideo
import media.project.archives.Domain.Model.Item

data class Video(val type : String = typeVideo) : Item(){
    override fun equals(other: Any?): Boolean {
        val otheritem = other as Item

        return  otheritem.url == this.url
    }
}
