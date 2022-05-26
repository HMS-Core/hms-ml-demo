package com.huawei.crazyrocketkotlin.util.transactor

import android.util.SparseArray
import com.huawei.crazyrocketkotlin.view.GameGraphic
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.hms.mlsdk.handkeypoint.MLHandKeypoints

class HandKeyPointTransactor(gameGraphic: GameGraphic) : MLAnalyzer.MLTransactor<MLHandKeypoints> {

    private var gameGraphic: GameGraphic? = null

    init {
        this.gameGraphic = gameGraphic
    }

    override fun transactResult(results: MLAnalyzer.Result<MLHandKeypoints?>) {
        val analyseList: SparseArray<MLHandKeypoints?>? = results.analyseList

        if (analyseList == null || analyseList.size() <= 0)
            return

        val centerY: Int = analyseList[0]?.rect!!.centerY()
        gameGraphic?.setOffset(centerY.toFloat())
        gameGraphic?.invalidate()
    }

    override fun destroy() {}
}