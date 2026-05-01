package com.example.dailynotifications.backend.entity

import com.example.dailynotifications.backend.util.ReminderFrequency
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "reminders")
class ReminderEntity(
    @Id
    @Column(nullable = false, updatable = false)
    var id: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserEntity? = null,
    @Column(nullable = false)
    var title: String = "",
    @Column(name = "date_time", nullable = false)
    var dateTime: LocalDateTime = LocalDateTime.now(),
    @Column
    var note: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var frequency: ReminderFrequency = ReminderFrequency.ONE_TIME,
)
