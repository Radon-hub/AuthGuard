package com.radon.authguard.core.di

interface Factory<T> {
    fun create():T
}