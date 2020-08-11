package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.reviewrating

import android.os.Bundle
import android.view.View
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteerdetails.VolunteerDetailsViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.ID
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_admin_staff_reviews_rating.*
import java.util.concurrent.TimeUnit

class FragmentRatingReviews : BaseFragment<VolunteerDetailsViewModel>() {

    companion object {
        private val TAG: String by lazy { "TAG -- ${FragmentRatingReviews::class.java.simpleName} -->" }
        fun getInstance(volunteerId: Int, volunteerName: String): FragmentRatingReviews {
            return FragmentRatingReviews().apply {
                arguments = Bundle().apply {
                    putInt(ID, volunteerId)
                    putString(ApiConstants.UserName, volunteerName)
                }
            }
        }
    }

    private val volunteerId: Int by lazy {
        if (arguments != null) {
            if (arguments!!.containsKey(ID)) arguments!!.getInt(ID, 0)
            else 0
        } else 0
    }
    private val volunteerName: String by lazy {
        if (arguments != null) {
            if (arguments!!.containsKey(ApiConstants.UserName))
                arguments!!.getString(ApiConstants.UserName, "")
            else ""
        } else ""
    }

    private val progressDialog: ProgressDialog.Builder by lazy {
        ProgressDialog.Builder(requireContext()).setMessage(R.string.wait_we_update_ratings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Observable.timer(LOAD_ELEMENTS_WITH_DELAY, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread()).subscribe { observeNetworkStream() }
            .autoDispose(disposables)

        initViews()
    }

    override fun provideViewModel(): VolunteerDetailsViewModel {
        return getViewModel { VolunteerDetailsViewModel.getInstance(dataManager, volunteerId) }
    }

    override fun provideLayoutResource(): Int {
        return R.layout.fragment_admin_staff_reviews_rating
    }

    override fun onCallPermissionGranted() {

    }

    override fun onCallPermissionDenied() {

    }

    private fun initViews() {
        tvRatingText.text = getString(R.string.rate_quality_of_service, volunteerName)

        rating.setOnRatingBarChangeListener { _, rating, _ -> updateRatings(rating) }

        btnUpdateRatings.throttleClick().subscribe {
            BaseUtility.showAlertMessage(
                requireContext(), getString(R.string.alert),
                getString(R.string.update_ratings_successful, volunteerName),
                getString(R.string.okay)
            )
            /*lifecycleScope.launch(Dispatchers.IO) {
                viewModel.updateRatings(requireContext(), rating.rating)
            }*/
        }.autoDispose(disposables)
    }

    private fun updateRatings(rating: Float) {
        println("$TAG $rating")
        if (rating >= 4.0) tvRating.setText(R.string.excellent)
        else if ((rating >= 3.0) && (rating <= 3.9)) tvRating.setText(R.string.very_good)
        else if ((rating >= 2.0) && (rating <= 2.9)) tvRating.setText(R.string.good)
        else if ((rating >= 1.4) && (rating <= 1.9)) tvRating.setText(R.string.bad)
        else if (rating < 1.4) tvRating.setText(R.string.poor)
    }

    private fun observeNetworkStream() {
        //viewModel.getNetworkStream().removeObservers(viewLifecycleOwner)
        //viewModel.getNetworkStream().observe(viewLifecycleOwner, Observer {
        //manageNetworkState(it)
        //})
    }

    private fun manageNetworkState(state: NetworkRequestState) {
        when (state) {
            is NetworkRequestState.NetworkNotAvailable ->
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.error, R.string.check_internet
                )
            is NetworkRequestState.LoadingData -> progressDialog.show()
            is NetworkRequestState.Error -> {
                progressDialog.dismiss()
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.error, R.string.something_went_wrong
                )
            }
            is NetworkRequestState.ErrorResponse -> {
                progressDialog.dismiss()
                BaseUtility.showAlertMessage(
                    requireContext(), R.string.alert, R.string.can_not_connect_to_server
                )
            }
            is NetworkRequestState.SuccessResponse<*> -> {
                rating.isEnabled = false
                progressDialog.dismiss()
                btnUpdateRatings.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), getString(R.string.alert),
                    getString(R.string.update_ratings_successful, volunteerName),
                    getString(R.string.okay)
                )
            }
        }
    }
}