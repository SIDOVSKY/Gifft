package com.gifft.gift.api

import java.util.*

abstract class Gift(
    open val uuid: String,
    open val sender: String,
    open val receiver: String,
    open val date: Date
)
