package media.project.archives.Domain.Model

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

open class Item(var title : String = "",
                var url : String = "",
                var isArchived : Boolean = false,
                var downloaduri : String = "",
                var storageReference: StorageReference = FirebaseStorage.getInstance().reference,
                var isLocal : Boolean = true) {


    fun setTit(tit: String){
        this.title = tit
    }

    fun setUl(ur: String){
        this.url = ur
    }

    fun setDownloadUr(downloadur: String){
        this.downloaduri = downloadur
    }

    fun setStoageRefence(storageRef: StorageReference){
        this.storageReference = storageRef
    }

    fun setIsLoc(isLoc: Boolean){
        this.isLocal = isLoc
    }

    fun setIsArch(isArch: Boolean){
        this.isArchived = isArch
    }

    fun getFormattedUrl() : String{
        return this.url.replace("/","&")
    }

    fun setToNormalUrl(remoteUrl : String){
        this.url = remoteUrl.replace("&","/")
    }
}