package com.nitiaayog.apnesaathi.ui.fragments.profile

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nitiaayog.apnesaathi.R
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.include_toolbar.*
import kotlinx.android.synthetic.main.include_toolbar.toolBar

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View = inflater.inflate(R.layout.fragment_profile, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        toolBar.title = getString(R.string.menu_profile)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(activity is AppCompatActivity){
            (activity!! as AppCompatActivity).setSupportActionBar(toolBar)
        }
        setHasOptionsMenu(true)
    }

    override
    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu);

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return (when (item.itemId) {
            R.id.editprofile -> {
                Toast.makeText(getActivity(), "Clear call log", Toast.LENGTH_SHORT).show();
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        })
    }

}