package media.project.archives.Data.Model

import media.project.archives.Constants.typeImage
import media.project.archives.Domain.Model.Item

data class Image(val type : String = typeImage) : Item(){
    override fun equals(other: Any?): Boolean {
        val otheritem = other as Item

        return  otheritem.url == this.url

    }

}
