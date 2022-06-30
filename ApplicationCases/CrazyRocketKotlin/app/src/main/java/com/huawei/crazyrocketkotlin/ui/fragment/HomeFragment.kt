package com.huawei.crazyrocketkotlin.ui.fragment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.fragment.findNavController
import com.huawei.crazyrocketkotlin.R
import com.huawei.crazyrocketkotlin.databinding.FragmentHomeBinding
import com.huawei.crazyrocketkotlin.util.GameUtils
import com.huawei.crazyrocketkotlin.util.viewBinding

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private var radioGroup: RadioGroup? = null
    private var cancel: AppCompatButton? = null
    private var dialog: AlertDialog? = null

    private var magnification = 1f
    private var choice = 0
    private var pickString: Array<String> = arrayOf()
    private var selectedLevel = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDialog()
        initListener()
        initData()
    }

    private fun setupDialog() {
        val view: View = LayoutInflater.from(binding.root.context).inflate(R.layout.custom_dialog_layout, null)
        radioGroup = view.findViewById(R.id.radio_group)
        cancel = view.findViewById(R.id.cancel)
        val builder = AlertDialog.Builder(binding.root.context).setView(view)

        dialog = builder.create()
        dialog?.window.apply {
            this?.setBackgroundDrawableResource(android.R.color.transparent)
            this?.setGravity(Gravity.BOTTOM)
        }
    }

    //ClickListeners
    private fun initListener() {
        binding.face.setOnClickListener {
                //Create a Face Analyze
                GameUtils.createFaceAnalyze()
                magnification = GameUtils.getMagnification() + 0.5f

                //LensEngine initialize
                GameUtils.initLensEngine(binding.root.context, 0)

                when (choice) {
                    0 -> selectedLevel = 8
                    1 -> selectedLevel = 4
                    2 -> selectedLevel = 1
                }

                val directions = HomeFragmentDirections.actionHomeFragmentToFaceGameFragment(selectedLevel,magnification)
                findNavController().navigate(directions)
        }

        binding.hand.setOnClickListener {
                //Create a Hand Analyze
                GameUtils.createHandAnalyze()
                magnification = GameUtils.getMagnification() + 0.5f

                //LensEngine initialize
                GameUtils.initLensEngine(binding.root.context, 1)

                when (choice) {
                    0 -> selectedLevel = 8
                    1 -> selectedLevel = 4
                    2 -> selectedLevel = 1
                }

                val directions = HomeFragmentDirections.actionHomeFragmentToHandGameFragment(selectedLevel,magnification)
                findNavController().navigate(directions)
        }

        binding.linearLevel.setOnClickListener { dialog?.show() }

        cancel?.setOnClickListener { dialog?.cancel() }

        //Select a Level
        radioGroup?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.hight -> {
                    choice = 0
                    binding.level.text = pickString[choice]
                }

                R.id.middle -> {
                    choice = 1
                    binding.level.text = pickString[choice]
                }

                R.id.low -> {
                    choice = 2
                    binding.level.text = pickString[choice]
                }
            }
            dialog?.dismiss()
        }
    }

    //Initialize level data
    private fun initData() {
        pickString = arrayOf(getString(R.string.hight), getString(R.string.middle), getString(R.string.low))
    }
}