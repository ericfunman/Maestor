package com.creditagricole.maestror.repository;

import com.creditagricole.maestror.entity.ParamFichier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour la table PARAM_FICHIER
 */
@Repository
public interface ParamFichierRepository extends JpaRepository<ParamFichier, Long> {
    
    /**
     * Recherche un fichier par son nom
     */
    Optional<ParamFichier> findByNomFichier(String nomFichier);
}
