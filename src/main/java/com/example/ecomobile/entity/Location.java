package com.example.ecomobile.entity;

import com.example.ecomobile.base.BaseEntity;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Location extends BaseEntity {
    private String region;
    private String district;
    private String street;
    private String home;

    @Override
    public String toString(){
        return region +"\t"+district+"\t"+street+"\t" + home;
    }
}
