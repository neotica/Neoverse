package id.neotica.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("currentuser")
class CurrentUserEntity (
    @field:ColumnInfo("id")
    @field:PrimaryKey
    val id: String = "",
    @field:ColumnInfo("username")
    val username: String = "",
    @field:ColumnInfo("email")
    val email: String = "",
    @field:ColumnInfo("status")
    val status: String = "",
    @field:ColumnInfo("imageUrl")
    val imageUrl: String = ""
)