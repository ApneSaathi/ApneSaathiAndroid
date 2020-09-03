package com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.reviewrating

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.ProgressDialog
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.base.extensions.rx.autoDispose
import com.nitiaayog.apnesaathi.base.extensions.rx.throttleClick
import com.nitiaayog.apnesaathi.model.Volunteer
import com.nitiaayog.apnesaathi.networkadapter.api.apirequest.NetworkRequestState
import com.nitiaayog.apnesaathi.networkadapter.apiconstants.ApiConstants
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteerdetails.VolunteerDetailsViewModel
import com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers.VolunteersFragment
import com.nitiaayog.apnesaathi.ui.base.BaseFragment
import com.nitiaayog.apnesaathi.utility.BaseUtility
import com.nitiaayog.apnesaathi.utility.ID
import com.nitiaayog.apnesaathi.utility.LOAD_ELEMENTS_WITH_DELAY
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_admin_staff_reviews_rating.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * To rate volunteer by their Seniors this page will be useful
 * */
class FragmentRatingReviews : BaseFragment<VolunteerDetailsViewModel>() {

    companion object {
        private val TAG: String by lazy { "TAG -- ${FragmentRatingReviews::class.java.simpleName} -->" }
        fun getInstance(volunteer: Volunteer): Fragment {
            return FragmentRatingReviews().apply {
                arguments = Bundle().apply {
                    putInt(ID, volunteer.id!!)
                    putString(ApiConstants.FirstName, volunteer.firstName)
                    putString(ApiConstants.LastName, volunteer.lastName)
                    putString(ApiConstants.Ratings, volunteer.ratings)
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

    private val firstName: String by lazy {
        if (arguments != null) {
            if (arguments!!.containsKey(ApiConstants.FirstName))
                arguments!!.getString(ApiConstants.FirstName, "")
            else ""
        } else ""
    }

    private val volunteerName: String by lazy {
        if (arguments != null) {
            val lastName: String = if (arguments!!.containsKey(ApiConstants.LastName))
                arguments!!.getString(ApiConstants.LastName, "")
            else ""

            firstName.plus(" ").plus(lastName)
        } else ""
    }

    private val ratings: String by lazy {
        if (arguments != null) {
            if (arguments!!.containsKey(ApiConstants.Ratings))
                arguments!!.getString(ApiConstants.Ratings, "0.0")
            else "0.0"
        } else "0.0"
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

    /**
     * Load initial values of volunteer ratings
     **/
    private fun initViews() {
        tvRatingText.text = getString(R.string.rate_quality_of_service, volunteerName)

        rating.setOnRatingBarChangeListener { _, rating, _ -> updateRatings(rating) }
        rating.rating = ratings.toFloat()

        tvRatingCount.text = ratings

        btnRateVolunteer.text = getString(R.string.rate_volunteer, volunteerName)
        btnRateVolunteer.throttleClick().subscribe {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.rateVolunteer(requireContext(), rating.rating, firstName)
            }
        }.autoDispose(disposables)
    }

    /**
     * When star slider's values are updated we will show the ratings and status of it
     **/
    private fun updateRatings(rating: Float) {
        //println("$TAG $rating")
        if (rating >= 4.0) tvRating.setText(R.string.excellent)
        else if ((rating >= 3.0) && (rating <= 3.9)) tvRating.setText(R.string.very_good)
        else if ((rating >= 2.0) && (rating <= 2.9)) tvRating.setText(R.string.good)
        else if ((rating >= 1.4) && (rating <= 1.9)) tvRating.setText(R.string.bad)
        else if (rating < 1.4) tvRating.setText(R.string.poor)

        tvRatingCount.text = rating.toString()
    }

    private fun observeNetworkStream() {
        viewModel.getNetworkStream().removeObservers(viewLifecycleOwner)
        viewModel.getNetworkStream().observe(viewLifecycleOwner) { manageNetworkState(it) }
    }

    /**
     * Through this method we will call onActivityResult of
     * @see com.nitiaayog.apnesaathi.ui.adminandstaffmember.fragments.volunteers.VolunteersFragment
     * to store updated data in database and in adapters
     * */
    private fun updatePreviousFragment() {
        try {
            targetFragment?.let {
                val intent: Intent = Intent().apply {
                    putExtra(ID, volunteerId)
                    putExtra(ApiConstants.Ratings, rating.rating.toString())
                }
                it.onActivityResult(
                    VolunteersFragment.VolunteerDetailsCode,
                    VolunteersFragment.VolunteerDetailsCode, intent
                )
            }
        } catch (e: Exception) {
            println("$TAG ${e.message}")
        }
    }

    /**
     * When network status is updating we can update Ui accordingly via this live data callbacks
     **/
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
                updatePreviousFragment()
                progressDialog.dismiss()
                btnRateVolunteer.visibility = View.GONE
                BaseUtility.showAlertMessage(
                    requireContext(), getString(R.string.success),
                    getString(R.string.update_ratings_successful, volunteerName),
                    getString(R.string.okay)
                )
            }
        }
    }
}