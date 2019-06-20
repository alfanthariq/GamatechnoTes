package com.alfanthariq.skeleton.features.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.alfanthariq.skeleton.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity<in V : BaseMvpView, T : BasePresenter<V>>
    : AppCompatActivity(), BaseMvpView {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        mPresenter.attachView(this as V)
        Log.v(getTagClass(), "onCreate")
    }

    override fun onResume() {
        super.onResume()
        Log.v(getTagClass(), "onResume")
    }

    override fun onStart() {
        super.onStart()
        Log.v(getTagClass(), "onStart")
    }

    protected abstract var mPresenter: T

    @LayoutRes
    abstract fun layoutId(): Int

    abstract fun getTagClass() : String

    private var pDialog: Dialog? = null

    override fun showLoadingDialog(message: String?) {
        if (pDialog == null) pDialog = Dialog(this)

        pDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        pDialog?.setContentView(R.layout.dialog_loading)
        pDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val progressBar = pDialog?.findViewById(R.id.progressBar) as ProgressBar
        val msg = pDialog?.findViewById(R.id.txt_message) as TextView
        if (message != null) msg.text = message
        else msg.text = getString(R.string.loading)
        pDialog?.setCancelable(false)
        pDialog?.show()
    }

    override fun hideLoadingDialog() {
        if (pDialog != null && pDialog?.isShowing!!) {
            pDialog?.dismiss(); pDialog = null
        }
    }

    override fun getContext(): Context = this

    override fun showError(error: String?) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun showSnackError(error: String?) : Snackbar {
        val error2 = if (error.isNullOrEmpty()) "" else error
        val view:View = findViewById(android.R.id.content)
        val snackbar : Snackbar = Snackbar.make(view, error2.toString(), Snackbar.LENGTH_INDEFINITE); return snackbar
    }

    override fun showSnackMessage(message: String?) : Snackbar {
        val message2 = if (message.isNullOrEmpty()) "" else message
        val view:View = findViewById(android.R.id.content)
        val snackbar : Snackbar = Snackbar.make(view, message2.toString(), Snackbar.LENGTH_LONG); return snackbar
    }

    override fun setToolbar(mToolbar: Toolbar?, title: String?, setDisplayHomeAsUpEnabled: Boolean) {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar)
            supportActionBar?.setDisplayShowTitleEnabled(true)
            if (title != null) {
                supportActionBar?.title = title
            }
            supportActionBar?.setDisplayHomeAsUpEnabled(setDisplayHomeAsUpEnabled)
            supportActionBar?.setHomeButtonEnabled(setDisplayHomeAsUpEnabled)
        }
    }

    override fun onDestroy() {
        hideLoadingDialog()
        super.onDestroy()
        Log.v(getTagClass(), "onDestroy")
        mPresenter.detachView()
    }

}