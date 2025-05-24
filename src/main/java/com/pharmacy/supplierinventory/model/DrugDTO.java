// DrugDTO.java
package com.pharmacy.supplierinventory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DrugDTO {
    private String batchId;
    private String name;
    private String description;
    private int availableQuantity;
}
