package com.nitiaayog.apnesaathi.ui.fragments.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.ui.localization.LanguageSelectionActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.fragment_register_new_sr_citizen.*
import kotlinx.android.synthetic.main.get_images_dialog.*
import kotlinx.android.synthetic.main.include_toolbar.toolBar

class ProfileFragment : Fragment() {

    private val REQUEST_CODE: Int = 1101
    private var image_uri: Uri? = null
    val PERMISSION_CODE: Int = 200
    val TAKE_PHOTO_REQUEST: Int = 201;
    var dialog: Dialog? = null;
    var menuBar: Menu? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        bindview(view);
        return view;
    }

    private fun bindview(view: View) {

        view.EditImageView.setOnClickListener {
            showGetImageDialog()
        }
        view.TxtMainSave.setOnClickListener {
            LinearProfileDetails.isVisible = true
            LinearProfileDetailsForEdit.isVisible = false
            callMenuBarHide(LinearProfileDetails.isVisible)
        }
        view.TxtMainCancel.setOnClickListener {
            LinearProfileDetails.isVisible = true
            LinearProfileDetailsForEdit.isVisible = false
            callMenuBarHide(LinearProfileDetails.isVisible)
        }
        view.TxtChangeLanguage.setOnClickListener {
            val intent = Intent(activity, LanguageSelectionActivity::class.java)
            startActivity(intent)
            activity!!.finish()
        }
    }

    fun callMenuBarHide(linearProfileDetails: Boolean) {
        if (linearProfileDetails) {
            val Import: MenuItem = menuBar!!.findItem(R.id.editprofile)
            Import.setVisible(true)
        } else {
            val Import: MenuItem = menuBar!!.findItem(R.id.editprofile)
            Import.setVisible(false)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<out String>,grantResults: IntArray) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Snackbar.make(
                        constraintLayout,
                        resources.getString(R.string.permission_denied),
                        Snackbar.LENGTH_LONG
                    ).show()

                }
            }
        }
    }


    @SuppressLint("WrongConstant")
    fun capturePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    activity!!,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri =
            activity!!.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)
    }

    fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            EditImageView.setImageURI(data?.data) // handle chosen image
            ProfileImage.setImageURI(data?.data)

            dialog!!.dismiss()
        } else if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            EditImageView.setImageURI(image_uri)
            ProfileImage.setImageURI(image_uri)
            dialog!!.dismiss()
        }
    }


    private fun showGetImageDialog() {
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.get_images_dialog)
        dialog!!.TxtGetFromcamera.setOnClickListener {
            capturePhoto()
        }
        dialog!!.TxtCancel.setOnClickListener {
            dialog!!.dismiss()
        }

        dialog!!.TxtGetFromGallery.setOnClickListener {
            fromGallery()
        }
        dialog!!.show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        toolBar.title = getString(R.string.menu_profile)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is AppCompatActivity) {
            (activity!! as AppCompatActivity).setSupportActionBar(toolBar)
        }
        setHasOptionsMenu(true)
    }

    override
    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuBar = menu
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.editprofile -> {
                LinearProfileDetails.isVisible = false
                LinearProfileDetailsForEdit.isVisible = true
                val Import: MenuItem = menuBar!!.findItem(R.id.editprofile)
                Import.setVisible(false)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }


}