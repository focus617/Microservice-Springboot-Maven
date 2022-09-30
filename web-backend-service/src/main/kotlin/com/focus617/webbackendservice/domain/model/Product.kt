package com.focus617.webbackendservice.domain.model

import org.springframework.format.annotation.DateTimeFormat
import javax.persistence.*

@Table(name = "product")
@Entity
data class Product(
    @Id
    @SequenceGenerator(
        name = "product_id_sequence_generator",
        sequenceName = "product_id_sequence"
    )
    @GeneratedValue(
        strategy = GenerationType.IDENTITY,
        generator = "product_id_sequence_generator"
    )
    @Column(name = "id", nullable = false)
    var id: Int = 0,

    @Column(name = "code", nullable = false)
    var code: String = "",

    @Column
    var title: String = "",

    @Column
    var description: String = "",

    @Column
    var image: String = "",

    @Column
    var price: Double = 0.0,

    @Column
    @DateTimeFormat(pattern="yyyy-MM-dd")
    var expiringDate: String = "2099-12-31"
)
