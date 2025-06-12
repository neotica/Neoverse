package id.neotica.alexandria.di

import id.neotica.alexandria.data.BookRepositoryImpl
import id.neotica.alexandria.data.ChapterRepositoryImpl
import id.neotica.alexandria.data.ContentRepositoryImpl
import id.neotica.alexandria.data.PermissionRepositoryImpl
import id.neotica.alexandria.data.admin.WarningRepositoryImpl
import id.neotica.alexandria.domain.BookRepository
import id.neotica.alexandria.domain.ChapterRepository
import id.neotica.alexandria.domain.ContentRepository
import id.neotica.alexandria.domain.PermissionRepository
import id.neotica.alexandria.domain.admin.WarningRepository
import id.neotica.alexandria.screen.admin.AdminViewModel
import id.neotica.alexandria.screen.admin.library.AdminLibraryViewModel
import id.neotica.alexandria.screen.admin.library.book.AdminBookViewModel
import id.neotica.alexandria.screen.admin.warning.detail.WarningDetailViewModel
import id.neotica.alexandria.screen.admin.warning.WarningHubViewModel
import id.neotica.alexandria.screen.book.BookViewModel
import id.neotica.alexandria.screen.book.content.BookContentViewModel
import id.neotica.alexandria.screen.library.LibraryViewModel
import id.neotica.alexandria.screen.user.UserLibraryViewModel
import id.neotica.alexandria.screen.user.addbook.AddBookViewModel
import id.neotica.alexandria.screen.user.editbook.EditBookViewModel
import id.neotica.alexandria.screen.user.editbook.chapter.EditChapterViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alexandriaModules = module {
    viewModelOf(::LibraryViewModel)
    viewModelOf(::BookViewModel)
    viewModelOf(::UserLibraryViewModel)
    viewModelOf(::EditBookViewModel)
    viewModelOf(::EditChapterViewModel)
    viewModelOf(::BookContentViewModel)
    viewModelOf(::AddBookViewModel)
    viewModelOf(::AdminLibraryViewModel)
    viewModelOf(::AdminBookViewModel)
    viewModelOf(::WarningHubViewModel)
    viewModelOf(::WarningDetailViewModel)
    viewModelOf(::AdminViewModel)
    singleOf(::BookRepositoryImpl).bind(BookRepository::class)
    singleOf(::ChapterRepositoryImpl).bind(ChapterRepository::class)
    singleOf(::ContentRepositoryImpl).bind(ContentRepository::class)
    singleOf(::WarningRepositoryImpl).bind(WarningRepository::class)
    singleOf(::PermissionRepositoryImpl).bind(PermissionRepository::class)
}