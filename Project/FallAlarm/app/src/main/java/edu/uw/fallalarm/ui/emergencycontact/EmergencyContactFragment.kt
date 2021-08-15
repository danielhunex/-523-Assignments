package edu.uw.fallalarm.ui.emergencycontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import edu.uw.fallalarm.database.ContactEntity

import edu.uw.fallalarm.databinding.EmergencycontactFragmentBinding


class EmergencyContactFragment : Fragment() {

    private var _binding: EmergencycontactFragmentBinding? = null
    private lateinit var _viewModel: EmergencyContactViewModel
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = EmergencycontactFragmentBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(this).get(EmergencyContactViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _viewModel.getContact()?.observe(viewLifecycleOwner) {
            contact ->
            if(contact!=null) {
                binding.editTextName.setText( contact.getName())
                binding.editTextPhone.setText(contact.getPhoneNumber())
            }

            binding.buttonSave.setOnClickListener()
            {
                var name = binding.editTextName.text
                var phone = binding.editTextPhone.text

                //TODO: validation ->required and phone

                _viewModel.insertContact(name.toString(),phone.toString())
            }
        }
    }
}