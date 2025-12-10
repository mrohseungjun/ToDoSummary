package com.oseungjun.todosummer.core.domain.di

import com.oseungjun.todosummer.core.domain.repository.TodoRepository
import com.oseungjun.todosummer.core.domain.usecase.TodoUseCases

/**
 * Domain 계층은 DI 프레임워크에 독립적으로 유지합니다.
 * Koin 모듈 정의는 app 계층에서 수행하고, 여기서는 팩토리 함수만 제공합니다.
 */
object DomainDI {
    fun provideTodoUseCases(repository: TodoRepository): TodoUseCases = TodoUseCases(repository)
}
