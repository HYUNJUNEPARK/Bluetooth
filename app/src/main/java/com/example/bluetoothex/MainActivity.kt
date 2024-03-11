package com.example.bluetoothex

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.example.bluetoothex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    /**
     * BluetoothManager: BluetoothAdapter 객체를 획득할 때 사용하는 클래스로 전체적인 블루투스 관리를 수행합니다.
     */
    private val bluetoothManager: BluetoothManager by lazy {
        getSystemService(BluetoothManager::class.java)
    }

    /**
     * 모든 Bluetooth 상호작용의 진입점입니다.
     * 이를 사용해 다른 Bluetooth 장치를 검색하고, 연결된(페어링된) 장치 목록을 쿼리하고,
     * MAC 주소를 사용하여 Bluetooth 기기를 인스턴스화(BluetoothDevice 객체 생성) 할 수 있고,
     * 다른 기기와 통신을 위해 Socket(BluetoothServerSocket)을 생성할 수 있습니다.
     */
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val activityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this,  "블루투스 활성화", Toast.LENGTH_SHORT).show()
                showMessage(this, "블루투스 활성화")
            } else if (it.resultCode == RESULT_CANCELED) {
                showMessage(this, "취소")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if(bluetoothAdapter == null) { //해당 객체가 Null 이라면 블루투스를 지원하지 않는 장비
            showMessage(this, "블루투스를 지원하지 않는 장비입니다.")
            finish()
        }

    }

    private fun showMessage(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    // 활성화 요청
    fun setActivate() {
        bluetoothAdapter?.let {
            // 비활성화 상태라면
            if (!it.isEnabled) { //현재 스마트폰에서 블루투스가 활성화 상태인지 비활성화 상태인지 알 수 있습니다.
                // 활성화 요청
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE) //시스템에 요청해서 블루투스 활성화를 요청할 수 있고
                activityResultLauncher.launch(intent)
            } else { // 활성 상태라면
                showMessage(this, "이미 활성화 되어 있습니다")
            }
        }
    }

    // 비활성화 요청
    @SuppressLint("MissingPermission")
    fun setDeActivate() {
        bluetoothAdapter?.let {
            // 비활성화 상태라면
            if (!it.isEnabled) {
                showMessage(this, "이미 비활성화 되어 있습니다")
                // 활성 상태라면
            } else {
                // 블루투스 비활성화
                it.disable()
                showMessage(this, "블루투스를 비활성화 하였습니다")
            }

        }
    }

    // 페어링된 디바이스 검색
    @SuppressLint("MissingPermission")
    fun getPairedDevices() {
        bluetoothAdapter?.let {
            // 블루투스 활성화 상태라면
            if (it.isEnabled) {
                // ArrayAdapter clear
                adapter.clear()
                // 페어링된 기기 확인
                val pairedDevices: Set<BluetoothDevice> = it.bondedDevices
                // 페어링된 기기가 존재하는 경우
                if (pairedDevices.isNotEmpty()) {
                    pairedDevices.forEach { device ->
                        // ArrayAdapter에 아이템 추가
                        adapter.add(Pair(device.name, device.address))
                    }
                } else {
                    showMessage(this, "페어링된 기기가 없습니다.")
                }
            } else {
                showMessage(this, "블루투스가 비활성화 되어 있습니다.")
            }
        }
    }
}