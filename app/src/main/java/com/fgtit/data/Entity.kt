package com.fgtit.data

data class Entity(
    val birthdate: BirthdateX,
    val by_staff: ByStaffX,
    val createdAt: String,
    val email: String,
    val enrolled_date: EnrolledDateX,
    val fac_a_department: String,
    val fullname: String,
    val gender: String,
    val left_fingerprint: String,
    val left_inpsize: String,
    val mobile: String,
    val nfc_card_code: String,
    val objectId: String,
    val password: String,
    val right_fingerprint: String,
    val right_inpsize: String,
    val staff_category: String,
    val staff_image: StaffImageX,
    val status: String,
    val unique_id_no: String,
    val updatedAt: String,
    val username: String,
    val uuid: String,
    val work_hour: String,
    val work_position: String
)