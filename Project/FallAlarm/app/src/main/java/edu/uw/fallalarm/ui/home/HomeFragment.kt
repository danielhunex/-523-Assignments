package edu.uw.fallalarm.ui.home


import android.R
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import edu.uw.fallalarm.databinding.FragmentHomeBinding
import edu.uw.fallalarm.service.FallDetectorService
import java.util.*


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var _started = false
    private lateinit var _homeViewModel: HomeViewModel

    private var _backgroundMessageReciever: BroadcastReceiver? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textHome
        _homeViewModel.getText()?.observe(viewLifecycleOwner, {
            textView.text = it
        })

        _homeViewModel.getHistories()?.observe(viewLifecycleOwner, {

            var histories: List<String?> = it.map { it.getMessage() }

            val adapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, histories)
            binding.listviewHistory.adapter = adapter
        })
        toggleStart(false)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnStart.setOnClickListener {
            val intent = Intent(context, FallDetectorService::class.java)
            context?.startService(intent)
            toggleStart(true)
        }

        binding.btnStop.setOnClickListener {
            val intent = Intent(context, FallDetectorService::class.java).also {
                it.action = "updates"
            }
            context?.stopService(intent)
            toggleStart(false)
        }
        val filter = IntentFilter()
        filter.addAction("edu.uw.status.transfer")
        _backgroundMessageReciever = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                val b: Bundle? = intent?.extras
                val value = b?.getString("heartbeat")
                binding.textHome.setBackgroundColor(Color.RED)
                if (value != null && value == "1") {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.textHome.setBackgroundColor(Color.GREEN)
                    }, 1000)
                }
            }
        }

        context?.registerReceiver(_backgroundMessageReciever, filter)
    }


    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("updates")
        _backgroundMessageReciever?.let {
            LocalBroadcastManager.getInstance(requireContext()).registerReceiver(it, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        if (_backgroundMessageReciever != null)
            context?.let {
                LocalBroadcastManager.getInstance(it)
                    .unregisterReceiver(_backgroundMessageReciever!!)
            }
        _backgroundMessageReciever = null
    }

    private fun toggleStart(toggle: Boolean) {
        _started = toggle
        binding.btnStop.isEnabled = _started
        binding.btnStart.isEnabled = !_started
    }
}
