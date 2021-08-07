package edu.uw.fallalarm.ui.emergencycontact

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.uw.fallalarm.R

class EmergencyContactFragment : Fragment() {

    companion object {
        fun newInstance() = EmergencyContactFragment()
    }

    private lateinit var viewModel: EmergencyContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.emergencycontact_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EmergencyContactViewModel::class.java)
        // TODO: Use the ViewModel
    }

}