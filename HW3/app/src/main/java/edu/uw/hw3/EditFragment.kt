package edu.uw.hw3

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import edu.uw.hw3.databinding.FragmentEditBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class EditFragment : Fragment() {

    private var _binding: FragmentEditBinding? = null

    private lateinit var _editViewModel: EditViewModel

    private lateinit var _imageToEditPath: String

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _editViewModel =
            ViewModelProvider(this).get(EditViewModel::class.java)
        _binding = FragmentEditBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: EditFragmentArgs by navArgs()

        _editViewModel.firstBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imageviewFirst.setImageBitmap(
                    bitmap
                )
            }
        }

        _editViewModel.originalFirstBitmap.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null) {
                binding.imageviewOriginal.setImageBitmap(
                    bitmap
                )
            }
        }

        binding.buttonEditBlur.setOnClickListener {
            blurAndPreview()
        }

        binding.buttonEditClear.setOnClickListener {
            _editViewModel.setFirstBitmap(
                _editViewModel.originalFirstBitmap.value!!.copy(
                    _editViewModel.originalFirstBitmap.value!!.config,
                    true
                )
            )
        }
        binding.buttonMain.setOnClickListener {
            findNavController().navigate(R.id.editAction)
        }
        _imageToEditPath = args.imageToEdit + ""
        loadPhoto(args.imageToEdit)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //load the photo in two image views. One view will be used for blurring
    fun loadPhoto(path: String?) {
        var helper = PictureHelper()
        var bitmap: Bitmap? = helper.getImage(path)
        bitmap?.let { _editViewModel.setOriginalFirstBitmap(helper.resize(it)!!) }
        bitmap?.let { _editViewModel.setFirstBitmap(_editViewModel.originalFirstBitmap.value!!.copy(
            _editViewModel.originalFirstBitmap.value!!.config,
            true
        )) }
    }

    private fun blurAndPreview() {
        var helper = PictureHelper()
        var bitmap: Bitmap? = helper.resize(helper.getImage(_imageToEditPath));
        var faceDetectorHelper = FaceDetectorHelper()

        bitmap?.let {
            activity?.let { it1 ->
                faceDetectorHelper.bitmapBlur(
                    it,
                    it1, binding.imageviewFirst
                )
            }
        }
    }
}