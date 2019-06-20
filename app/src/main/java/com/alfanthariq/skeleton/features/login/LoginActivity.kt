package com.alfanthariq.skeleton.features.login

import alfanthariq.com.signatureapp.util.PreferencesHelper
import alfanthariq.com.signatureapp.utils.animation.LoadingCircleAnim
import android.Manifest
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.ViewTreeObserver
import android.view.animation.Animation
import androidx.appcompat.app.AlertDialog
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.features.base.BaseActivity
import com.alfanthariq.skeleton.features.main.MainActivity
import com.alfanthariq.skeleton.utils.AppRoute
import com.alfanthariq.skeleton.utils.gone
import com.alfanthariq.skeleton.utils.visible
import com.livinglifetechway.k4kotlin.onClick
import com.livinglifetechway.k4kotlin.toast
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsRequest
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity<LoginContract.View,
        LoginPresenter>(),
    LoginContract.View {

    private var defaultButtonWidth = 0
    var pref_setting : SharedPreferences? = null

    override var mPresenter: LoginPresenter
        get() = LoginPresenter(this)
        set(value) {}

    override fun layoutId(): Int = R.layout.activity_login

    override fun getTagClass(): String = javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pref_setting = PreferencesHelper.getSettingPref(this)

        layout_btn_login.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            override fun onGlobalLayout() {
                layout_btn_login.viewTreeObserver.removeOnGlobalLayoutListener(this)
                defaultButtonWidth = layout_btn_login.width
            }
        })

        methodWithPermissions()

        println("Login : "+pref_setting!!.getBoolean("is_login", false))
        if (pref_setting!!.getBoolean("is_login", false)) {
            AppRoute.open(this, MainActivity::class.java)
            finish()
        } else {
            init()
        }
    }

    fun init(){
        val runnable = Runnable {
            stopLoading()
            /*mPresenter.doLogin("","", "") {
                AppRoute.open(this@LoginActivity, "main")
                finish()
            }*/
        }

        chk_show_pwd.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //edit_password.inputType = InputType.TYPE_CLASS_TEXT
                edit_pwd.transformationMethod = null
            } else {
                //edit_password.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                edit_pwd.transformationMethod = PasswordTransformationMethod()
            }
        }

        btn_login.onClick {
            startLoading()
            //handler.postDelayed(runnable, 5000)
            mPresenter.doLogin(edit_username.text.toString(),edit_pwd.text.toString(), "") { it, err_message ->
                stopLoading()
                if (it) {
                    AppRoute.open(this@LoginActivity, MainActivity::class.java)
                    finish()
                } else {
                    toast(err_message)
                }
            }
        }
    }

    fun startLoading(){
        val loading = LoadingCircleAnim(layout_btn_login, layout_btn_login.height)
        loading.duration = 500
        loading.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                spin_kit.visible()
                btn_login.gone()
            }

        })
        layout_btn_login.startAnimation(loading)
    }

    fun stopLoading(){
        val loading = LoadingCircleAnim(layout_btn_login, defaultButtonWidth)
        loading.duration = 500
        loading.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                spin_kit.gone()
                btn_login.visible()
            }

            override fun onAnimationStart(animation: Animation?) {

            }

        })
        layout_btn_login.startAnimation(loading)
    }

    val quickPermissionsOption = QuickPermissionsOptions(
        false,
        "Custom rational message",
        true,
        "Custom permanently denied message",
        { rationaleCallback(it) },
        { permissionsPermanentlyDenied(it) }
    )

    fun methodWithPermissions() = runWithPermissions(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, options = quickPermissionsOption) {
        val editor = pref_setting!!.edit()
        editor.putBoolean("is_permited", true)
        editor.apply()
    }

    fun runTimePermission() = runWithPermissions(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE, options = quickPermissionsOption) {
    }

    private fun rationaleCallback(req: QuickPermissionsRequest) {
        // this will be called when permission is denied once or more time. Handle it your way
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom rationale dialog. Please allow us to proceed " + "asking for permissions again, or cancel to end the permission flow.")
            .setPositiveButton("Go Ahead") { dialog, which -> req.proceed() }
            .setNegativeButton("cancel") { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }

    private fun permissionsPermanentlyDenied(req: QuickPermissionsRequest) {
        // this will be called when some/all permissions required by the method are permanently
        // denied. Handle it your way.
        AlertDialog.Builder(this)
            .setTitle("Permissions Denied")
            .setMessage("This is the custom permissions permanently denied dialog. " +
                    "Please open app settings to open app settings for allowing permissions, " +
                    "or cancel to end the permission flow.")
            .setPositiveButton("App Settings") { dialog, which -> req.openAppSettings() }
            .setNegativeButton("Cancel") { dialog, which -> req.cancel() }
            .setCancelable(false)
            .show()
    }
}
