package net.rmitsolutions.libcamdemo

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import net.rmitsolutions.libcam.Constants.CROP_PHOTO
import net.rmitsolutions.libcam.Constants.DEFAULT_FILE_PREFIX
import net.rmitsolutions.libcam.Constants.SELECT_PHOTO
import net.rmitsolutions.libcam.Constants.TAKE_PHOTO
import net.rmitsolutions.libcam.Constants.globalBitmapUri
import net.rmitsolutions.libcam.Constants.logD
import net.rmitsolutions.libcam.Constants.mCurrentImageName
import net.rmitsolutions.libcam.Constants.mCurrentPhotoPath
import net.rmitsolutions.libcam.LibCam
import net.rmitsolutions.libcam.LibPermissions
import net.rmitsolutions.libcam.PrivateInformationObject
import net.rmitsolutions.libcam.callback.SavePhotoCallback
import net.rmitsolutions.libcam.listeners.SavePhotoListener
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(), SavePhotoListener {

    private lateinit var libPermissions: LibPermissions
    private lateinit var libCam : LibCam
    private lateinit var bitmap: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = arrayOf<String>(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)

        libPermissions = LibPermissions(this, permissions)
        libCam = LibCam(this, libPermissions, 100)

    }

    fun openCamera(view: View){
        showPictureDialog()
    }

    fun cropImage(view: View){
        if (bitmap!=null){
//            val uri = libCam.getBitmapUri(this,bitmap)
//            libCam.cropImage(uri!!)
//            libCam.savePhotoInMemoryDevice(bitmap, "Prefix",true)
            val savePhotoCallback = SavePhotoCallback()
//            savePhotoCallback.setSavePhotoListener(object :SavePhotoListener{
//                override fun onPhotoSaved(activity: Activity) {
//                    toast("Photo Saved Successfully")
//                    activity.finish()
//                }
//
//            })
//            savePhotoCallback.onSavePhoto(this)
            libCam.savePhotoInMemoryDevice(bitmap, DEFAULT_FILE_PREFIX, true)
            savePhotoCallback.setSavePhotoListener(this)
            savePhotoCallback.onSavePhoto(mCurrentPhotoPath)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        libCam.selectPicture()
    }

    private fun takePhotoFromCamera() {
        libCam.takePhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            TAKE_PHOTO ->{
                if (data!= null){
                    bitmap = libCam.loadBitmapFromUri(globalBitmapUri!!)!!
                    Glide.with(this).load(bitmap).into(imageViewCamera)
                }

            }
            SELECT_PHOTO ->{
                if (data!=null){
                    bitmap = libCam.resultPhoto(requestCode, resultCode, data)!!
                    Glide.with(this).load(bitmap).into(imageViewCamera)
                }else{
                    logD("Data is null")
                }

            }
            CROP_PHOTO ->{
                if (data!=null){
                    val uri= libCam.cropImageActivityResult(requestCode, resultCode, data)
                    bitmap = libCam.loadBitmapFromUri(uri!!)!!
                    Glide.with(this).load(bitmap).into(imageViewCamera)
                }else {
                    logD("Data is null")
                }
            }
        }
    }

    override fun onPhotoSaved(privateInfo: PrivateInformationObject) {
        if (privateInfo!=null){

            logD("Image Name $mCurrentImageName")
            logD("Orientation ${privateInfo.orientation}")
            logD("Model Name ${Build.MODEL}")
            logD("Make Device ${Build.BRAND}")
            logD("Model Name ${privateInfo.modelDevice}")
            logD("Make Device ${privateInfo.makeCompany}")
            logD("Latitude ${privateInfo.latitude}")
            logD("Longitude : ${privateInfo.longitude}")
            logD("Width ${privateInfo.imageWidth}")
            logD("Length ${privateInfo.imageLength}")
            logD("Date Stamp ${privateInfo.dateStamp}")
            logD("Date time take photo ${privateInfo.dateTimeTakePhoto}")
            toast("Save photo successfully")
        }else {
            logD("Some error occurred")
        }

        //activity.finish()

    }

}
