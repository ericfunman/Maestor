#!/usr/bin/env python3
import openpyxl
import os

# Lire le fichier Excel
file_path = os.path.join(os.path.dirname(__file__), 'Modeles_Mappings.xlsx')
wb = openpyxl.load_workbook(file_path)
ws = wb['MODELE_STAGING']

# Afficher toutes les lignes
print("=" * 150)
print("CONTENU DE L'ONGLET MODELE_STAGING")
print("=" * 150)

for row_idx, row in enumerate(ws.iter_rows(min_row=1, values_only=True), 1):
    # Filtrer les None
    filtered_row = [str(cell) if cell is not None else '' for cell in row]
    print(" | ".join(filtered_row))
    if row_idx >= 100:
        break

print("=" * 150)
