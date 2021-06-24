package com.xenori.pushbox

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.xenori.pushbox.misc.SharedPref
import kotlin.system.exitProcess

class MainActivity : BaseActivity()  {
    @BindView(R.id.url)
    lateinit var editUrl: EditText
    @BindView(R.id.save_btn)
    lateinit var saveBtn: LinearLayout

    private var exitTime: Long = 0
    private var prevFocusItem : View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)

        initView()
        initData()


    }

    fun initView() {
        var url = SharedPref.getString("obox_url", "")
        editUrl.setText(url)
    }

    fun initData() {

    }



    @OnClick(  R.id.save_btn )
    fun onClick(v : View) {
        when(v.id) {

            R.id.save_btn -> {
                SharedPref.editor().putString("obox_url", editUrl.text.toString()).apply()
                Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
            }
        }
    }



    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (event.action == KeyEvent.ACTION_DOWN) {

                        if ((System.currentTimeMillis() - exitTime) > 2000)
                        {
                            Toast.makeText(applicationContext, "再按一次退出程序", Toast.LENGTH_SHORT).show()
                            exitTime = System.currentTimeMillis()
                        } else {
                            finish()
                            exitProcess(0)
                        }
                        return true;
                    }
                }
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        val action = event!!.action
        val keyCode = event!!.keyCode

        return when (keyCode) {
            KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (action == KeyEvent.ACTION_DOWN && event!!.isLongPress) {


                    return true
                }
                super.dispatchKeyEvent(event)
            }
            else -> super.dispatchKeyEvent(event)
        }
    }

}