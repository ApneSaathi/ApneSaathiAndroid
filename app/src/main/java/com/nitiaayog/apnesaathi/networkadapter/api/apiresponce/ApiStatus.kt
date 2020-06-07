package com.nitiaayog.apnesaathi.networkadapter.api.apiresponce

abstract class ApiStatus {

    var status: String? = ""
        get() = field ?: "0"
        set(value) {
            field = value ?: ""
        }

    var message: String? = ""
        get() = field ?: "0"
        set(value) {
            field = value ?: ""
        }
}