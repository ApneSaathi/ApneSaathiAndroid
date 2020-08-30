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
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.api.apiresponce.volunteerdata.VolunteerDataResponse
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiProvider
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.ui.fragments.grievances.GrievanceDetailsViewModel
import com.nitiaayog.apnesaathi.ui.localization.LanguageSelectionActivity
import com.nitiaayog.apnesaathi.ui.login.LoginActivity
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import com.nitiaayog.apnesaathi.utility.ROLE_VOLUNTEER
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.get_images_dialog.*
import kotlinx.android.synthetic.main.include_toolbar.toolBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

/**
 * Fragment for showing the detailed view of user profile!
  * [BaseFragment] is the base fragment with functions that are common in all the fragments
 * [ProfileFragmentViewModel] is the view model for performing fetching user profile details data from API
 * [ProfileFragment] will handel the show user profile details and edit user profile details.
 */
class ProfileFragment : BaseFragment<ProfileFragmentViewModel>() {

    private val TAKE_GALLARY_PHOTO_REQUEST_CODE: Int = 1101
    val PERMISSION_CODE: Int = 200
    val TAKE_PHOTO_REQUEST: Int = 201
    var dialog: Dialog? = null
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
        bindview(view)
        return view
    }
    /**
     * Method for validating the email address.
     */
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this)
            .matches()
    }
    /**
     * Method for binding the data
     * [view] get all UI component.
     * view.TxtMainSave onClick user will update the user data to server
     * view.TxtMainCancel onClick to visible the show user details Layout and hide user editable layout
     */
    private fun bindview(view: View) {
        view.EditImageView.isClickable = false
        view.EditImageView.setOnClickListener {
//            showGetImageDialog()
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
            lifecycleScope.launch(Dispatchers.Main) {
                val count = withContext(Dispatchers.IO) {
                    viewModel.getCountOfDataRemainingToSync()
                }
                if (count <= 0) BaseUtility.showAlertMessage(
                    requireContext(), getString(R.string.alert), getString(R.string.logout_message),
                    getString(R.string.logout)
                ) { dialog, _ ->
                    dialog.dismiss()
                    prepareToLogout()
                }
                else BaseUtility.showAlertMessage(
                    requireContext(), R.string.alert, R.string.logout_error_message,
                    R.string.logout, R.string.no, { dialog, _ ->
                        dialog.dismiss()
                        prepareToLogout()
                    }, { dialog, _ -> dialog.dismiss() }
                )
            }
        }

        view.TxtMainSave.throttleClick().subscribe {
            if (TextUtils.isEmpty(view.EditFirstName.text.toString())) {
                view.EditFirstName.error = "Enter first name"
            } else {
                view.EditFirstName.error = null
                if (TextUtils.isEmpty(view.EditLastName.text.toString())) {
                    view.EditLastName.setError("Enter last name")
                } else {
                    view.EditLastName.error = null
                    if (TextUtils.isEmpty(EditAddress.text.toString())) {
                        EditAddress.error = "Enter Address"

                    } else {
                        EditAddress.error = null
                        if (view.EditEmail.text.toString().isEmailValid()) {
                            try {
                                Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                                        viewModel.getUpdatedvolunteerData(
                                            context!!, dataManager.getUserId(),
                                            EditFirstName.text.toString().trim(),
                                            EditLastName.text.toString().trim(),
                                            EditAddress.text.toString().trim(),
                                            EditEmail.text.toString().trim()
                                        )
                                    }
                                    .autoDispose(disposables)
                            } catch (e: Exception) {
                                println("TAG -- MyData --> ${e.message}")
                            }
                        } else {
                            view.EditEmail.error = "Enter valid email"
                        }
                    }
                }


            }


        }.autoDispose(disposables)

    }
    /**
     * Method for hiding the edit option in toolbar.
     * [linearProfileDetails] RootLayout of show user profile details.
     */
    private fun callMenuBarHide(linearProfileDetails: Boolean) {
        menuBar!!.findItem(R.id.editprofile).isVisible = linearProfileDetails
    }


    /**
     * Method for handling the runtime permission result.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    ProfileshowPermissionTextPopup(R.string.Camera_permission_text)
                }
            }
        }
    }


    /**
     * Method for showing the pop-up after runtime permission deny
     */
    private fun ProfileshowPermissionTextPopup(@StringRes message: Int) {
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


    /**
     * Method for prepare the camera open of system
     */
    @SuppressLint("WrongConstant")
    fun capturePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    activity!!,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                val permission =
                    arrayOf(Manifest.permission.CAMERA)
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    /**
     * Method for open a system camera for taking a photo.
     */
    private fun openCamera() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, TAKE_PHOTO_REQUEST)

        } catch (ex: Exception) {
            ProfileshowPermissionTextPopup(R.string.Camera_permission_text)
        }
    }

    /**
     * Method for open a system gallery for taking a photo.
     */
    fun fromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, TAKE_GALLARY_PHOTO_REQUEST_CODE)
    }

    /**
     * This method is hiding now, not required this functionality.
     * Method for getting a result of capture image from camera and image from gallery.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            if (resultCode == Activity.RESULT_OK && requestCode == TAKE_GALLARY_PHOTO_REQUEST_CODE) {
                EditImageView.setImageURI(data?.data) // handle chosen image
                ProfileImage.setImageURI(data?.data)
                dialog!!.dismiss()
            } else if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST && data != null) {
                var fileq: String = imageto64String(data.extras?.get("data") as Bitmap)
                createABitmap(fileq)
                dialog!!.dismiss()
            }
        } catch (ex: Exception) {
            ProfileshowPermissionTextPopup(R.string.Camera_permission_text)
        }
    }

    /**
     * This method is hiding now, not required this functionality.
     * Method for creating a Bitmap from string.
     */
    private fun createABitmap(base64String: String) {
        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        EditImageView.setImageBitmap(decodedImage)
    }

    /**
     * This method is hiding now, not required this functionality.
     * Method for creating a 64Image from Bitmap.
     */
    private fun imageto64String(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }


    /**
     * This method is hiding now, not required this functionality.
     * Method for get profile image from camera and gallery.
     */
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

        if (dataManager.getFirstName().isNullOrEmpty()) {
            TxtName.text = "-"
        } else {
            TxtName.text = dataManager.getFirstName()
        }

        if (dataManager.getAddress().isNullOrEmpty()) {
            txtAddress.text = "-"
        } else {
            txtAddress.text = dataManager.getAddress()
        }

        if (dataManager.getPhoneNumber().isNullOrEmpty()) {
            TxtContactNumber.text = "-"
        } else {
            TxtContactNumber.text = dataManager.getPhoneNumber()
        }



        if (dataManager.getEmail().isNullOrEmpty()) {
            TxtEmail.text = "-"
        } else {
            TxtEmail.text = dataManager.getEmail()
        }

        observeStates()
        callvolunteerData()
        updateEditField()


    }

    /**
     * Method for request to the API of volunteer profile details
     */
    private fun callvolunteerData() {
        try {
            Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    viewModel.getvolunteerData(context!!, dataManager.getPhoneNumber())
                }.autoDispose(disposables)
        } catch (e: Exception) {
            println("TAG -- MyData --> ${e.message}")
        }
    }


    /**
     * Method for updating the UI from shared preferences data
     */
    private fun updateEditField() {
        EditFirstName.setText(dataManager.getFirstName().toString())
        EditLastName.setText(dataManager.getLastName())

        EditAddress.setText(txtAddress.text.toString())
        EditPhone.setText(TxtContactNumber.text.toString())
        EditEmail.setText(TxtEmail.text.toString())



        if (dataManager.getGender().equals("F")) {
            EditImageView.setImageResource(R.drawable.ic_volunteer_female)
        } else {
            EditImageView.setImageResource(R.drawable.ic_volunteer_male)

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is AppCompatActivity) {
            (activity!! as AppCompatActivity).setSupportActionBar(toolBar)
        }
        setHasOptionsMenu(true)
    }


    /**
     * Method for onCreateOptionsMenu
     */
    override  fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menuBar = menu
        inflater.inflate(R.menu.profile_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    /**
     * Method for for click on onCreateOptionsMenu
     * after clicking on editprofile option menu will hide the user profile details layout and visible to the user details edit layout,
     */
    override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.editprofile -> {
                LinearProfileDetails.isVisible = false
                LinearProfileDetailsForEdit.isVisible = true
                val Import: MenuItem = menuBar!!.findItem(R.id.editprofile)
                Import.isVisible = false
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

    /**
     * Method for prepare user logout
     * clear the all shared preferences data
     */
    private fun prepareToLogout() {
        viewModel.clearData()
        proceedToLogout()
    }
    /**
     * Method for user is logout properly
     * And call login Activity
     */
    private fun proceedToLogout() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finishAffinity()
    }

    /**
     * Method for fetching the data from API.
     * This method also handles all the network issues.
     */
    private fun observeStates() {
        viewModel.getDataObserver().removeObservers(viewLifecycleOwner)
        viewModel.getDataObserver().observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkRequestState.NetworkNotAvailable -> when (it.apiName) {
                    ApiProvider.ApiGetVolunteerData ->
                        BaseUtility.showAlertMessage(
                            context!!,
                            R.string.alert,
                            R.string.check_internet
                        )
                    ApiProvider.ApiGetVolunteerData ->
                        BaseUtility.showAlertMessage(
                            context!!, R.string.alert, R.string.check_internet
                        )
                }
                is NetworkRequestState.LoadingData -> {
                    when (it.apiName) {
                        ApiProvider.ApiGetVolunteerData -> {
                            progressBarloadData.visibility = VISIBLE
//                            BaseUtility.showAlertMessage(
//                                activity!!, R.string.alert, R.string.can_not_connect_to_server
//                            )
                        }
                        ApiProvider.ApiUpdateProfile -> {
                            progressDialog.show()
//                            BaseUtility.showAlertMessage(
//                                activity!!, R.string.alert, R.string.can_not_connect_to_server
//                            )
                        }

                    }
                }
                is NetworkRequestState.ErrorResponse -> {
                    when (it.apiName) {
                        ApiProvider.ApiGetVolunteerData ->
                            progressBarloadData.visibility = GONE
                        ApiProvider.ApiUpdateProfile ->
                            progressDialog.dismiss()
                    }
                }
                is NetworkRequestState.SuccessResponse<*> -> {

                    when (it.apiName) {
                        ApiProvider.ApiGetVolunteerData -> {
                            progressBarloadData.visibility = GONE
                            getdataFromApi(it)
                        }
                        ApiProvider.ApiUpdateProfile -> {
                            progressDialog.dismiss()
                            var snack = Snackbar.make(
                                mainRootRelativeLayout,
                                R.string.profile_details_saved_successfully,
                                Snackbar.LENGTH_LONG
                            )
                            snack.show()
                            LinearProfileDetails.isVisible = true
                            LinearProfileDetailsForEdit.isVisible = false
                            callMenuBarHide(LinearProfileDetails.isVisible)
                            callvolunteerData()
                        }
                    }
                }
            }
        })
    }

    /**
     * Method for get data from volunteer profile details API
     * [apiresponse] is the data of volunteer profile details
     * save data in shared preference
     * also set default image for female user or male user
     */
    private fun getdataFromApi(apiresponse: NetworkRequestState.SuccessResponse<*>) {
        val volunteerDataResponse = apiresponse.data

        if (volunteerDataResponse is VolunteerDataResponse) {
            if (dataManager.getRole() == ROLE_VOLUNTEER) {
                dataManager.setFirstName(volunteerDataResponse.volunteer!!.firstName!!)
                dataManager.setLastName(volunteerDataResponse.volunteer!!.lastName!!)
                dataManager.setEmail(volunteerDataResponse.volunteer!!.email!!)
                dataManager.setAddress(volunteerDataResponse.volunteer!!.address!!)
                dataManager.setGender(volunteerDataResponse.volunteer!!.gender!!)
                dataManager.setDistrict(volunteerDataResponse.volunteer!!.district!!)
                dataManager.setState(volunteerDataResponse.volunteer!!.state!!)

                TxtName.text =
                    volunteerDataResponse.volunteer!!.firstName + " " + volunteerDataResponse.volunteer!!.lastName

                txtAddress.text =
                    volunteerDataResponse.volunteer!!.address
                TxtContactNumber.text = volunteerDataResponse.volunteer!!.phoneNo
                TxtEmail.text = volunteerDataResponse.volunteer!!.email
            } else {
                dataManager.setFirstName(volunteerDataResponse.admin!!.firstName ?: "")
                dataManager.setLastName(volunteerDataResponse.admin.lastName ?: "")
                dataManager.setEmail(volunteerDataResponse.admin.email ?: "")
                dataManager.setAddress(volunteerDataResponse.admin.address ?: "")
                dataManager.setGender(volunteerDataResponse.admin.gender ?: "")
                dataManager.setDistrict(volunteerDataResponse.admin.district ?: "")
                dataManager.setState(volunteerDataResponse.admin.state ?: "")

                TxtName.text =
                    volunteerDataResponse.admin.firstName.plus(" ")
                        .plus(volunteerDataResponse.admin.lastName)

                txtAddress.text = volunteerDataResponse.admin.address
                TxtContactNumber.text = volunteerDataResponse.admin.phoneNo
                TxtEmail.text = volunteerDataResponse.admin.email
            }

            if (dataManager.getGender().equals("F")) {
                ProfileImage.setImageResource(R.drawable.ic_volunteer_female)
            } else {
                ProfileImage.setImageResource(R.drawable.ic_volunteer_male)
            }
        }
    }
}