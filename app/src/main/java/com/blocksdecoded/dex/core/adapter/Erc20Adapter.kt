package com.blocksdecoded.dex.core.adapter

import android.content.Context
import com.blocksdecoded.dex.core.manager.fee.IFeeRateProvider
import com.blocksdecoded.dex.core.model.Coin
import com.blocksdecoded.dex.core.model.TransactionAddress
import com.blocksdecoded.dex.core.model.TransactionRecord
import io.horizontalsystems.erc20kit.core.Erc20Kit
import io.horizontalsystems.erc20kit.core.TransactionKey
import io.horizontalsystems.erc20kit.models.TransactionInfo
import io.horizontalsystems.ethereumkit.core.EthereumKit
import io.horizontalsystems.ethereumkit.core.hexStringToByteArray
import io.reactivex.Flowable
import io.reactivex.Single
import java.math.BigDecimal

class Erc20Adapter(
    coin: Coin,
    context: Context,
    kit: EthereumKit,
    feeRateProvider: IFeeRateProvider,
    decimal: Int,
    private val fee: BigDecimal,
    contractAddress: String,
    override val feeCoinCode: String?
) : EthereumBaseAdapter(coin, kit, feeRateProvider, decimal) {

    private val erc20Kit: Erc20Kit = Erc20Kit.getInstance(context, ethereumKit, contractAddress)

    override val state: AdapterState
        get() = when (erc20Kit.syncState) {
            is Erc20Kit.SyncState.Synced -> AdapterState.Synced
            is Erc20Kit.SyncState.NotSynced -> AdapterState.NotSynced
            is Erc20Kit.SyncState.Syncing -> AdapterState.Syncing(50, null)
        }

    override val stateUpdatedFlowable: Flowable<Unit>
        get() = erc20Kit.syncStateFlowable.map { Unit }

    override val balance: BigDecimal
        get() = balanceInBigDecimal(erc20Kit.balance, decimal)

    override val balanceUpdatedFlowable: Flowable<Unit>
        get() = erc20Kit.balanceFlowable.map { Unit }

    override fun getTransactions(from: Pair<String, Int>?, limit: Int): Single<List<TransactionRecord>> {
        return erc20Kit.transactions(from?.let { TransactionKey(it.first.hexStringToByteArray(), it.second) }, limit).map {
            it.map { tx -> transactionRecord(tx) }
        }
    }

    override val transactionRecordsFlowable: Flowable<List<TransactionRecord>>
        get() = erc20Kit.transactionsFlowable.map { it.map { tx -> transactionRecord(tx) } }

    override fun sendSingle(address: String, amount: String, gasPrice: Long): Single<Unit> {
        return erc20Kit.send(address, amount, gasPrice).map { Unit }
    }

    override fun availableBalance(address: String?, feePriority: FeeRatePriority): BigDecimal {
        return balance - fee
    }

    override fun fee(value: BigDecimal, address: String?, feePriority: FeeRatePriority): BigDecimal {
        return erc20Kit.fee(feeRateProvider.ethereumGasPrice(feePriority)).movePointLeft(18)
    }

    override fun validate(amount: BigDecimal, address: String?, feePriority: FeeRatePriority): List<SendStateError> {
        val errors = mutableListOf<SendStateError>()
        if (amount > availableBalance(address, feePriority)) {
            errors.add(SendStateError.InsufficientAmount)
        }
        if (balanceInBigDecimal(ethereumKit.balance, decimal) < fee(amount, address, feePriority)) {
            errors.add(SendStateError.InsufficientFeeBalance)
        }
        return errors
    }

    private fun transactionRecord(transaction: TransactionInfo): TransactionRecord {
        val mineAddress = ethereumKit.receiveAddress

        val from = TransactionAddress(transaction.from, transaction.from == mineAddress)
        val to = TransactionAddress(transaction.to, transaction.to == mineAddress)

        var amount: BigDecimal

        transaction.value.toBigDecimal().let {
            amount = it.movePointLeft(decimal)
            if (from.mine) {
                amount = -amount
            }
        }

        return TransactionRecord(
            transactionHash = transaction.transactionHash,
            transactionIndex = transaction.transactionIndex ?: 0,
            interTransactionIndex = transaction.interTransactionIndex,
            blockHeight = transaction.blockNumber,
            amount = amount,
            timestamp = transaction.timestamp,
            from = listOf(from),
            to = listOf(to)
        )
    }

    companion object {
        fun clear(context: Context) {
            Erc20Kit.clear(context)
        }
    }

}