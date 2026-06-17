package com.jp.jumpeak.data.repositoryImpl

import androidx.lifecycle.LiveData
import com.jp.jumpeak.data.dao.PaymentDao
import com.jp.jumpeak.data.entity.Payment
import com.jp.jumpeak.data.repository.PaymentRepository
import com.jp.jumpeak.enums.Action
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val paymentDao: PaymentDao): PaymentRepository {
    override suspend fun getById(id: Long): Result<Payment> {
        return runCatching { paymentDao.getById(id) }
    }

    override fun getByInvoiceId(invoiceId: Long): LiveData<List<Payment>> {
        return paymentDao.getByInvoiceId(invoiceId)
    }

    override suspend fun manage(payment: Payment,action: Action): Result<String> {
        return runCatching {
            when(action){
                Action.ADD -> paymentDao.insert(payment)
                Action.EDIT -> paymentDao.update(payment)
                Action.DELETE -> paymentDao.delete(payment)
            }
            "Payment ${action.displayName}"
        }
    }
}