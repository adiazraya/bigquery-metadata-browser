package com.mercadolibre.incidenciabq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {
    private String name;
    private String type;
    private String mode;         // NULLABLE, REQUIRED, REPEATED
    private String description;
}


