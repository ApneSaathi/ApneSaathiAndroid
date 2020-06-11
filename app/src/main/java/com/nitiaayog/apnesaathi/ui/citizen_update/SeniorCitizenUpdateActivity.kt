package com.nitiaayog.apnesaathi.ui.citizen_update

import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import com.nitiaayog.apnesaathi.R
import com.nitiaayog.apnesaathi.base.extensions.getViewModel
import com.nitiaayog.apnesaathi.ui.base.BaseActivity
import kotlinx.android.synthetic.main.senior_citizen_update_form.*

class SeniorCitizenUpdateActivity : BaseActivity<SeniorCitizenUpdateViewModel>() {
    override fun provideViewModel(): SeniorCitizenUpdateViewModel =
        getViewModel { SeniorCitizenUpdateViewModel.getInstance(dataManager) }

    override fun provideLayoutResource(): Int = R.layout.senior_citizen_update_form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigationDrawer()
        initClicks()
    }

    private fun initClicks() {
        img_complaints.setOnClickListener { addMoreComplaints() }
        img_other_problems.setOnClickListener { addMoreOtherProblems() }
        cb_yes.setOnCheckedChangeListener { buttonView, b ->
            if(b)cb_no.isChecked = !buttonView.isChecked
        }
        cb_no.setOnCheckedChangeListener { buttonView, b ->
            if(b)cb_yes.isChecked = !buttonView.isChecked
        }
    }

    private fun addMoreOtherProblems() {
        val editText = EditText(this)
        editText.background = ContextCompat.getDrawable(this, R.drawable.bg_rectangle_border_form)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, resources.getDimension(R.dimen.dimen_5).toInt(), 0, 0)
        editText.layoutParams = params
        editText.minHeight = resources.getDimension(R.dimen.dimen_30).toInt()

        editText.setPadding(resources.getDimension(R.dimen.dimen_5).toInt(), 0, 0, 0)
        ll_medical_problems.addView(editText)
    }


    private fun addMoreComplaints() {
        val editText = EditText(this)
        editText.background = ContextCompat.getDrawable(this, R.drawable.bg_rectangle_border_form)
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, resources.getDimension(R.dimen.dimen_5).toInt(), 0, 0)
        editText.layoutParams = params
        editText.minHeight = resources.getDimension(R.dimen.dimen_30).toInt()

        editText.setPadding(resources.getDimension(R.dimen.dimen_5).toInt(), 0, 0, 0)
        ll_complaints_container.addView(editText)
    }

    private fun initNavigationDrawer() {
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.drawerOpen, R.string.drawerClose
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
}