package com.fgtit.model

import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Unique

@Entity
data class StaffDetailData(
    @Id
    var id: Long = 0,
    val department: String?,
    val staffAvatar:String?,
    val staffid: String?,
    val fullname:String?,
    val email: String?,
    val phone: String?,
    @Unique(onConflict = ConflictStrategy.REPLACE) val username: String?,

)