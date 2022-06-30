package com.huawei.crazyrocketkotlin.util

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Camera
import android.util.Log
import com.huawei.crazyrocketkotlin.camera.LensEnginePreview
import com.huawei.crazyrocketkotlin.util.transactor.FaceAnalyzerTransactor
import com.huawei.crazyrocketkotlin.util.transactor.HandKeyPointTransactor
import com.huawei.hms.mlsdk.common.LensEngine
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzerFactory
import java.io.IOException
import com.huawei.crazyrocketkotlin.view.GameGraphic
import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.face.MLFaceAnalyzer
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypointAnalyzer


object GameUtils {

    private const val TAG = "GameUtils"
    private var faceAnalyzer : MLFaceAnalyzer? = null
    private var handAnalyzer : MLHandKeypointAnalyzer? = null

    @SuppressLint("StaticFieldLeak")
    private var lensEngine: LensEngine? = null
    var width = 0
    var height = 0

    //Face Analyze creator
    fun createFaceAnalyze() {
        faceAnalyzer = MLAnalyzerFactory.getInstance().faceAnalyzer
    }

    //Hand Analyze creator
    fun createHandAnalyze() {
        handAnalyzer = MLHandKeypointAnalyzerFactory.getInstance().handKeypointAnalyzer
    }

    //set FaceTransactor
    fun setFaceTransactor(gameGraphic: GameGraphic) =
        faceAnalyzer?.setTransactor(FaceAnalyzerTransactor(gameGraphic))

    //set HandTransactor
    fun setHandTransactor(gameGraphic: GameGraphic) =
        handAnalyzer?.setTransactor(HandKeyPointTransactor(gameGraphic))

    fun getMagnification(): Float {
        val magnification: Int
        val camera = Camera.open(1)
        val supportedPreviewSizes = camera.parameters.supportedPreviewSizes

        for (i in supportedPreviewSizes.indices.reversed()) {
            width = supportedPreviewSizes[i].width
            height = supportedPreviewSizes[i].height

            if (width >= 300 && height >= 300) {
                break
            }
        }
        camera.release()
        magnification = supportedPreviewSizes[0].width / width
        return magnification.toFloat()
    }

    //Initialize LensEngine
    fun initLensEngine(context: Context?,analyzeType: Int) {
        when(analyzeType) {
            0 -> lensEngine = LensEngine.Creator(context, faceAnalyzer)
                    .setLensType(LensEngine.FRONT_LENS)
                    .applyDisplayDimension(width, height)
                    .applyFps(30.0f)
                    .enableAutomaticFocus(true)
                    .create()

           1 -> lensEngine = LensEngine.Creator(context, handAnalyzer)
               .setLensType(LensEngine.FRONT_LENS)
               .applyDisplayDimension(width, height)
               .applyFps(30.0f)
               .enableAutomaticFocus(true)
               .create()
        }
    }

    //Start a LensEngine
    fun startLensEngine(preview: LensEnginePreview) {
            try {
                preview.start(lensEngine)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start lens engine.", e)
                lensEngine?.release()
                lensEngine = null
            }
    }

    //Stop LensEngine
    fun stopPreview(mPreview: LensEnginePreview) = mPreview.stop()

    //Destroy analyzer
    fun releaseAnalyze(analyzeType: Int) {
        lensEngine?.release()
        when(analyzeType) {
          0 -> faceAnalyzer?.destroy()
          1 -> handAnalyzer?.destroy()
        }
    }
}