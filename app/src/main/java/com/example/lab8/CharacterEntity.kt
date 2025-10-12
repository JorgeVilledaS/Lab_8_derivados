package com.example.lab8

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val image: String
)