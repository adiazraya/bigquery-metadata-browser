package com.mercadolibre.incidenciabq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Table {
    private String tableId;
    private String datasetId;
    private String projectId;
    private String friendlyName;
    private String description;
    private String type;
    private Long creationTime;
    private Long numRows;
}






