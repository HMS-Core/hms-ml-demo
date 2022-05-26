package com.huawei.crazyrocketkotlin.ui.fragment

import android.content.Context
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {
    var baseContext: Context? = null

    constructor() : super()
    constructor(contentLayoutId: Int) : super(contentLayoutId)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        baseContext = context
    }
}