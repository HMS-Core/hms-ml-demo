package com.huawei.crazyrocketkotlin.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.huawei.crazyrocketkotlin.R
import com.huawei.crazyrocketkotlin.camera.LensEnginePreview
import com.huawei.crazyrocketkotlin.databinding.FragmentFaceGameBinding
import com.huawei.crazyrocketkotlin.util.GameUtils
import com.huawei.crazyrocketkotlin.util.viewBinding

class FaceGameFragment : BaseFragment(R.layout.fragment_face_game) {

    private val binding by viewBinding(FragmentFaceGameBinding::bind)

    private var lensEnginePreview: LensEnginePreview? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        initListener()
    }

    private fun setUp() {
        lensEnginePreview = binding.preview

        val bundle = arguments
        if (bundle == null) {
            Log.e("FaceGameFragment", "FaceGameFragment did not receive information")
            return
        }

        val args = FaceGameFragmentArgs.fromBundle(bundle)

        binding.gameGraphic.initData(binding.root.context, binding.gameOver,binding.score, args.level, args.magnification)
        GameUtils.setFaceTransactor(binding.gameGraphic)
    }

    private fun initListener() {
        binding.start.setOnClickListener {
            binding.gameStart.visibility = View.GONE
            binding.gameGraphic.startGame()
            binding.gameGraphic.invalidate()
        }

        binding.exit.setOnClickListener {
            findNavController().navigate(R.id.action_faceGameFragment_to_homeFragment)
        }

        binding.restart.setOnClickListener {
            binding.gameOver.visibility = View.GONE
            binding.gameGraphic.startGame()
            binding.gameGraphic.invalidate()
        }

    }

    override fun onResume() {
        super.onResume()
        lensEnginePreview?.let { GameUtils.startLensEngine(it) }
    }

    override fun onPause() {
        super.onPause()
        lensEnginePreview?.let { GameUtils.stopPreview(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        GameUtils.releaseAnalyze(0)
    }
}