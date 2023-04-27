package com.example.hwada.di

import android.app.Application
import com.example.hwada.application.App
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModules {
    @Singleton
    @Provides fun provideFireStoreInstance () = FirebaseFirestore.getInstance()

    @Provides fun providesMyApplicationInstance(application: Application) : App = application as App

}