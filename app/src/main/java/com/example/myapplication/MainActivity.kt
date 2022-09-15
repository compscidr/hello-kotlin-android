package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.lib.MyClass
import dagger.hilt.android.AndroidEntryPoint
import logcat.logcat
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var testComponent: TestComponent

    @Inject
    lateinit var myClass: MyClass

    private fun testObfuscation() {
        Log.d("LOGSTRING", "TEST MESSAGE")
        logcat { "logcat test" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testObfuscation()
        testComponent.someFunction()

        myClass.display()
    }
}
