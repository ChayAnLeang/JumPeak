package com.jp.jumpeak.data.repositoryImpl

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.jp.jumpeak.data.dao.CustomerDao
import com.jp.jumpeak.data.entity.Customer
import com.jp.jumpeak.data.repository.CustomerRepository
import com.jp.jumpeak.enums.Action
import javax.inject.Inject

class CustomerRepositoryImpl @Inject constructor (
    private val customerDao: CustomerDao
) : CustomerRepository {
    override suspend fun getById(id: Long): Result<Customer> {
        return runCatching { customerDao.getById(id) }
    }

    override fun getByName(name: String): LiveData<PagingData<Customer>>{
        return Pager(
            config = PagingConfig(pageSize = 20),
            pagingSourceFactory = { customerDao.getByName(name) }
        ).liveData
    }

    override suspend fun manage(customer: Customer,action: Action): Result<String> {
        return runCatching {
            when(action){
                Action.ADD -> customerDao.insert(customer)
                Action.EDIT -> customerDao.update(customer)
                Action.DELETE -> customerDao.delete(customer)
            }
            "Customer ${action.displayName}"
        }
    }
}