package com.invictoprojects.streetlyshop.util

import org.mockito.ArgumentCaptor
import org.mockito.Mockito

fun <T> any(): T = Mockito.any()

fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
