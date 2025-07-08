package com.projectfyp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerProduce {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String mobileNumber;
    private String email;

    private String productName;
    private double productQuantity;
    private double productPrice;
    private String province;
    private String city;

    private String productImage;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

}
