package com.creditagricole.maestror.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private String name;
    private String description;
    private String sqlQuery;
    private String chartType;
    private String xAxisColumn;
    private String yAxisColumn;
}
