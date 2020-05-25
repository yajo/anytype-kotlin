package com.agileburo.anytype.domain.page

import com.agileburo.anytype.domain.base.BaseUseCase
import com.agileburo.anytype.domain.block.model.Command
import com.agileburo.anytype.domain.block.repo.BlockRepository
import com.agileburo.anytype.domain.common.Id
import com.agileburo.anytype.domain.event.model.Payload

/**
 * Use-case for re-doing latest changes in document.
 */
class Redo(
    private val repo: BlockRepository
) : BaseUseCase<Payload, Redo.Params>() {

    override suspend fun run(params: Params) = safe {
        repo.redo(
            command = Command.Redo(
                context = params.context
            )
        )
    }

    /**
     * Params for redoing latest changes in document.
     * @property context id of the context
     */
    data class Params(
        val context: Id
    )
}