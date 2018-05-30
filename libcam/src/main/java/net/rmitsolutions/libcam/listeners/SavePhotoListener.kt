package net.rmitsolutions.libcam.listeners

import android.app.Activity
import net.rmitsolutions.libcam.PrivateInformationObject

interface SavePhotoListener {

    fun onPhotoSaved(data : PrivateInformationObject)
}