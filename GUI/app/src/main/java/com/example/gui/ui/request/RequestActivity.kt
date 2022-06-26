package com.example.gui.ui.request

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import android.content.Intent
import com.example.gui.ui.main.MainActivity
import android.content.pm.PackageManager
import com.example.gui.databinding.RequestAuthorityBinding

/*
 * RequestActivity
 * 이 화면은 앱 첫 기동 시 권한 요청하기 위해 만들어졌다.
 */

class RequestActivity : AppCompatActivity() {
    private lateinit var binding: RequestAuthorityBinding
    private val request_Code = 101
    private val permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        setupUI()
    }

    fun init() {
        binding = RequestAuthorityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupUI() {
        binding.checkButton.setOnClickListener {
            if (!checkPermission()) {
                ActivityCompat.requestPermissions(this@RequestActivity, permissions, request_Code)
            } else {
                val intent = Intent(this@RequestActivity, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (checkPermission()) {
            val intent = Intent(this@RequestActivity, MainActivity::class.java)
            startActivity(intent)
        } else {
            ActivityCompat.requestPermissions(this@RequestActivity, permissions, request_Code)
        }
    }

    private fun checkPermission(): Boolean {
        for (pm in permissions) {
            if (ActivityCompat.checkSelfPermission(this, pm) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }
}