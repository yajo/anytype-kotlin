package com.anytypeio.anytype.data.auth.repo

import com.anytypeio.anytype.data.auth.mapper.toDomain
import com.anytypeio.anytype.data.auth.mapper.toEntity
import com.anytypeio.anytype.data.auth.repo.config.Configurator
import com.anytypeio.anytype.domain.auth.model.Account
import com.anytypeio.anytype.domain.auth.model.Wallet
import com.anytypeio.anytype.domain.auth.repo.AuthRepository
import com.anytypeio.anytype.core_models.FlavourConfig
import kotlinx.coroutines.flow.map

class AuthDataRepository(
    private val factory: AuthDataStoreFactory,
    private val configurator: Configurator
) : AuthRepository {

    override suspend fun startAccount(
        id: String, path: String
    ): Pair<Account, FlavourConfig> = factory.remote.startAccount(id, path).let { pair ->
        Pair(
            first = pair.first.toDomain(),
            second = pair.second.toDomain()
        )
    }

    override suspend fun createAccount(
        name: String,
        avatarPath: String?,
        invitationCode: String
    ): Account = factory.remote.createAccount(name, avatarPath, invitationCode).toDomain()

    override suspend fun startLoadingAccounts() {
        factory.remote.recoverAccount()
    }

    override suspend fun saveAccount(account: Account) {
        factory.cache.saveAccount(account.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        factory.cache.updateAccount(account.toEntity())
    }

    override fun observeAccounts() = factory.remote.observeAccounts().map { it.toDomain() }

    override suspend fun createWallet(
        path: String
    ): Wallet = factory.remote.createWallet(path).toDomain()

    override suspend fun convertWallet(entropy: String): String =
        factory.remote.convertWallet(entropy)

    override suspend fun recoverWallet(path: String, mnemonic: String) {
        factory.remote.recoverWallet(path, mnemonic)
    }

    override suspend fun getCurrentAccount() = factory.cache.getCurrentAccount().toDomain()

    override suspend fun getCurrentAccountId() = factory.cache.getCurrentAccountId()

    override suspend fun saveMnemonic(
        mnemonic: String
    ) = factory.cache.saveMnemonic(mnemonic)

    override suspend fun getMnemonic() = factory.cache.getMnemonic()

    override suspend fun logout() {
        configurator.release()
        factory.remote.logout()
        factory.cache.logout()
    }

    override suspend fun getAccounts() = factory.cache.getAccounts().map { it.toDomain() }

    override suspend fun setCurrentAccount(id: String) {
        factory.cache.setCurrentAccount(id)
    }

    override suspend fun getVersion(): String = factory.remote.getVersion()
}