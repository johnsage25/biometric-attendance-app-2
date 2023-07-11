package com.fgtit.model

import io.objectbox.annotation.*
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*

@Entity
data class Attendance(
    @Id
    var id: Long = 0,
    var staff_objectId: String? = "",
    var enrol_id: Long = 0,
    var timestamp_date: String? = "",
    var capture_type: String? = "",
    var staff_uuid: String? = "",
    var location: String? = "",
    var timestamp: String? = "",
    var time: String? = "",
    var attns_type: String? = "",
    var queue: Boolean? = false,
    var device_admin: String? = null,
    @Unique(onConflict = ConflictStrategy.REPLACE) val uid: String? = null,
)