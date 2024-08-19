package media.project.archives.Data.Model

import media.project.archives.Constants.typeAudio
import media.project.archives.Domain.Model.Item

data class Song(
    val type : String = typeAudio,
    var artist : String = "",
    var duration : String = "",
    var id : String = "") : Item(){

    override fun equals(other: Any?): Boolean {
        val otheritem = other as Item

        return  otheritem.url == this.url
    }
    fun EncodeTADI() : String{
        val stringbuilder = StringBuilder(super.title)
        stringbuilder.append("*${this.artist}")
        stringbuilder.append("*${this.duration}")
        stringbuilder.append("*${this.id}")

        return stringbuilder.toString()
    }

    fun DecodeTADI(encodedTADI : String){
        val properties = encodedTADI.split("*")
        super.title = properties[0]
        this.artist = properties[1]
        this.duration = properties[2]
        this.id = properties[3]
    }

    }
