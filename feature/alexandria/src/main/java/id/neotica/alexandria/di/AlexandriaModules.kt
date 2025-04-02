package id.neotica.alexandria.di

import id.neotica.alexandria.screen.book.BookViewModel
import id.neotica.alexandria.screen.library.LibraryViewModel
import id.neotica.alexandria.screen.user.UserLibraryViewModel
import id.neotica.alexandria.screen.user.editbook.EditBookViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val alexandriaModules = module {
    viewModelOf(::LibraryViewModel)
    viewModelOf(::BookViewModel)
    viewModelOf(::UserLibraryViewModel)
    viewModelOf(::EditBookViewModel)
}