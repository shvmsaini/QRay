package com.major.qr.models

data class Doc(
    var documentType: String? = null,
    var documentReference: String? = null,
    var documentId: String? = null,
    var docLink: String? = null,
) {
    constructor(
        documentType: String? = null,
        documentReference: String? = null,
        documentId: String? = null,
    ) : this(documentType, documentReference, documentId, null)
}