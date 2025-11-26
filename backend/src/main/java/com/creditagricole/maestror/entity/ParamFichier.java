package com.creditagricole.maestror.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité représentant la table PARAM_FICHIER
 * Cette table stocke les métadonnées des fichiers intégrés en STAGING
 */
@Entity
@Table(name = "\"PARAM_FICHIER\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamFichier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"ID_FICHIER\"")
    private Long idFichier;

    @Column(name = "\"NOM_FICHIER\"", nullable = false, length = 100)
    private String nomFichier;

    @Column(name = "\"DATE_RECEPTION\"", nullable = false)
    private LocalDateTime dateReception;

    @Column(name = "\"DATE_INTEGRATION_STG\"", nullable = false)
    private LocalDateTime dateIntegrationStg;
}
