package com.alfanthariq.skeleton.features.main.chat

import alfanthariq.com.signatureapp.util.PreferencesHelper
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alfanthariq.skeleton.R
import com.alfanthariq.skeleton.data.model.Messages
import com.alfanthariq.skeleton.utils.DateOperationUtil
import com.alfanthariq.skeleton.utils.DateOperationUtil.getDateFromString
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class MessageAdapter (private var contents: ArrayList<Messages>,
                      private val context: Context,
                      private val clickListener: (Messages) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val pref_profile = PreferencesHelper.getProfilePref(context)
    private var show_tanggal = true

    private val TYPE_INCOMING = 0
    private val TYPE_NORMAL_INCOMING = 1
    private val TYPE_OUTCOMING = 2
    private val TYPE_NORMAL_OUTCOMING = 3
    private val TYPE_INFO = 4

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        lateinit var view: View
        when (viewType) {
            TYPE_INCOMING -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_incoming, parent, false)
                return object : IncomingHolder(view) {

                }
            }
            TYPE_OUTCOMING -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_outgoing, parent, false)
                return object : OutcomingHolder(view) {

                }
            }
            TYPE_NORMAL_INCOMING -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_normal_income, parent, false)
                return object : IncomingHolder(view) {

                }
            }
            TYPE_NORMAL_OUTCOMING -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_normal_outgo, parent, false)
                return object : OutcomingHolder(view) {

                }
            }
            TYPE_INFO -> {
                view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.chat_info, parent, false)
                return object : OutcomingHolder(view) {

                }
            }
            else -> return IncomingHolder(view)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val sender_id = contents[position].sender_id
        val user_id = pref_profile.getInt("user_id", -1)
        var sender_id_prev = 0
        if (position > 0) {
            sender_id_prev = contents[position - 1].sender_id
        }

        if (position < contents.size - 1) {
            val msg_date_next = getDateFromString(contents[position + 1].message_time)
            val msg_date_curr = getDateFromString(contents[position].message_time)
            val d1 = SimpleDateFormat("yyyy-MM-dd").format(msg_date_next)
            val d2 = SimpleDateFormat("yyyy-MM-dd").format(msg_date_curr)
            show_tanggal = d1 != d2
        } else {
            show_tanggal = true
        }

        return if (sender_id == user_id) {
            if (sender_id == sender_id_prev) {
                TYPE_NORMAL_OUTCOMING
            } else {
                TYPE_OUTCOMING
            }
        } else {
            if (sender_id == sender_id_prev) {
                TYPE_NORMAL_INCOMING
            } else {
                TYPE_INCOMING
            }
        }
    }

    override fun getItemCount(): Int = contents.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context
        var txt = ""
        if (holder is IncomingHolder) {
            txt = contents[position].message
            holder.txt_message.text = txt

            holder.txt_nama.text = ""

            txt = contents[position].message_time
            val msg_date = getDateFromString(txt)
            holder.txt_msg_date.text = SimpleDateFormat("HH:mm").format(msg_date)

            if (position == contents.size - 1) {
                holder.container.setPadding(
                    holder.container.paddingLeft,
                    10,
                    holder.container.paddingRight,
                    holder.container.paddingBottom
                )
            }

            if (show_tanggal) {
                txt = contents[position].message_time
                val msg_datex = getDateFromString(txt)
                holder.container_tgl.visibility = View.VISIBLE
                holder.txt_tanggal.text = SimpleDateFormat("dd MMMM yyyy").format(msg_datex)
            } else {
                holder.container_tgl.visibility = View.GONE
            }
        } else if (holder is OutcomingHolder) {
            var txt: String? = ""

            txt = contents[position].message
            holder.txt_message.text = txt

            txt = contents[position].message_time
            val msg_date = getDateFromString(txt)
            val tgl = SimpleDateFormat("HH:mm").format(msg_date)
            holder.txt_msg_date.text = tgl

            if (position == contents.size - 1) {
                holder.container.setPadding(
                    holder.container.paddingLeft,
                    10,
                    holder.container.paddingRight,
                    holder.container.paddingBottom
                )
            }

            if (show_tanggal) {
                txt = contents[position].message_time
                val msg_datex = getDateFromString(txt)
                holder.container_tgl.visibility = View.VISIBLE
                holder.txt_tanggal.text = SimpleDateFormat("dd MMMM yyyy").format(msg_datex)
            } else {
                holder.container_tgl.visibility = View.GONE
            }
        }
    }

    open class IncomingHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_message: TextView
        var txt_msg_date: TextView
        var txt_nama: TextView
        var txt_tanggal: TextView
        var container: RelativeLayout
        var container_tgl: RelativeLayout
        var msg_container: LinearLayout

        init {
            txt_message = view.findViewById(R.id.txt_message)
            txt_msg_date = view.findViewById(R.id.txt_msg_date)
            txt_nama = view.findViewById(R.id.txt_nama)
            txt_tanggal = view.findViewById(R.id.txt_tanggal)

            container = view.findViewById(R.id.container)
            msg_container = view.findViewById(R.id.msg_container)
            container_tgl = view.findViewById(R.id.container_tgl)
        }
    }

    open class OutcomingHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txt_message: TextView
        var txt_msg_date: TextView
        var txt_tanggal: TextView
        var container: RelativeLayout
        var container_tgl: RelativeLayout
        var msg_container: LinearLayout

        init {

            txt_message = view.findViewById(R.id.txt_message)
            txt_msg_date = view.findViewById(R.id.txt_msg_date)
            txt_tanggal = view.findViewById(R.id.txt_tanggal)

            container = view.findViewById(R.id.container)
            msg_container = view.findViewById(R.id.msg_container)
            container_tgl = view.findViewById(R.id.container_tgl)
        }
    }
}