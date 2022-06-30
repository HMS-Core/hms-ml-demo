package com.huawei.crazyrocketkotlin.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.huawei.crazyrocketkotlin.databinding.ActivityMainBinding
import com.huawei.crazyrocketkotlin.util.viewBinding


class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)

    companion object {
        private val PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        private const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (!isGranted(Manifest.permission.CAMERA))
        requestPermission()
    }

    private fun isGranted(permission: String): Boolean {
        val checkSelfPermission = checkSelfPermission(permission)
        return checkSelfPermission == PackageManager.PERMISSION_GRANTED
    }

    //Request for Camera and Storage Permissions
    private fun requestPermission(): Boolean {
        if (!isGranted(PERMISSIONS[0])) {
            requestPermissions(PERMISSIONS, REQUEST_CODE)
        }
        return true
    }

}