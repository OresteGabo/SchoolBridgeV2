package com.schoolbridge.v2.data.session

import com.schoolbridge.v2.domain.user.CurrentUser

sealed class SessionState {
    object Loading : SessionState()
    data class LoggedIn(val user: CurrentUser) : SessionState()
    object LoggedOut : SessionState()
}