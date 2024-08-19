package media.project.archives.Utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Audio
import com.google.firebase.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.launch
import media.project.archives.Constants.AudiosReferencePath
import media.project.archives.Constants.ImagesReferencePath
import media.project.archives.Constants.VideosReferencePath
import media.project.archives.Constants.errorOccured
import media.project.archives.Constants.imageDownloadDone
import media.project.archives.Constants.inProgress
import media.project.archives.Constants.storageRootRef
import media.project.archives.Constants.typeImage
import media.project.archives.Constants.typeNull
import media.project.archives.Constants.typeVideo
import media.project.archives.Data.Model.Image
import media.project.archives.Data.Model.Song
import media.project.archives.Data.Model.Video
import media.project.archives.Domain.Model.Item
import java.io.File
import java.net.URLDecoder

fun getArt(id : String) : Uri{
    val uriContent = Uri.parse("content://media/external/audio/albumart")
    val imageUrl = ContentUris.withAppendedId(uriContent,id.toLong())
    return imageUrl
}



fun checkType(items : List<Any>) : String{
    if (items.isNotEmpty()){
        val firstValue = items[0]

        if (firstValue is Image){
            return typeImage
        }else if (firstValue is Video){
            return typeVideo
        }else if (firstValue is Audio){
            return typeImage
        }
    }
    return  typeNull
}

fun DecodeUrl(string: String) : String{
    return URLDecoder.decode(string, "UTF-8")
}



fun createNewImage(uri: Uri) : Image{
    val title = DecodeUrl(uri.toString()).substringAfterLast("/")
    val url = "${Environment.getExternalStorageDirectory()}/${DecodeUrl(uri.toString()).substringAfterLast(":")}"

    val image = Image()
    image.setTit(title)
    image.setUl(url)
    image.setIsLoc(true)

    return image
}

fun createNewVideo(uri: Uri) : Video{
    val title = DecodeUrl(uri.toString()).substringAfterLast("/")
    val url = "${Environment.getExternalStorageDirectory()}/${DecodeUrl(uri.toString()).substringAfterLast(":")}"

    val video = Video()
    video.setTit(title)
    video.setUl(url)
    video.setIsLoc(true)

    return video
}

fun createNewAudio(uri: Uri) : Song{
    val title = DecodeUrl(uri.toString()).substringAfterLast("/")
    val url = "${Environment.getExternalStorageDirectory()}/${DecodeUrl(uri.toString()).substringAfterLast(":")}"

    val song = Song()
    song.setTit(title)
    song.setUl(url)
    song.setIsLoc(true)

    return song
}


fun FirebaseStorage.SaveImage(image: Image,
                              onfail: (coroutine : CoroutineScope) -> Unit,
                              onsuccess: (coroutine : CoroutineScope) -> Unit){
    //report to ui that we are sending file
    val coroutine = CoroutineScope(Dispatchers.Default)
    coroutine.launch {
        EventBus.sendSavingStatus(inProgress)
    }
    //create storage reference
    val storageRef = this.reference
    //create image reference
    val imageRef =
        storageRef.child("$ImagesReferencePath/${image.title}/${image.getFormattedUrl()}")
    //send file and wait for task
    val task = imageRef.putFile(Uri.fromFile(File(image.url)))

    //use task to receive results and use event bus to report to ui
    task.addOnSuccessListener {
        onsuccess.invoke(coroutine)
    }.addOnFailureListener {
        onfail.invoke(coroutine)
    }
}

fun FirebaseStorage.getArchivedImage(producerScope: ProducerScope<Image>){
    var prefixList: List<StorageReference>
    val storageRef = this.reference

    val imagesRef = storageRef.child(ImagesReferencePath)

    val imagetask = imagesRef.listAll()

    var count = 0

    imagetask.addOnSuccessListener { listresults ->

        if (listresults.prefixes.size > 0) {
            prefixList = listresults.prefixes
            prefixList.forEach { prefix ->
                val image = Image()
                val imagepath = DecodeUrl(prefix.toString().substringAfter(storageRootRef))


                val imageRef = storageRef.child(imagepath)
                val task = imageRef.listAll()

                task.addOnSuccessListener { listresults2 ->
                    val items = listresults2.items

                    val imagefullpath = DecodeUrl(items.toString().substringAfter(storageRootRef).removeSuffix("]"))


                    items[0].downloadUrl.addOnSuccessListener { uri ->

                        val imageProperties: List<String> = imagefullpath.split("/")

                        image.setTit(imageProperties[1])
                        image.setToNormalUrl(imageProperties[2])
                        image.setStoageRefence(items[0])
                        image.setIsArch(true)
                        image.setDownloadUr(uri.toString())

                        producerScope.trySend(image)

                        count++
                        if(count == prefixList.size){
                            val coroutineScope = CoroutineScope(Dispatchers.Default)

                            coroutineScope.launch {
                                EventBus.sendSavingStatus(imageDownloadDone)
                            }
                        }
                    }

                }
            }
        } else {

        }
    }
}

fun FirebaseStorage.DownLoadFile(item: Item, context: Context,
                                 uri: Uri ,
                                 producerScope: ProducerScope<Boolean>,
                                 onUpdate : (coroutine : CoroutineScope) -> Unit){
    val coroutine = CoroutineScope(Dispatchers.Default)

    try {
        val storageReference = FirebaseStorage.getInstance().reference
        val downloadRef  = if(item is Image){
            storageReference.child(ImagesReferencePath).child(item.title).child(item.getFormattedUrl())
        }else if (item is Video){
            storageReference.child(VideosReferencePath).child(item.title).child(item.getFormattedUrl())
        }else if (item is Song){
            storageReference.child(AudiosReferencePath).child(item.EncodeTADI()).child(item.getFormattedUrl())
        }else{
            FirebaseStorage.getInstance().reference
        }



        val SIZE =1024 * 1024L
        downloadRef.getBytes(SIZE).addOnSuccessListener {array ->
            coroutine.launch {
                val outputstream = context.contentResolver.openOutputStream(uri)
                outputstream?.write(array)
                outputstream?.flush()
                outputstream?.close()

                producerScope.trySend(true)
                //delete old one
                downloadRef.delete()
                //update item
                if (item is Image) {
                    onUpdate.invoke(coroutine)
                }
            }
        }

    }catch (exception : Exception){
        coroutine.launch {
            EventBus.sendSavingStatus(errorOccured)
        }
    }
}

fun ContentResolver.getListOfImages() : List<Image>{
    var listofimages = mutableListOf<Image>()

    //create projection of columns
    val projection = arrayOf(MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.TITLE)
    //create query using contentresolver
    val query = this.query(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null
    )
    //receive data from query and update _images
    query?.use { cursor ->


        while (cursor.moveToNext()){
            val urlIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val titleIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)
            val url = cursor.getString(urlIndex)
            val title = cursor.getString(titleIndex)

            val image = Image()
            image.setUl(url)
            image.setTit(title)
            listofimages.add(image)
        }
    }

    return listofimages
}

fun ContentResolver.getListOfVideos() : List<Video>{
    var listofvideos = mutableListOf<Video>()
    //create projection of columns
    val projection = arrayOf(MediaStore.Video.VideoColumns.DATA,MediaStore.Video.VideoColumns.TITLE)
    //create query using contentresolver
    val query = this.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null
    )
    //receive data from query and update _videos
    query?.use { cursor ->


        while (cursor.moveToNext()){
            val urlIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
            val titleIndex = cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE)
            val url = cursor.getString(urlIndex)
            val title = cursor.getString(titleIndex)

            val video = Video()
            video.setUl(url)
            video.setTit(title)
            listofvideos.add(video)
        }
    }

    return listofvideos
}

fun ContentResolver.getListOfAudios() : List<Song>{
    var listofaudios = mutableListOf<Song>()

    //create projection of columns
    val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.TITLE,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.ALBUM_ID)
    //create query using contentresolver
    val query = this.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        projection,
        null,
        null
    )
    //receive data from query and update _audios
    query?.use { cursor ->


        while (cursor.moveToNext()){
            val urlIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA)
            val artistIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
            val titleIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
            val durationIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
            val IdIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)

            val url = cursor.getString(urlIndex)
            val artist = cursor.getString(artistIndex)
            val title = cursor.getString(titleIndex)
            val duration = cursor.getString(durationIndex)
            val albumID = cursor.getString(IdIndex)

            val audio = Song(artist = artist, duration = duration, id = albumID)
            audio.setTit(title)
            audio.setUl(url)
            listofaudios.add(audio)
        }
    }

    return listofaudios
}




















//    override suspend fun getArchivedVideos() = callbackFlow {
//        var prefixList: List<StorageReference>
//        val storageRef = FirebaseStorage.getInstance().reference
//
//        val videoRef = storageRef.child(VideosReferencePath)
//        val videotask = videoRef.listAll()
//        var count = 0
//
//        videotask.addOnSuccessListener { listresults ->
//
//            if (listresults.prefixes.size > 0) {
//                prefixList = listresults.prefixes
//                prefixList.forEach { prefix ->
//                    val video = Video()
//                    val videopath = prefix.toString().substringAfter(storageRootRef)
//
//                    val videoRef = storageRef.child(videopath)
//                    val task = videoRef.listAll()
//
//                    task.addOnSuccessListener { listresults2 ->
//                        val items = listresults2.items
//
//                        val videofullpath = DecodeUrl(items.toString().substringAfter(storageRootRef).removeSuffix("]"))
//
//                        items[0].downloadUrl.addOnSuccessListener { uri ->
//                            val videoProperties: List<String> = videofullpath.split("/")
//
//                            video.setTit(videoProperties[1])
//                            video.setToNormalUrl(videoProperties[2])
//                            video.setStoageRefence(items[0])
//                            video.setIsArch(true)
//                            video.setDownloadUr(uri.toString())
//
//
//                            trySend(video)
//
//                            count++
//                            if(count == prefixList.size){
//                                val coroutineScope = CoroutineScope(Dispatchers.Default)
//                                coroutineScope.launch {
//                                    EventBus.sendSavingStatus(videoDownloadDone)
//                                }
//                            }
//                        }
//
//                    }
//                }
//
//            } else {
//
//            }
//        }
//
//        awaitClose {
//
//        }
//    }
//
//    override suspend fun getArchivedAudios() = callbackFlow {
//        val storageRef = FirebaseStorage.getInstance().reference
//
//        val audiosRef = storageRef.child(AudiosReferencePath)
//
//        val audiotask = audiosRef.listAll()
//
//        var prefixList: List<StorageReference>
//
//        var count = 0
//
//
//        audiotask.addOnSuccessListener { listresults ->
//
//            if (listresults.prefixes.size > 0) {
//                prefixList = listresults.prefixes
//                prefixList.forEach { prefix ->
//                    val audio = Song()
//                    val audiopath = DecodeUrl(prefix.toString().substringAfter(storageRootRef))
//
//                    val audioRef = storageRef.child(audiopath)
//                    val task = audioRef.listAll()
//
//                    task.addOnSuccessListener { listresults2 ->
//                        val items = listresults2.items
//                        val audiofullpath = DecodeUrl(items.toString().substringAfter(storageRootRef).removeSuffix("]"))
//
//                        items[0].downloadUrl.addOnSuccessListener { uri ->
//                            val audioProperties: List<String> = audiofullpath.split("/")
//
//                            audio.DecodeTADI(audioProperties[1])
//                            audio.setToNormalUrl(audioProperties[2])
//                            audio.setStoageRefence(items[0])
//                            audio.setIsArch(true)
//                            audio.setDownloadUr(uri.toString())
//
//                            trySend(audio)
//                            count++
//
//                            if(count == prefixList.size){
//                                val coroutineScope = CoroutineScope(Dispatchers.Default)
//                                coroutineScope.launch {
//                                    EventBus.sendSavingStatus(audioDownloadDone)
//                                }
//                            }
//                        }
//
//                    }
//                }
//
//            } else {
//                //UpdateAudiosUi()
//            }
//        }
//
//        awaitClose {
//
//        }
//    }


//    override suspend fun saveVideo(video: Video) {
//        //report to ui that we are sending file
//        val coroutine = CoroutineScope(Dispatchers.Default)
//        coroutine.launch {
//            EventBus.sendSavingStatus(inProgress)
//        }
//
//        //create storage reference
//        val storageRef = FirebaseStorage.getInstance().reference
//        //create image reference
//        val videoRef =
//            storageRef.child("$VideosReferencePath/${video.title}/${video.getFormattedUrl()}")
//        //send file and wait for task
//        val task = videoRef.putFile(Uri.fromFile(File(video.url)))
//
//        //use task to receive results and use event bus to report to ui
//        task.addOnSuccessListener {
//            coroutine.launch {
//                EventBus.sendSavingStatus(savedSuccessfully)
//            }
//
//        }.addOnFailureListener {
//            coroutine.launch {
//                EventBus.sendSavingStatus(errorOccured)
//            }
//
//        }
//    }
//
//    override suspend fun saveAudio(audio: Song) {
//        //report to ui that we are sending file
//        val coroutine = CoroutineScope(Dispatchers.Default)
//        coroutine.launch {
//            EventBus.sendSavingStatus(inProgress)
//        }
//        //create storage reference
//        val storageRef = FirebaseStorage.getInstance().reference
//        //create image reference
//        val audioRef =
//            storageRef.child("$AudiosReferencePath/${audio.EncodeTADI()}/${audio.getFormattedUrl()}")
//        //send file and wait for task
//        val task = audioRef.putFile(Uri.fromFile(File(audio.url)))
//
//        //use task to receive results and use event bus to report to ui
//        task.addOnSuccessListener {
//            coroutine.launch {
//                EventBus.sendSavingStatus(savedSuccessfully)
//            }
//
//        }.addOnFailureListener {
//            coroutine.launch {
//                EventBus.sendSavingStatus(errorOccured)
//            }
//
//        }
//    }

