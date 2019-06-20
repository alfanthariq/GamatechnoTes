package com.alfanthariq.skeleton.features.base

import android.content.Context
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar

/**
 * Base interface that any class that wants to act as a View in the MVP (Model View Presenter)
 * pattern must implement. Generally this interface will be extended by a more specific interface
 * that then usually will be implemented by an Activity or Fragment.
 */
interface BaseMvpView{

    fun getContext(): Context

    fun showError(error: String?)

    fun showSnackError(error: String?) : Snackbar

    fun showSnackMessage(message: String?) : Snackbar

    fun showMessage(message: String)

    fun showLoadingDialog(message: String?)

    fun hideLoadingDialog()

    fun setToolbar(mToolbar: Toolbar?, title: String?, setDisplayHomeAsUpEnabled: Boolean)
}