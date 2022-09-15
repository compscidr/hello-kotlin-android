package com.example.myapplication

import com.example.lib.MyClass
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideTestComponent(): TestComponent {
        return TestComponent()
    }

    @Provides
    @Singleton
    fun provideMyClass(): MyClass {
        return MyClass()
    }
}
