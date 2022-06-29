package com.huawei.crazyrocketkotlin.util.transactor

import com.huawei.hms.mlsdk.face.MLFaceKeyPoint.TYPE_TIP_OF_NOSE
import com.huawei.hms.mlsdk.face.MLFace
import com.huawei.hms.mlsdk.common.MLAnalyzer
import com.huawei.crazyrocketkotlin.view.GameGraphic


class FaceAnalyzerTransactor(gameGraphic: GameGraphic) : MLAnalyzer.MLTransactor<MLFace> {

    private var gameGraphic: GameGraphic? = null

    init {
        this.gameGraphic = gameGraphic
    }

    override fun transactResult(results: MLAnalyzer.Result<MLFace>) {
        val items = results.analyseList

        //If items is null
        if (items.size() <= 0) {
            return
        }

        //Result data about Face
        val centerY = items[0].getFaceKeyPoint(TYPE_TIP_OF_NOSE)
        gameGraphic?.setOffset(centerY.coordinatePoint.y)
        gameGraphic?.invalidate()
    }

    override fun destroy() {}
}