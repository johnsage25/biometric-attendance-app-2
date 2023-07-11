package com.fgtit.model

import io.objectbox.annotation.*
import java.util.*

@Entity
data class EnrolmentModel(
    @Id()
    var id: Long = 0,
    @Unique(onConflict = ConflictStrategy.REPLACE) var uuid: String? = null,
    var fullname: String? = null,
    var birthdate: Date? = null,
    var fac_a_department: String? = null,
    var work_position: String? = null,
    var staff_category: String? = null,
    var gender: String? = null,
    var unique_id_no: String? = null,
    var username: String? = null,
    var password: String? = null,
    var email: String? = null,
    var mobile: String? = null,
    var left_fingerprint: String? = null,
    var left_inpsize: Int? = null,
    var right_inpsize: Int? = null,
    var right_fingerprint: String? = null,
    var nfc_card_code: String? = null,
    var enrolled_date: Date? = null,
    var image: String? = null,
    val by_staff: String? = null,
    var objectId: String,
    val work_hour: String?= null
)