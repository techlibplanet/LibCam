package net.rmitsolutions.libcam.callback

import net.rmitsolutions.libcam.Constants.logD
import net.rmitsolutions.libcam.PrivateInformation
import net.rmitsolutions.libcam.listeners.SavePhotoListener

class SavePhotoCallback  {


    var listener : SavePhotoListener? = null

    fun setSavePhotoListener(savePhotoListener: SavePhotoListener){
        listener = savePhotoListener
    }

    fun onSavePhoto(mCurrentPhotoPath: String) {
        if (listener!=null){
            val privateInformation = PrivateInformation()
            val data =privateInformation.getImageInformation(mCurrentPhotoPath)
            listener?.onPhotoSaved(data!!)
        }
    }

}