package id.neotica.chat.client

import id.neotica.domain.remote.ConversationRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val chatModules = module {
    single { ChatRealtimeClient(get(), get()) }
    singleOf(::ConversationRepositoryImpl).bind<ConversationRepository>()
}
