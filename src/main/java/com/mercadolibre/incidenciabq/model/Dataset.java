package com.mercadolibre.incidenciabq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
    private String datasetId;
    private String projectId;
    private String friendlyName;
    private String description;
    private String location;
    private Long creationTime;
}



