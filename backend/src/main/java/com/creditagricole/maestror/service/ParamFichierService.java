package com.creditagricole.maestror.service;

import com.creditagricole.maestror.entity.ParamFichier;
import com.creditagricole.maestror.repository.ParamFichierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service pour gérer la table PARAM_FICHIER
 * Cette table est alimentée automatiquement lors de l'intégration de fichiers en STAGING
 */
@Service
@Slf4j
public class ParamFichierService {

    private final ParamFichierRepository paramFichierRepository;

    @Autowired
    public ParamFichierService(ParamFichierRepository paramFichierRepository) {
        this.paramFichierRepository = paramFichierRepository;
    }

    /**
     * Crée une entrée PARAM_FICHIER lors de la réception d'un fichier
     * 
     * @param nomFichier Nom du fichier uploadé
     * @return L'entité ParamFichier créée avec son ID
     */
    @Transactional
    public ParamFichier enregistrerReceptionFichier(String nomFichier) {
        log.info("Enregistrement de la réception du fichier: {}", nomFichier);
        
        LocalDateTime now = LocalDateTime.now();
        
        ParamFichier paramFichier = ParamFichier.builder()
                .nomFichier(nomFichier)
                .dateReception(now)
                .dateIntegrationStg(now)
                .build();
        
        ParamFichier saved = paramFichierRepository.save(paramFichier);
        log.info("Fichier enregistré avec ID_FICHIER: {}", saved.getIdFichier());
        
        return saved;
    }

    /**
     * Met à jour la date d'intégration STAGING
     * 
     * @param idFichier ID du fichier
     */
    @Transactional
    public void mettreAJourDateIntegration(Long idFichier) {
        log.info("Mise à jour de la date d'intégration pour ID_FICHIER: {}", idFichier);
        
        paramFichierRepository.findById(idFichier).ifPresent(pf -> {
            pf.setDateIntegrationStg(LocalDateTime.now());
            paramFichierRepository.save(pf);
            log.debug("Date d'intégration mise à jour pour: {}", pf.getNomFichier());
        });
    }

    /**
     * Récupère un fichier par son ID
     * 
     * @param idFichier ID du fichier
     * @return L'entité ParamFichier
     * @throws IllegalArgumentException si le fichier n'existe pas
     */
    public ParamFichier getFichierById(Long idFichier) {
        return paramFichierRepository.findById(idFichier)
                .orElseThrow(() -> new IllegalArgumentException("Fichier non trouvé avec ID: " + idFichier));
    }
}
