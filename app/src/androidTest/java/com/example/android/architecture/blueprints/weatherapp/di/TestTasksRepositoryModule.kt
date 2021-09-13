package com.example.android.architecture.blueprints.weatherapp.di

import com.example.android.architecture.blueprints.weatherapp.data.source.DefaultTasksRepository
import com.example.android.architecture.blueprints.weatherapp.data.source.FakeRepository
import com.example.android.architecture.blueprints.weatherapp.data.source.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * TasksRepository binding to use in tests.
 *
 * Hilt will inject a [FakeRepository] instead of a [DefaultTasksRepository].
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [TasksRepositoryModule::class]
)
abstract class TestTasksRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindRepository(repo: FakeRepository): TasksRepository
}
