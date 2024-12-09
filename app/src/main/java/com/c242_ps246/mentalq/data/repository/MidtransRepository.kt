package com.c242_ps246.mentalq.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.c242_ps246.mentalq.data.remote.response.DataTransaction
import com.c242_ps246.mentalq.data.remote.response.DataTransactionStatus
import com.c242_ps246.mentalq.data.remote.retrofit.MidtransApiService

class MidtransRepository(
    private val midtransApiService: MidtransApiService
) {
    fun createTransaction(price: Int, itemId: String): LiveData<Result<DataTransaction>> =
        liveData {
            emit(Result.Loading)

            try {
                val response = midtransApiService.createTransaction(price, itemId)
                val dataTransaction = response.dataTransaction

                emit(Result.Success(dataTransaction))
            } catch (e: Exception) {
                emit(Result.Error("error: ${e.message}"))
            }
        }

    fun getTransactionStatus(orderId: String): LiveData<Result<DataTransactionStatus>> = liveData {
        emit(Result.Loading)

        try {
            val response = midtransApiService.getTransactionStatus(orderId)
            val dataTransaction = response.data

            emit(Result.Success(dataTransaction))
        } catch (e: Exception) {
            emit(Result.Error("error: ${e.message}"))
        }
    }

    fun cancelTransaction(orderId: String): LiveData<Result<DataTransactionStatus>> = liveData {
        emit(Result.Loading)

        try {
            val response = midtransApiService.cancelTransaction(orderId)
            val dataTransaction = response.data

            emit(Result.Success(dataTransaction))
        } catch (e: Exception) {
            emit(Result.Error("error: ${e.message}"))
        }
    }


}
