package com.flexedev.twobirds_onescone.helper

class PermissionsRequest(
    val needsWriteSettings: Boolean,
    val onSuccess: () -> Unit,
    val onFailure: () -> Unit
)