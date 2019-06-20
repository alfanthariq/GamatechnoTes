package com.alfanthariq.skeleton.features.main.contact

import alfanthariq.com.signatureapp.util.PreferencesHelper
import com.alfanthariq.skeleton.data.local.AppDatabase
import com.alfanthariq.skeleton.data.model.Users
import com.alfanthariq.skeleton.data.remote.ApiClient
import com.alfanthariq.skeleton.data.remote.ApiService
import com.alfanthariq.skeleton.features.base.BasePresenterImpl
import com.alfanthariq.skeleton.utils.NetworkUtil
import com.google.gson.JsonIOException
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.ResponseBody
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactPresenter (var view: ContactContract.View) :
    BasePresenterImpl<ContactContract.View>(), ContactContract.Presenter {

    private val context = view.getContext()
    private val pref_setting = PreferencesHelper.getSettingPref(context)
    private val pref_profile = PreferencesHelper.getProfilePref(context)
    private var apiService: ApiService? = null
    private val db = AppDatabase.getInstance(context)!!

    override fun getData(page: Int, callback: (List<Users>?, Boolean, String) -> Unit) {
        apiService = ApiClient.getClient(context,pref_profile.getString("token", ""), NetworkUtil.useAPI)
            .create(ApiService::class.java)
        val sendApi = apiService?.getUsers(page)
        sendApi?.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback(null, false, "Request Failed")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val resp = response.body()!!.string()
                    val json: JsonObject = JsonParser().parse(resp).asJsonObject
                    val obj = json.get("gtfwResult").asJsonObject
                    val status = obj.get("status").asInt
                    println("Status $status")

                    if (status == 200) {
                        try {
                            val data: JsonObject = obj.get("data").asJsonObject
                            doAsync {
                                val users = data.get("list_user").asJsonArray
                                val userList = ArrayList<Users>()
                                users.forEach {
                                    val user = it.asJsonObject
                                    val userDB = Users()
                                    userDB.user_id = user.get("user_id").asInt
                                    userDB.user_key = user.get("user_key").asString
                                    userDB.user_name = user.get("user_name").asString
                                    userDB.user_username = user.get("user_username").asString
                                    userDB.user_email = user.get("user_email").asString
                                    userDB.user_photo = user.get("user_photo").asString
                                    userDB.user_location = if (user.get("user_location").isJsonNull) ""
                                    else user.get("user_location").asString
                                    userDB.user_flag = user.get("user_flag").asString
                                    userDB.user_distance = user.get("user_distance").asString
                                    userDB.user_online = user.get("user_online").asInt
                                    userList.add(userDB)
                                }

                                db.UserDAO().insertLists(userList)

                                uiThread {
                                    callback(userList, true, "")
                                }
                            }
                        } catch (e : JsonIOException) {

                        }
                    } else {
                        callback(null, false, obj.get("message").asString)
                    }
                } else {
                    callback(null, false, "Internal Server Error")
                }
            }
        })
    }

    override fun refreshData() {
        doAsync {
            val data = db.UserDAO().allUserOnly()

            uiThread {
                view.onRefreshData(data)
            }
        }
    }

    override fun search(keyword: String) {
        doAsync {
            val data = db.UserDAO().searchUserOnly(keyword)

            uiThread {
                view.onRefreshData(data)
            }
        }
    }
}