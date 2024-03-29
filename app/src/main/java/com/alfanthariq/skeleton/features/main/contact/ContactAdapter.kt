package com.alfanthariq.skeleton.features.main.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.utils.GlideApp
import com.livinglifetechway.k4kotlin.onClick
import kotlinx.android.synthetic.main.item_contact.view.*
import kotlinx.android.synthetic.main.item_loading.view.*

class ContactAdapter (private var detail: ArrayList<Users?>,
                      private val clickListener: (Users) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var db : AppDatabase
    private val VIEW_TYPE_LOADING = 1
    private val VIEW_TYPE_ITEM = 0

    fun addLoading() {
        detail.add(null)
        notifyItemInserted(detail.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val vh : RecyclerView.ViewHolder
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
            db = AppDatabase.getInstance(v.context)!!
            vh = MainHolder(v, db)

            vh
        } else {
            LoadingHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false))
        }
    }

    override fun getItemCount(): Int = detail.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MainHolder){
            holder.bind(detail[position]!!, clickListener)
        } else if (holder is LoadingHolder){
            holder.showLoading()
        }
    }

    override fun getItemViewType(position: Int): Int {
        var viewType = 0
        if (detail[position] == null) {
            viewType = VIEW_TYPE_LOADING
        } else {
            viewType = VIEW_TYPE_ITEM
        }
        return viewType
    }

    class LoadingHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun showLoading(){
            view.progressBar.visibility = View.VISIBLE
        }
    }

    class MainHolder(private val view: View, private val db : AppDatabase) : RecyclerView.ViewHolder(view) {
        fun bind(data: Users, listener: (Users) -> Unit) {
            view.txt_username.text = data.user_name
            view.txt_location.text = data.user_location
            view.txt_email.text = data.user_email

            view.container.onClick {
                listener(data)
            }

            GlideApp.with(view.context)
                .load(data.user_photo)
                .error(R.mipmap.ic_user)
                .into(view.img_profpic)
        }
    }
}