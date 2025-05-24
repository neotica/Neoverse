package id.neotica.alexandria.di

import id.neotica.alexandria.screen.admin.LibraryAdminViewModel
import id.neotica.alexandria.screen.admin.book.AdminBookViewModel
import id.neotica.alexandria.screen.admin.warning.detail.WarningDetailViewModel
import id.neotica.alexandria.screen.admin.warning.WarningHubViewModel
import id.neotica.alexandria.screen.book.BookViewModel
import id.neotica.alexandria.screen.book.content.BookContentViewModel
import id.neotica.alexandria.screen.library.LibraryViewModel
import id.neotica.alexandria.screen.user.UserLibraryViewModel
import id.neotica.alexandria.screen.user.addbook.AddBookViewModel
import id.neotica.alexandria.screen.user.editbook.EditBookViewModel
import id.neotica.alexandria.screen.user.editbook.chapter.EditChapterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val alexandriaModules = module {
    viewModelOf(::LibraryViewModel)
    viewModelOf(::BookViewModel)
    viewModelOf(::UserLibraryViewModel)
    viewModelOf(::EditBookViewModel)
    viewModelOf(::EditChapterViewModel)
    viewModelOf(::BookContentViewModel)
    viewModelOf(::AddBookViewModel)
    viewModelOf(::LibraryAdminViewModel)
    viewModelOf(::AdminBookViewModel)
    viewModelOf(::WarningHubViewModel)
    viewModelOf(::WarningDetailViewModel)
}