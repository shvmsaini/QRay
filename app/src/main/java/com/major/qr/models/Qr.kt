package com.major.qr.models

data class Qr(
    var lastSeen: String? = null,
    var docs: ArrayList<Doc>? = null,
    var sessionName: String? = null,
    var creationDate: String? = null,
    var sessionValidTime: String? = null,
    var qrId: String? = null,
    var token: String? = null
)