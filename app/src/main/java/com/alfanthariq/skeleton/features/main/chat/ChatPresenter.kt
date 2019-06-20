package com.alfanthariq.skeleton.features.main.chat

import com.alfanthariq.skeleton.features.base.BasePresenterImpl

class ChatPresenter (var view: ChatContract.View) :
    BasePresenterImpl<ChatContract.View>(), ChatContract.Presenter {
}