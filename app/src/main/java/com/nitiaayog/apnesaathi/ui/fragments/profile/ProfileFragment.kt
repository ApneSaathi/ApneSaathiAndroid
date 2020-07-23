package com.nitiaayog.apnesaathi.ui.fragments.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.view.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.localization.LanguageSelectionActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.get_images_dialog.*
import kotlinx.android.synthetic.main.include_toolbar.toolBar
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class ProfileFragment : BaseFragment<ProfileFragmentViewModel>() {

    private val TAKE_GALLARY_PHOTO_REQUEST_CODE: Int = 1101
    private var image_uri: Uri? = null
    val PERMISSION_CODE: Int = 200
    val TAKE_PHOTO_REQUEST: Int = 201;
    var dialog: Dialog? = null;
    var menuBar: Menu? = null
    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(context!!).setMessage(R.string.wait_profile_data)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_profile, container, false)
        bindview(view);
        return view;
    }

    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
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

        }
        view.TxtLogout.setOnClickListener {
            val intent = Intent(activity, LoginActivity::class.java)
            dataManager.setPhoneNumber("")
            dataManager.setFirstName("")
            startActivity(intent)

        }
        view.TxtMainSave.throttleClick().subscribe() {
            if (view.EditEmail.text.toString().isEmailValid()) {

            } else {
                view.EditEmail.setError("Enter valid email")
            }
        }.autoDispose(disposables)

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //called when user presses ALLOW or DENY from Permission Request Popup
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {

                    ProfileshowPermissionTextPopup(R.string.Camera_permission_text)
//                    Snackbar.make(
//                        this.requireView(),
//                        resources.getString(R.string.permission_denied),
//                        Snackbar.LENGTH_LONG
//                    ).show()
                }
            }
        }
    }

    private fun ProfileshowPermissionTextPopup(
        @StringRes message: Int
    ) {
        val dialog = AlertDialog.Builder(activity, R.style.Theme_AlertDialog)
            .setTitle(R.string.permission_detail).apply {
                this.setMessage(message)
                this.setPositiveButton(R.string.accept) { dialog, _ ->
                    dialog.dismiss()

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", activity!!.packageName, null)
                    }
                    startActivityForResult(intent, CONST_PERMISSION_FROM_SETTINGS)

                }
                this.setNegativeButton(R.string.not_now) { dialog, _ -> dialog.dismiss() }
            }.create()
        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.color_orange))
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(context!!, R.color.color_orange))
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
        try {
//            val values = ContentValues()
//            values.put(MediaStore.Images.Media.TITLE, "New Picture")
//            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
//            image_uri = activity!!.contentResolver.insert(
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                values
//            )
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
//            startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)

        } catch (ex: Exception) {
            ProfileshowPermissionTextPopup(R.string.Camera_permission_text)
        }
    }

    fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, TAKE_GALLARY_PHOTO_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == TAKE_GALLARY_PHOTO_REQUEST_CODE) {
                EditImageView.setImageURI(data?.data) // handle chosen image
                ProfileImage.setImageURI(data?.data)

                dialog!!.dismiss()
            } else if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST && data != null) {
//                EditImageView.setImageBitmap(data.extras?.get("data") as Bitmap)

                var fileq: String = imageto64String(data.extras?.get("data") as Bitmap);
                createABitmap(fileq)
                dialog!!.dismiss()
//                EditImageView.setImageURI(image_uri)
//                ProfileImage.setImageURI(image_uri)
//                dialog!!.dismiss()
            }
        } catch (ex: Exception) {
            ProfileshowPermissionTextPopup(R.string.Camera_permission_text)

        }
    }

    private fun createABitmap(base64String: String) {


        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        EditImageView.setImageBitmap(decodedImage)
    }

    private fun imageto64String(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }


    private fun showGetImageDialog() {
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(true)
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

        if (dataManager.getFirstname().isEmpty()) {
            try {
                Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        viewModel.getvolunteerData(context!!, dataManager.getPhoneNumber())
                    }
                    .autoDispose(disposables)
            } catch (e: Exception) {
                println("TAG -- MyData --> ${e.message}")
            }
            observeStates()
        } else {
            TxtName.text = dataManager.getFirstname() + " " + dataManager.getLastname()
            txtAddress.text = dataManager.getAddress()
            TxtContactNumber.text = dataManager.getPhoneNumber()
            TxtEmail.text = dataManager.getEmail()
        }

        updateEditField()


    }

    private fun updateEditField() {
        EditFirstName.setText(
            dataManager.getFirstname().toString() + " " + dataManager.getLastname().toString()
        )
        EditAddress.setText(txtAddress.text.toString())
        EditPhone.setText(TxtContactNumber.text.toString())
        EditEmail.setText(TxtEmail.text.toString())
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
                updateEditField()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

    override fun provideViewModel(): ProfileFragmentViewModel {
        return getViewModel { ProfileFragmentViewModel.getInstance(dataManager) }
    }

    override fun provideLayoutResource(): Int {
        TODO("Not yet implemented")
    }

    override fun onCallPermissionGranted() {
        openCamera()
    }

    override fun onCallPermissionDenied() {
    }

    private fun observeStates() {

        viewModel.getDataObserver().removeObservers(viewLifecycleOwner)
        viewModel.getDataObserver().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable ->
                    BaseUtility.showAlertMessage(context!!, R.string.alert, R.string.check_internet)

                is NetworkRequestState.LoadingData -> {
                    progressDialog.show()
                }
                is NetworkRequestState.ErrorResponse -> {
                    progressDialog.dismiss()
                }
                is NetworkRequestState.SuccessResponse<*> -> {
                    progressDialog.dismiss()

                    val volunteerDataResponse = it.data

                    if (volunteerDataResponse is VolunteerDataResponse) {
                        dataManager.setFirstName(volunteerDataResponse.volunteer.firstName)
                        dataManager.setLastname(volunteerDataResponse.volunteer.lastName)
                        dataManager.setEmail(volunteerDataResponse.volunteer.email)
                        dataManager.setAddress(volunteerDataResponse.volunteer.address)

                        TxtName.text =
                            volunteerDataResponse.volunteer.firstName + " " + volunteerDataResponse.volunteer.lastName
                        txtAddress.text =
                            volunteerDataResponse.volunteer.address + " , " + volunteerDataResponse.volunteer.state + " , " + volunteerDataResponse.volunteer.district
                        TxtContactNumber.text = volunteerDataResponse.volunteer.phoneNo
                        TxtEmail.text = volunteerDataResponse.volunteer.email

                    }
                }
            }
        })
    }


}