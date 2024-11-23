package org.sopt.and4ever.data.service

import org.sopt.and4ever.domain.model.MyPingList
import retrofit2.http.GET

interface OtherPingService {
    @GET
    suspend fun getOtherPingList() : MyPingList
}