# CAHIER DES CHARGES - RISKSCORE PRO
## Plateforme SaaS d'Analyse du Risque Crédit B2B

═══════════════════════════════════════════════════════════════════════════════
**Référence**: CDC-RISK-003-FINAL-MICROSERVICES  
**Version**: 3.0 Finale (Votre Document Original + Améliorations)  
**Date**: Décembre 2024  
**Étudiant**: [Votre Nom]  
**Encadrant**: [Nom Encadrant]  
═══════════════════════════════════════════════════════════════════════════════

## 📋 TABLE DES MATIÈRES

**1. INTRODUCTION ET CONTEXTE**
- 1.1 Problématique Métier
- 1.2 Objectif Général
- 1.3 Bénéfices Attendus

**2. BESOINS FONCTIONNELS**
- 2.1 Gestion Multi-Client (Multi-Tenancy)
- 2.2 Analyse et Scoring Automatique ⭐ DÉTAILLÉ
- 2.3 Moteur de Décision et Recommandations
- 2.4 Tableau de Bord et Suivi
- 2.5 Système d'Alerte et Monitoring
- 2.6 API et Intégrations

**3. RÈGLES MÉTIER**
- 3.1 Pondération du Score
- 3.2 Ajustements Sectoriels
- 3.3 Seuils de Décision
- 3.4 ⭐ Algorithme de Scoring Détaillé (NOUVEAU)
- 3.5 ⭐ Gestion des Cas Limites (NOUVEAU)

**4. BESOINS TECHNIQUES ET ARCHITECTURE**
- 4.1 Architecture Microservices ⭐ COMME VOUS VOULEZ
- 4.2 Stack Technologique
- 4.3 Modèle de Données
- 4.4 Contraintes Techniques

**5. LIVRABLES ET PLANIFICATION**
- 5.1 Livrables Attendus
- 5.2 Planning Prévisionnel (14 semaines)
- 5.3 ⭐ Gestion des Risques (NOUVEAU)

**6. ⭐ SCÉNARIOS DE DÉMONSTRATION** (NOUVEAU)

**7. CRITÈRES D'ACCEPTATION**

**ANNEXES**
- Annexe A: ⭐ Liste des 15 Ratios Financiers (DÉTAILLÉ)
- Annexe B: ⭐ Matrice de Recommandations (DÉTAILLÉ)
- Annexe C: Glossaire

⭐ = Sections ajoutées/améliorées par rapport à votre document original

---

# 1. INTRODUCTION ET CONTEXTE

## 1.1 Problématique Métier

Les entreprises marocaines, particulièrement les PME, perdent en moyenne **12 milliards de MAD par an** en créances impayées. Le délai moyen de paiement dépasse **90 jours**, impactant gravement leur trésorerie. L'évaluation du risque client reste majoritairement manuelle, subjective et non standardisée, conduisant à des décisions incohérentes.

### Impacts Mesurables

| Problème | Impact Chiffré |
|----------|----------------|
| **Pertes annuelles** | 12 milliards MAD au Maroc |
| **Délai de paiement** | 90 jours (vs 30 contractuel) |
| **Temps d'analyse** | 3-5 jours par dossier |
| **Coût par analyse** | 500 MAD en moyenne |
| **PME en difficulté** | 40% à cause des retards de paiement |
| **Faillites** | 15% causées par impayés |

---

## 1.2 Objectif Général

Développer une **plateforme SaaS** permettant aux entreprises d'automatiser l'analyse du risque financier de leurs partenaires B2B (clients, fournisseurs), de générer un **score de risque objectif sur 100**, et de recevoir des **recommandations décisionnelles claires** pour chaque transaction à crédit.

### Objectifs Principaux

```
┌─────────────────────────────────────────────────────────────────┐
│ 1. AUTOMATISER l'analyse du risque financier                   │
│    → Calcul en < 30 secondes (vs 3-5 jours actuellement)      │
│                                                                 │
│ 2. GÉNÉRER un score standardisé sur 100 points                 │
│    → Basé sur 15 ratios financiers clés                        │
│    → Pondération: 40% santé + 35% paiement + 25% contexte     │
│                                                                 │
│ 3. FOURNIR des recommandations actionnables                    │
│    → Montant maximum, délai, garanties requis                  │
│    → Justification claire et traçable                          │
│                                                                 │
│ 4. MONITORER en continu l'évolution du portefeuille           │
│    → Alertes automatiques (email + SMS)                        │
│    → Dashboard temps réel                                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 1.3 Bénéfices Attendus

### Bénéfices Quantifiables

| Indicateur | Avant | Après | Gain |
|------------|-------|-------|------|
| **Temps d'analyse** | 3-5 jours | < 30 secondes | **-99%** |
| **Coût par analyse** | 500 MAD | 50 MAD | **-90%** |
| **Taux d'impayés** | 8-12% | 5-7% | **-40%** |
| **Pertes annuelles** | 1 000 000 MAD | 600 000 MAD | **-40%** |

### ROI Estimé

**Pour une PME avec 100 clients**:
- Coût plateforme: 3 000 MAD/mois
- Économies: 25 000 MAD/mois
- **ROI: 733% annuel** 🎯

---

# 2. BESOINS FONCTIONNELS

## 2.1 Gestion Multi-Client (Multi-Tenancy)

### F-01.01: Inscription en ligne d'une nouvelle entreprise cliente (Tenant)

**Description**: Permettre à une entreprise de créer son compte et son espace isolé.

**Données requises**:
- Nom entreprise cliente
- Email administrateur
- Secteur d'activité
- Plan d'abonnement (STARTER / BUSINESS / ENTERPRISE)

**Processus**:
1. Remplissage formulaire inscription
2. Validation email
3. Création tenant (base de données dédiée)
4. Configuration profil avec logo et couleurs
5. Activation compte

### F-01.02: Profil client avec logo, couleurs, paramètres spécifiques

**Personnalisation**:
- Upload logo entreprise
- Choix couleurs primaire/secondaire (thème)
- Configuration seuils de scoring spécifiques
- Paramétrage alertes
- Gestion utilisateurs internes

### F-01.03: Isolation stricte des données

**Garanties**:
- ✅ Base de données séparée par tenant
- ✅ Impossible d'accéder aux données d'un autre client
- ✅ Chiffrement des données sensibles
- ✅ Logs d'audit complets

### F-01.04: Plans d'abonnement différenciés

| Plan | Prix/mois | Utilisateurs | Entreprises | Analyses/mois |
|------|-----------|--------------|-------------|---------------|
| **STARTER** | 1 500 MAD | 2 | 50 | 100 |
| **BUSINESS** | 3 000 MAD | 5 | 200 | 500 |
| **ENTERPRISE** | Sur devis | Illimité | Illimité | Illimité |

---

## 2.2 Analyse et Scoring Automatique

### F-02.01: Saisie des données d'une entreprise à analyser

**Informations générales**:
```
- Raison sociale
- RC (Registre Commerce)
- ICE
- Forme juridique (SARL, SA, SAS, etc.)
- Secteur d'activité
- Date de création
- Capital social
- Coordonnées (ville, téléphone, email, site web)
```

### F-02.02: Import de documents (PDF de bilans, Excel)

**⭐ AMÉLIORATION: Méthodes d'import**

| Méthode | Description | Disponibilité |
|---------|-------------|---------------|
| **Saisie manuelle** | Formulaire structuré | MVP |
| **Import CSV/Excel** | Template prédéfini | MVP |
| **Extraction PDF** | OCR + IA | V2.0 |

**Template CSV fourni**:
```csv
poste_comptable,montant
immobilisations_incorporelles,500000
immobilisations_corporelles,2000000
stocks,800000
creances_clients,1200000
tresorerie_actif,500000
capital_social,1000000
dettes_financement,1200000
chiffre_affaires,8000000
...
```

### F-02.03: Calcul automatique de 15 ratios financiers clés

**⭐ DÉTAIL COMPLET DES 15 RATIOS** (voir Annexe A pour formules)

**CATÉGORIE 1: LIQUIDITÉ** (3 ratios - 20% du score)
1. Liquidité Générale
2. Liquidité Réduite
3. Liquidité Immédiate

**CATÉGORIE 2: SOLVABILITÉ** (3 ratios - 20% du score)
4. Autonomie Financière
5. Capacité d'Endettement
6. Couverture des Intérêts

**CATÉGORIE 3: RENTABILITÉ** (4 ratios - 30% du score)
7. ROA (Return on Assets)
8. Marge Nette
9. ROE (Return on Equity)
10. EBITDA Margin

**CATÉGORIE 4: GESTION** (3 ratios - 20% du score)
11. Délai Clients (DSO)
12. Délai Fournisseurs
13. Rotation des Stocks

**CATÉGORIE 5: STRUCTURE FINANCIÈRE** (2 ratios - 10% du score)
14. Fonds de Roulement Net
15. BFR (Besoin en Fonds de Roulement)

### F-02.04: Intégration de l'historique de paiement

**Données collectées**:
- Nombre total de paiements (12 derniers mois)
- Nombre de paiements à temps
- Nombre et durée moyenne des retards
- Impayés existants
- Contentieux en cours

**Calcul score paiement**:
```
Taux Ponctualité = (Paiements à temps / Total paiements) × 100

Si ≥ 95%  → Note = 100
Si 90-94% → Note = 85
Si 80-89% → Note = 70
Si < 60%  → Note = 10

Pénalités:
- Retard moyen > 15j: -10 points
- Retard moyen > 30j: -20 points
- Impayé en cours: -30 points
- Contentieux: -50 points
```

### F-02.05: Application d'ajustements basés sur secteur et ancienneté

**Ajustement Sectoriel**:

| Secteur | Ajustement | Justification |
|---------|------------|---------------|
| Santé/Pharmacie | +5 | Secteur régulé, stable |
| Services IT | +3 | Marges élevées |
| Agroalimentaire | +2 | Demande constante |
| Commerce | 0 | Baseline |
| Import-Export | -3 | Volatilité change |
| Tourisme | -5 | Forte saisonnalité |
| **BTP/Construction** | **-7** | Délais paiement très longs |

**Ajustement Ancienneté**:

| Âge entreprise | Ajustement |
|----------------|------------|
| < 1 an | -15 |
| 1-2 ans | -10 |
| 2-5 ans | -5 |
| 5-10 ans | 0 |
| > 10 ans | +5 |

### F-02.06: Génération d'un score unique sur 100

**⭐ CATÉGORISATION AMÉLIORÉE**:

| Score | Catégorie | Badge | Taux Défaut Estimé |
|-------|-----------|-------|-------------------|
| **90-100** | Excellent | 🟢🟢🟢 | < 1% |
| **75-89** | Faible Risque | 🟢🟢 | 1-3% |
| **60-74** | Risque Modéré | 🟡🟡 | 3-8% |
| **40-59** | Risque Moyen | 🟠 | 8-15% |
| **25-39** | Risque Élevé | 🔴 | 15-30% |
| **0-24** | Risque Critique | 🔴🔴 | > 30% |

### F-02.07: Génération d'un rapport détaillé

**Structure du rapport PDF (8 sections)**:

1. **Page de garde**
   - Logo RiskScore Pro + logo client
   - Nom entreprise analysée
   - Score en grand format
   - Date du rapport

2. **Synthèse Exécutive**
   - Score final et catégorie
   - Recommandation principale
   - 3 points forts
   - 3 points d'alerte

3. **Informations Entreprise**
   - Raison sociale, RC, ICE
   - Secteur, ancienneté
   - Chiffre d'affaires, capital

4. **Analyse des Ratios**
   - Tableau des 15 ratios
   - Graphique radar
   - Comparaison sectorielle

5. **Décomposition du Score**
   - Score Santé Financière (40%)
   - Score Comportement Paiement (35%)
   - Score Contexte (25%)

6. **Recommandations Détaillées**
   - Décision crédit
   - Montant maximum
   - Délai de paiement
   - Garanties suggérées

7. **Points d'Attention**
   - Risques identifiés
   - Actions de suivi

8. **Annexes**
   - Méthodologie
   - Bilan comptable complet

---

## 2.3 Moteur de Décision et Recommandations

### F-03.01: Recommandation automatique selon le score

**⭐ MATRICE DE DÉCISION DÉTAILLÉE**:

| Score | Décision | Montant Max | Délai Max | Garanties | Actions |
|-------|----------|-------------|-----------|-----------|---------|
| **90-100** | ✅ Accord sans réserve | Illimité | 90 jours | Aucune | Suivi annuel |
| **75-89** | ✅ Accord | 100% demandé | 60-90j | Facultative | Suivi semestriel |
| **60-74** | ⚠️ Accord conditionnel | 70% demandé | 45-60j | Caution simple 20% | Suivi trimestriel |
| **40-59** | ⚠️ Accord restrictif | 40% demandé | 30j | Caution bancaire 30% | Suivi mensuel |
| **25-39** | ❌ Refus avec alternatives | Max 10K MAD | 15j | Paiement anticipé 50% | Réévaluation 3 mois |
| **0-24** | ❌ Refus ferme | 0 MAD | Comptant | N/A | Blocage |

**Exemple de recommandation générée**:

```
═══════════════════════════════════════════════════════════
RECOMMANDATION AUTOMATIQUE

Entreprise: TechCorp SARL
Score: 68/100 (Risque Modéré)
Date: 15 Mars 2024
═══════════════════════════════════════════════════════════

DÉCISION: ✅ ACCORD CONDITIONNEL

CONDITIONS PROPOSÉES:
• Montant maximum: 350 000 MAD (70% du montant demandé)
• Délai de paiement: 60 jours nets
• Garantie requise: Caution bancaire à hauteur de 20%
• Révision du dossier: Dans 6 mois

JUSTIFICATION:
Points forts:
✓ Liquidité acceptable (ratio 1.3)
✓ Secteur porteur (IT Services, +3 points)
✓ Résultat positif (+18% vs N-1)

Points de vigilance:
⚠ Endettement légèrement élevé (ratio 1.8)
⚠ Délai clients supérieur à 60 jours
⚠ Baisse du CA de 5% sur l'exercice

ACTIONS RECOMMANDÉES:
1. Suivre mensuellement l'évolution du CA
2. Vérifier le respect des échéances de paiement
3. Recalculer le score dans 6 mois
4. Demander états financiers intermédiaires
═══════════════════════════════════════════════════════════
```

### F-03.02: Proposition montant maximum, délai et garanties

**Calculé automatiquement** selon la matrice ci-dessus.

### F-03.03: Possibilité de modifier manuellement la recommandation

**Règles d'override**:

| Score | Qui peut modifier? | Conditions |
|-------|-------------------|------------|
| 75-100 | Analyste ou Admin | Libre |
| 40-74 | Analyste ou Admin | Justification requise (min 50 caractères) |
| 25-39 | Analyste ou Admin | Justification détaillée (min 100 caractères) |
| **0-24** | **Admin UNIQUEMENT** | Justification + Validation 2ème Admin |

**Traçabilité**:
- Qui a modifié (user_id)
- Quand (timestamp)
- Ancienne vs nouvelle recommandation
- Justification fournie
- Conservation permanente (audit_logs)

---

## 2.4 Tableau de Bord et Suivi (Dashboard)

### F-04.01: Vue synthétique avec indicateurs clés

**4 CARTES KPI**:
```
┌─────────────┐ ┌──────────────┐ ┌──────────────┐ ┌─────────────┐
│   Total     │ │   Analyses   │ │ Exposition   │ │   Alertes   │
│ Entreprises │ │   Ce Mois    │ │   Risque     │ │  Actives    │
│             │ │              │ │              │ │             │
│     247     │ │      45      │ │  8.5M MAD    │ │      12     │
└─────────────┘ └──────────────┘ └──────────────┘ └─────────────┘
```

**KPIs calculés**:
- Nombre total d'entreprises analysées
- Nombre d'analyses effectuées ce mois
- **Exposition totale au risque** = Somme des montants en cours
- Nombre d'alertes actives non traitées

### F-04.02: Liste des alertes en cours

**Types d'alertes affichées**:
1. 🔴 Dégradation de score (baisse > 10 points)
2. 🔴 Passage en zone critique (score < 40)
3. 🟡 Révision nécessaire (> 6 mois)
4. 🟠 Retard de paiement signalé
5. ⚪ Nouvel endettement détecté

### F-04.03: Graphique d'évolution du score d'une entreprise

**Graphique Line Chart**:
```
Score
100 │
    │                   ●
 80 │         ●               ●
    │   ●
 60 │
    │
 40 │
    └─────────────────────────────────
     Jan  Avr  Jul  Oct  Jan  Avr
```

**Fonctionnalités**:
- Zoom sur période spécifique
- Affichage événements marquants
- Export image PNG

### F-04.04: Répartition du portefeuille clients par catégorie de risque

**Graphique Pie Chart**:
```
Excellent (90-100):     12 entreprises (5%)  🟢
Faible Risque (75-89):  45 entreprises (18%) 🟢
Risque Modéré (60-74):  110 entreprises (45%) 🟡
Risque Moyen (40-59):   65 entreprises (26%) 🟠
Risque Élevé (25-39):   12 entreprises (5%)  🔴
Risque Critique (0-24): 3 entreprises (1%)   🔴
```

### F-04.05: Liste des entreprises nécessitant une révision

**Critères de révision**:
- Dernière analyse > 6 mois
- Score en zone orange/rouge
- Événement externe signalé

**Affichage**:
```
┌─────────────────┬──────────┬──────────────┬───────────────┐
│ Entreprise      │ Score    │ Dernière     │ Action        │
│                 │          │ Analyse      │               │
├─────────────────┼──────────┼──────────────┼───────────────┤
│ BuildCo SARL    │ 35 🔴   │ Il y a 8 mois│ [Réviser]     │
│ TradeX          │ 58 🟠   │ Il y a 7 mois│ [Réviser]     │
│ MediaPlus       │ 72 🟡   │ Il y a 6 mois│ [Réviser]     │
└─────────────────┴──────────┴──────────────┴───────────────┘
```

---

## 2.5 Système d'Alerte et Monitoring

### F-05.01: Recalcul automatique périodique des scores

**Fréquence selon risque**:

| Catégorie Risque | Fréquence Recalcul |
|------------------|--------------------|
| Critique (0-24) | **Hebdomadaire** |
| Élevé (25-39) | **Bimensuel** |
| Moyen (40-59) | **Mensuel** |
| Modéré (60-74) | **Trimestriel** |
| Faible (75-100) | **Semestriel** |

**Implémentation**:
- Batch nocturne (3h du matin)
- Notification avant recalcul
- Détection automatique changements

### F-05.02: Détection automatique dégradation significative

**Critères de détection**:
```
Si (Score_Nouveau < Score_Ancien - 10)
  OU (Score_Nouveau < 40 ET Score_Ancien >= 40)
  OU (Capitaux_Propres < 0)
ALORS
  Générer Alerte CRITIQUE
```

### F-05.03: Génération d'alertes via notification + email + SMS

**Canaux de notification**:

| Canal | Délai | Gravité |
|-------|-------|---------|
| **In-app** | Temps réel | Toutes |
| **Email** | < 5 min | Moyenne + Élevée + Critique |
| **SMS** | < 2 min | Critique uniquement |

**Template Email**:
```
De: alerts@riskscorepro.com
À: analyste@votreentreprise.ma
Objet: 🔴 [URGENT] Dégradation TechCorp SARL

Bonjour M. Alami,

Alerte critique déclenchée:

ENTREPRISE: TechCorp SARL (RC: 123456)

SCORE PRÉCÉDENT: 75/100 (Faible Risque 🟢)
SCORE ACTUEL: 58/100 (Risque Moyen 🟠)
VARIATION: -17 points (-23%)

DATE: 15 Mars 2024 à 14:30

RAISONS:
• Baisse liquidité générale (1.5 → 1.0)
• Augmentation endettement (120% → 180%)
• CA en baisse (-12%)

ACTIONS URGENTES:
1. Contacter l'entreprise immédiatement
2. Demander explications
3. Réviser lignes de crédit en cours
4. Demander garanties supplémentaires

Voir détail complet: [lien]

Cordialement,
RiskScore Pro - Système d'Alertes
```

### F-05.04: Alertes basées sur événements externes

**Sources d'événements**:
- Retard de paiement détecté
- Nouvel endettement (crédit bancaire)
- Changement statut juridique
- Nouvelle procédure collective
- Article de presse négatif (V2.0 avec IA)

---

## 2.6 API et Intégrations

### F-06.01: API REST sécurisée

**Endpoints principaux**:

```
POST /api/v1/analyses/request
{
  "rc": "123456",
  "ice": "002345678900001",
  "montant_demande": 500000,
  "delai_souhaite": 60
}

Response 200:
{
  "analysis_id": "uuid-xxx",
  "score": 68,
  "risk_category": "MODERE",
  "recommendation": {
    "decision": "ACCORD_CONDITIONNEL",
    "montant_max": 350000,
    "delai_max": 60,
    "garanties": "Caution bancaire 20%"
  },
  "report_url": "https://..."
}
```

**Authentification**:
- API Key (pour clients)
- OAuth 2.0 (pour intégrations tierces)

### F-06.02: Webhooks pour notifications externes

**Configuration webhook**:
```json
{
  "webhook_url": "https://client.com/webhook",
  "events": [
    "score.degraded",
    "score.critical",
    "analysis.completed"
  ],
  "secret": "xxx"
}
```

**Payload envoyé**:
```json
{
  "event": "score.degraded",
  "timestamp": "2024-03-15T14:30:00Z",
  "data": {
    "company_id": "123",
    "company_name": "TechCorp SARL",
    "old_score": 75,
    "new_score": 58,
    "variation": -17
  },
  "signature": "hmac-sha256..."
}
```

---

# 3. RÈGLES MÉTIER

## 3.1 Pondération du Score

**Formule principale**:
```
Score Final = (Santé Financière × 0.40) +
              (Comportement Paiement × 0.35) +
              (Facteurs Contextuels × 0.25)
```

**Justification**:
- **40% Santé Financière**: Indicateur le plus objectif
- **35% Comportement Paiement**: Meilleur prédicteur de défaut
- **25% Contexte**: Ajustement pour facteurs externes

## 3.2 Ajustements Sectoriels

Voir section 2.2 (F-02.05)

## 3.3 Seuils de Décision

**Les seuils sont configurables** par l'administrateur client, mais valeurs par défaut:

```
90-100 → Excellent
75-89  → Faible Risque
60-74  → Risque Modéré
40-59  → Risque Moyen
25-39  → Risque Élevé
0-24   → Risque Critique
```

## 3.4 ⭐ ALGORITHME DE SCORING DÉTAILLÉ

### Étape 1: Calcul des Notes de Ratios (0-10)

**Méthode**: Interpolation linéaire entre bornes min/max

**Exemple - Liquidité Générale**:
```
Valeur   →   Note
≥ 1.5    →   10
1.3      →   8
1.0      →   5
< 0.8    →   0

Formule:
Note = ((Valeur - Min) / (Max - Min)) × 10
Note = Plafonnée [0, 10]
```

### Étape 2: Score Santé Financière (40%)

**Calcul par catégorie**:

```
LIQUIDITÉ (20%):
  Note_Liquidité = (R1 + R2 + R3) / 3
  Score_Liquidité = Note_Liquidité × 0.20

SOLVABILITÉ (20%):
  Note_Solvabilité = (R4 + R5 + R6) / 3
  Score_Solvabilité = Note_Solvabilité × 0.20

RENTABILITÉ (30%):
  Note_Rentabilité = (R7 + R8 + R9 + R10) / 4
  Score_Rentabilité = Note_Rentabilité × 0.30

GESTION (20%):
  Note_Gestion = (R11 + R12 + R13) / 3
  Score_Gestion = Note_Gestion × 0.20

STRUCTURE (10%):
  Note_Structure = (R14 + R15) / 2
  Score_Structure = Note_Structure × 0.10

Score_Santé_Brut = Somme des 5 scores ci-dessus

Score_Santé_Financière = Score_Santé_Brut × 10
Contribution_Santé = Score_Santé_Financière × 0.40
```

**Exemple TechCorp**:
```
Liquidité: 7/10 → 70 × 0.20 = 14
Solvabilité: 6/10 → 60 × 0.20 = 12
Rentabilité: 8/10 → 80 × 0.30 = 24
Gestion: 7/10 → 70 × 0.20 = 14
Structure: 6/10 → 60 × 0.10 = 6
────────────────────────────────
Score_Santé_Brut = 70
Score_Santé = 70 × 10 = 70/100
Contribution = 70 × 0.40 = 28
```

### Étape 3: Score Comportement Paiement (35%)

Voir section 2.2 (F-02.04)

### Étape 4: Score Contexte (25%)

```
Score_Contexte = 50 (base)
               + Ajustement_Secteur (-7 à +5)
               + Ajustement_Ancienneté (-15 à +5)
               + Bonus_Taille (0 à +10)
               + Bonus_Capital (0 à +5)

Contribution_Contexte = Score_Contexte × 0.25
```

### Étape 5: Score Final

```
SCORE_FINAL = Contribution_Santé +
              Contribution_Paiement +
              Contribution_Contexte

SCORE_FINAL = Arrondi à l'entier
```

---

## 3.5 ⭐ GESTION DES CAS LIMITES

### CAS-01: Entreprise Très Jeune (< 2 ans)

**Solution**:
- Score basé sur capital + secteur
- Pénalité ancienneté: -10 à -15
- Score plafonné à 65/100
- Crédit limité (30% max)
- Garanties renforcées obligatoires

### CAS-02: Données Incomplètes

**Règle**:
- Si 10-15 ratios OK: Calcul normal
- Si 7-9 ratios: Ratios manquants = 5/10 + Warning
- Si < 7 ratios: **Blocage analyse**

### CAS-03: Résultat Net Négatif

**Solution**:
- Ratios rentabilité = 0/10
- Pénalité -5 points
- Si pertes 2 ans: Score plafonné 40/100

### CAS-04: Capitaux Propres Négatifs

**Solution**:
- Score forcé < 25 (Critique)
- Recommandation: REFUS FERME
- Non modifiable (même par Admin)

### CAS-05: Pas d'Historique Paiement

**Solution**:
- Score paiement = 50/100 (neutre)
- Montant limité (50%)
- Suivi rapproché premiers mois

### CAS-06: Procédure Collective

**Solution**:
- Score forcé à 0
- Refus automatique
- Badge rouge partout
- V2.0: Vérification auto via API RC

---

# 4. BESOINS TECHNIQUES ET ARCHITECTURE

## 4.1 Architecture Microservices

### Vue d'Ensemble

```
                        [UTILISATEURS]
                        (Web, Mobile)
                              │
                              │ HTTPS
                              ▼
                     ┌─────────────────┐
                     │   API GATEWAY   │
                     │  (Spring Cloud) │
                     └────────┬────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        │                     │                     │
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│   SERVICE     │   │   SERVICE     │   │   SERVICE     │
│     AUTH      │   │   COMPANY     │   │   SCORING     │
│               │   │               │   │  (CORE)       │
│ Port: 8081    │   │ Port: 8082    │   │ Port: 8083    │
│ DB: PostgreSQL│   │ DB: PostgreSQL│   │ DB: PostgreSQL│
└───────────────┘   └───────────────┘   └───────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│   SERVICE     │   │   SERVICE     │   │   SERVICE     │
│   ANALYSIS    │   │     REPORT    │   │     ALERT     │
│               │   │               │   │               │
│ Port: 8084    │   │ Port: 8085    │   │ Port: 8086    │
│ DB: PostgreSQL│   │ DB: PostgreSQL│   │ DB: MongoDB   │
└───────────────┘   └───────────────┘   └───────────────┘

                              │
                              ▼
                  ┌────────────────────┐
                  │  MESSAGE QUEUE     │
                  │  (RabbitMQ/Kafka)  │
                  └────────────────────┘
```

---

### Détail des Microservices

#### **1. SERVICE AUTHENTIFICATION (auth-service)**

**Responsabilités**:
- Gestion utilisateurs (CRUD)
- Authentification JWT
- Gestion rôles et permissions
- Multi-tenancy (tenant management)

**Technologies**:
- Spring Boot
- Spring Security
- JWT (io.jsonwebtoken)
- PostgreSQL

**Endpoints**:
```
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
POST /api/auth/logout
GET  /api/users
POST /api/users
```

---

#### **2. SERVICE ENTREPRISES (company-service)**

**Responsabilités**:
- CRUD entreprises
- Recherche et filtrage
- Gestion relations commerciales
- Import/Export données

**Technologies**:
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Apache POI (Excel)

**Endpoints**:
```
GET    /api/companies
POST   /api/companies
GET    /api/companies/{id}
PUT    /api/companies/{id}
DELETE /api/companies/{id}
GET    /api/companies/search?q=...
POST   /api/companies/import/csv
```

---

#### **3. SERVICE SCORING (scoring-service)** ⭐ CŒUR MÉTIER

**Responsabilités**:
- Calcul des 15 ratios financiers
- Algorithme de scoring
- Règles métier
- Ajustements sectoriels

**Technologies**:
- Spring Boot
- PostgreSQL
- Cache Redis (pour performances)

**Endpoints**:
```
POST /api/scoring/calculate
{
  "company_id": "123",
  "financial_data_id": "456"
}

Response:
{
  "score_final": 68,
  "score_sante": 67,
  "score_paiement": 75,
  "score_contexte": 58,
  "risk_category": "MODERE",
  "ratios": {...}
}
```

**Logique métier**:
```java
@Service
public class ScoringEngine {
    
    public ScoreResult calculateScore(FinancialData data) {
        // Étape 1: Calcul ratios
        Map<String, Double> ratios = calculateRatios(data);
        
        // Étape 2: Notes ratios (0-10)
        Map<String, Double> notes = scoreRatios(ratios);
        
        // Étape 3: Score santé (40%)
        double scoreSante = calculateHealthScore(notes);
        
        // Étape 4: Score paiement (35%)
        double scorePaiement = calculatePaymentScore(data);
        
        // Étape 5: Score contexte (25%)
        double scoreContexte = calculateContextScore(data);
        
        // Étape 6: Score final
        double scoreFinal = (scoreSante * 0.40) +
                           (scorePaiement * 0.35) +
                           (scoreContexte * 0.25);
        
        return new ScoreResult(scoreFinal, ...);
    }
}
```

---

#### **4. SERVICE ANALYSE (analysis-service)**

**Responsabilités**:
- Gestion des analyses
- Historique analyses
- Comparaison exercices
- Génération recommandations

**Technologies**:
- Spring Boot
- PostgreSQL

**Endpoints**:
```
POST /api/analyses
GET  /api/analyses/{id}
GET  /api/analyses/company/{companyId}
GET  /api/analyses/{id}/history
POST /api/analyses/{id}/recommendation/override
```

---

#### **5. SERVICE RAPPORTS (report-service)**

**Responsabilités**:
- Génération PDF
- Templates personnalisés
- Envoi email rapports
- Stockage historique

**Technologies**:
- Spring Boot
- iText 7 (PDF)
- Spring Mail
- PostgreSQL (métadonnées)
- S3/MinIO (stockage PDFs)

**Endpoints**:
```
POST /api/reports/generate/{analysisId}
GET  /api/reports/{reportId}/download
POST /api/reports/{reportId}/send-email
```

---

#### **6. SERVICE ALERTES (alert-service)**

**Responsabilités**:
- Détection alertes
- Notifications (email, SMS, in-app)
- Gestion préférences utilisateur
- Historique alertes

**Technologies**:
- Spring Boot
- MongoDB (logs alertes)
- Spring Mail
- Twilio (SMS)
- WebSocket (notifications temps réel)

**Endpoints**:
```
GET  /api/alerts
POST /api/alerts/{id}/acknowledge
PUT  /api/alerts/preferences
GET  /api/alerts/history
```

---

### Communication Inter-Services

**1. REST API (Synchrone)**:
```
Scoring Service appelle Company Service:
GET http://company-service:8082/api/companies/123
```

**2. Message Queue (Asynchrone)**:
```
Analysis créée → Event sur RabbitMQ
                → Alert Service écoute
                → Génère alertes si nécessaire
```

**3. Service Discovery (Eureka)**:
```
Tous les services s'enregistrent sur Eureka
API Gateway découvre dynamiquement les instances
```

---

### Gestion Multi-Tenancy

**Approche**: Base de données par tenant

```
Tenant 1 (Entreprise A) → DB: riskscore_tenant_1
Tenant 2 (Entreprise B) → DB: riskscore_tenant_2
Tenant 3 (Entreprise C) → DB: riskscore_tenant_3
```

**Avantages**:
✅ Isolation complète des données
✅ Scalabilité par tenant
✅ Backup/Restore indépendants
✅ Performance isolée

**Implémentation**:
```java
@Component
public class TenantContext {
    private static ThreadLocal<String> currentTenant = new ThreadLocal<>();
    
    public static void setTenant(String tenantId) {
        currentTenant.set(tenantId);
    }
    
    public static String getTenant() {
        return currentTenant.get();
    }
}

@Configuration
public class DataSourceConfig {
    @Bean
    public DataSource dataSource() {
        return new TenantAwareRoutingDataSource();
    }
}
```

---

## 4.2 Stack Technologique

### Backend (Microservices)

| Composant | Technologie |
|-----------|-------------|
| **Framework** | Spring Boot 3.x |
| **Langage** | Java 17+ |
| **Service Discovery** | Spring Cloud Eureka |
| **API Gateway** | Spring Cloud Gateway |
| **Config Server** | Spring Cloud Config |
| **Load Balancer** | Ribbon (client-side) |
| **Circuit Breaker** | Resilience4j |
| **Message Queue** | RabbitMQ ou Apache Kafka |
| **Cache** | Redis |
| **Sécurité** | Spring Security + JWT |
| **ORM** | Spring Data JPA |
| **Migration DB** | Flyway |

### Bases de Données

| Service | Base de Données | Justification |
|---------|----------------|---------------|
| Auth, Company, Scoring, Analysis | PostgreSQL 15 | Relationnel, ACID |
| Alert (logs) | MongoDB | Documents, flexibilité |
| Cache | Redis | Performance |

### Frontend

| Composant | Technologie |
|-----------|-------------|
| Framework | Angular 16+ / React 18+ |
| UI Kit | Angular Material / Bootstrap 5 |
| Charts | Chart.js + ECharts |
| State | NgRx / Redux |
| Real-time | Socket.io (notifications) |

### Infrastructure

| Composant | Technologie |
|-----------|-------------|
| **Conteneurisation** | Docker |
| **Orchestration** | Kubernetes (K8s) ou Docker Swarm |
| **CI/CD** | GitLab CI / GitHub Actions |
| **Monitoring** | Prometheus + Grafana |
| **Logs** | ELK Stack (Elasticsearch, Logstash, Kibana) |
| **Tracing** | Zipkin / Jaeger |
| **Cloud** | AWS / Azure / GCP |

---

## 4.3 Modèle de Données

### Base Commune (Partagée)

**Table: tenants**
```sql
CREATE TABLE tenants (
    id SERIAL PRIMARY KEY,
    tenant_key VARCHAR(50) UNIQUE NOT NULL,
    company_name VARCHAR(255),
    database_name VARCHAR(100),
    subscription_plan VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP
);
```

### Base par Tenant

**Table: users**
```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    role VARCHAR(50), -- ADMIN, ANALYSTE, LECTEUR
    created_at TIMESTAMP
);
```

**Table: companies**
```sql
CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    legal_name VARCHAR(255),
    rc VARCHAR(50) UNIQUE,
    ice VARCHAR(50) UNIQUE,
    sector VARCHAR(100),
    creation_date DATE,
    capital DECIMAL(15,2),
    current_score INTEGER,
    last_analysis_date DATE,
    created_at TIMESTAMP
);
```

**Table: financial_data** (Voir section précédente)

**Table: analyses** (Voir section précédente)

**Table: recommendations** (Voir section précédente)

**Table: alerts** (MongoDB)
```json
{
  "_id": "...",
  "company_id": 123,
  "type": "DEGRADATION",
  "severity": "CRITICAL",
  "old_score": 75,
  "new_score": 58,
  "message": "...",
  "created_at": "2024-03-15T14:30:00Z"
}
```

---

## 4.4 Contraintes Techniques

### Performance

- Dashboard: < 2 secondes
- Calcul score: < 5 secondes
- Génération PDF: < 10 secondes

### Scalabilité

- Support 1000+ tenants
- 10000+ analyses/jour
- Auto-scaling sur Kubernetes

### Disponibilité

- **SLA: 99.5%** (3.6h downtime/mois)
- Multi-AZ deployment
- Backup automatique quotidien

### Sécurité

- HTTPS obligatoire
- Chiffrement données au repos (AES-256)
- Isolation stricte multi-tenant
- RGPD compliant

---

# 5. LIVRABLES ET PLANIFICATION

## 5.1 Livrables Attendus

### Livrable 1: Application Web Complète

- ✅ Frontend déployé et accessible
- ✅ 6 microservices déployés
- ✅ API Gateway configuré
- ✅ Base de données provisionnée

### Livrable 2: Code Source

- ✅ Dépôt Git (GitHub/GitLab)
- ✅ Code commenté
- ✅ README.md avec instructions
- ✅ Docker Compose pour dev local
- ✅ Kubernetes manifests pour prod

### Livrable 3: Base de Données

- ✅ Schéma documenté (ERD)
- ✅ Scripts de migration (Flyway)
- ✅ Jeu de données démo:
  - 3 tenants
  - 30 entreprises fictives
  - 50 analyses complètes
  - 20 alertes

### Livrable 4: Documentation Technique

- ✅ Architecture microservices (diagrammes)
- ✅ Guide d'installation
- ✅ Documentation API (Swagger/OpenAPI)
- ✅ Guide déploiement Kubernetes

### Livrable 5: Documentation Utilisateur

- ✅ Manuel PDF (20-30 pages)
- ✅ Captures d'écran annotées
- ✅ Tutoriels vidéo (optionnel)

### Livrable 6: Support Présentation

- ✅ Slides PowerPoint (20-25 slides)
- ✅ Vidéo démo (5-10 minutes)

---

## 5.2 Planning Prévisionnel (14 semaines)

### ⭐ PLANNING RÉVISÉ MICROSERVICES

| Phase | Durée | Activités | Livrable |
|-------|-------|-----------|----------|
| **1. Conception** | 2 sem | Specs détaillées, maquettes, architecture microservices | CDC + Maquettes + Diagrammes |
| **2. Infra & Auth** | 2 sem | Docker, K8s, Eureka, Gateway, Auth Service | Auth fonctionnel |
| **3. Services Core** | 3 sem | Company Service, Scoring Service (CŒUR), Analysis Service | Services métier opérationnels |
| **4. Frontend** | 2.5 sem | Angular/React, intégration services | UI fonctionnelle |
| **5. Services Avancés** | 2 sem | Report Service, Alert Service | Système complet |
| **6. Tests & Deploy** | 1.5 sem | Tests, déploiement K8s | Application en ligne |
| **7. Documentation** | 1 sem | Docs technique + utilisateur, présentation | Livrables finaux |

**Total: 14 semaines** ✅

---

## 5.3 ⭐ GESTION DES RISQUES

### Risques Identifiés

| Risque | Probabilité | Impact | Mitigation |
|--------|-------------|--------|------------|
| **Complexité microservices** | Élevée | Critique | Commencer simple, ajouter services progressivement |
| **Communication inter-services** | Moyenne | Élevé | Utiliser Spring Cloud, tester tôt |
| **Déploiement K8s** | Moyenne | Élevé | Docker Compose d'abord, K8s en fin de projet |
| **Performance scoring** | Faible | Moyen | Cache Redis, optimisation algorithme |
| **Multi-tenancy complexe** | Moyenne | Élevé | Démarrer avec 1 tenant, généraliser ensuite |

### Plan de Contingence

Si **retard > 1 semaine**:
1. Réduire nombre de microservices (fusionner)
2. Déployer sur Docker Compose au lieu de K8s
3. Reporter features V2.0 (extraction PDF, SMS)

---

# 6. ⭐ SCÉNARIOS DE DÉMONSTRATION

## Scénario 1: Client Excellent (Score 88)

**Entreprise**: TechSoft SARL  
**Secteur**: IT Services  
**Ancienneté**: 8 ans  

**Données financières**:
- Liquidité Générale: 1.8 (Excellent)
- Autonomie Financière: 65% (Très bon)
- ROE: 22% (Excellent)
- Délai Clients: 35 jours (Bon)

**Paiement**: 98% à temps (24/24 paiements)

**RÉSULTAT**:
```
Score Final: 88/100
Catégorie: Faible Risque 🟢🟢

Recommandation:
✅ ACCORD SANS RÉSERVE
• Montant: Illimité
• Délai: 90 jours
• Garanties: Aucune
```

---

## Scénario 2: Client Risqué (Score 35)

**Entreprise**: BuildCo Construction  
**Secteur**: BTP (-7 points)  
**Ancienneté**: 3 ans  

**Données financières**:
- Liquidité: 0.7 (Faible)
- Endettement: 320% (Critique)
- ROE: -8% (Perte)
- Délai Clients: 110 jours (Très long)

**Paiement**: 60% à temps (12/20), 2 impayés

**RÉSULTAT**:
```
Score Final: 35/100
Catégorie: Risque Élevé 🔴

Recommandation:
❌ REFUS AVEC ALTERNATIVES
• Montant max: 10 000 MAD
• Délai: 15 jours
• Garanties: Paiement anticipé 50%
```

---

## Scénario 3: Alerte de Dégradation

**Setup**: MediaPlus analysée il y a 6 mois (Score: 72)

**Nouvelle analyse**: Score actuel: 55 (-17 points)

**Déclenchement alerte**:
```
🔴 ALERTE CRITIQUE

ENTREPRISE: MediaPlus
DÉGRADATION: 72 → 55 (-17 points, -24%)

RAISONS:
• CA en baisse (-18%)
• Liquidité dégradée (1.4 → 0.9)
• 3 retards de paiement récents

ACTION: Email + SMS envoyés
STATUT: En attente traitement analyste
```

---

# 7. CRITÈRES D'ACCEPTATION

## Critères Fonctionnels

✅ CF-01: Authentification multi-tenant fonctionnelle  
✅ CF-02: Isolation données entre tenants vérifiée  
✅ CF-03: CRUD entreprises opérationnel  
✅ CF-04: Import CSV fonctionnel  
✅ CF-05: Calcul 15 ratios correct  
✅ CF-06: Score final généré en < 10 secondes  
✅ CF-07: Recommandation automatique cohérente  
✅ CF-08: Rapport PDF professionnel généré  
✅ CF-09: Dashboard avec KPIs temps réel  
✅ CF-10: Alertes email envoyées automatiquement  
✅ CF-11: API REST accessible et documentée  

## Critères Techniques

✅ CT-01: 6 microservices déployés et opérationnels  
✅ CT-02: Service Discovery (Eureka) fonctionnel  
✅ CT-03: API Gateway route correctement  
✅ CT-04: Communication inter-services OK  
✅ CT-05: Multi-tenancy avec BDs séparées  
✅ CT-06: Authentification JWT sécurisée  
✅ CT-07: Performance < 3s dashboard  
✅ CT-08: 10 utilisateurs simultanés sans crash  
✅ CT-09: Tests unitaires > 60% coverage  
✅ CT-10: Déploiement Kubernetes réussi  

## Critères Qualité

✅ CQ-01: Interface intuitive (utilisable sans manuel)  
✅ CQ-02: Rapport PDF professionnel  
✅ CQ-03: Documentation complète  
✅ CQ-04: Jeu de données démo réaliste  
✅ CQ-05: Présentation efficace (< 25 min)  

---

# ANNEXES

## Annexe A: Liste des 15 Ratios Financiers

### LIQUIDITÉ (20% du score)

**R1. LIQUIDITÉ GÉNÉRALE**
```
Formule: Actif Circulant / Passif Circulant

Interprétation:
≥ 1.5  → Excellent (10/10)
1.2-1.5→ Bon (7-9/10)
1.0-1.2→ Acceptable (5-6/10)
< 1.0  → Préoccupant (0-4/10)

Exemple:
AC = 800K, PC = 500K
Ratio = 800/500 = 1.6 → Excellent
```

**R2. LIQUIDITÉ RÉDUITE**
```
Formule: (Actif Circulant - Stocks) / Passif Circulant

Interprétation:
≥ 1.0  → Excellent (10/10)
0.8-1.0→ Bon (7-9/10)
0.5-0.8→ Moyen (4-6/10)
< 0.5  → Faible (0-3/10)
```

**R3. LIQUIDITÉ IMMÉDIATE**
```
Formule: Trésorerie / Passif Circulant

Interprétation:
≥ 0.3  → Excellent (10/10)
0.2-0.3→ Bon (7-9/10)
0.1-0.2→ Moyen (4-6/10)
< 0.1  → Critique (0-3/10)
```

### SOLVABILITÉ (20% du score)

**R4. AUTONOMIE FINANCIÈRE**
```
Formule: Capitaux Propres / Total Passif

Interprétation:
≥ 0.5  → Excellente indépendance (10/10)
0.3-0.5→ Bonne structure (7-9/10)
0.2-0.3→ Dépendance acceptable (4-6/10)
< 0.2  → Forte dépendance (0-3/10)
```

**R5. CAPACITÉ D'ENDETTEMENT**
```
Formule: Total Dettes / Capitaux Propres

Interprétation:
< 1.0  → Faible endettement (10/10)
1.0-2.0→ Endettement modéré (6-9/10)
2.0-3.0→ Endettement élevé (3-5/10)
> 3.0  → Sur-endetté (0-2/10)
```

**R6. COUVERTURE DES INTÉRÊTS**
```
Formule: Résultat d'Exploitation / Charges Financières

Interprétation:
≥ 5    → Excellente couverture (10/10)
3-5    → Bonne couverture (7-9/10)
1-3    → Couverture juste (4-6/10)
< 1    → Insuffisante (0-3/10)
```

### RENTABILITÉ (30% du score)

**R7. ROA (Return on Assets)**
```
Formule: (Résultat Net / Total Actif) × 100

Interprétation:
≥ 10%  → Excellente rentabilité (10/10)
5-10%  → Bonne rentabilité (7-9/10)
2-5%   → Rentabilité faible (4-6/10)
< 2%   → Non rentable (0-3/10)
```

**R8. MARGE NETTE**
```
Formule: (Résultat Net / CA) × 100

Interprétation:
≥ 15%  → Excellente marge (10/10)
8-15%  → Bonne marge (7-9/10)
3-8%   → Marge faible (4-6/10)
< 3%   → Marge critique (0-3/10)
```

**R9. ROE (Return on Equity)**
```
Formule: (Résultat Net / Capitaux Propres) × 100

Interprétation:
≥ 15%  → Excellente (10/10)
10-15% → Bonne (7-9/10)
5-10%  → Moyenne (4-6/10)
< 5%   → Faible (0-3/10)
```

**R10. EBITDA MARGIN**
```
Formule: (EBITDA / CA) × 100

Interprétation:
≥ 20%  → Excellente (10/10)
12-20% → Bonne (7-9/10)
5-12%  → Moyenne (4-6/10)
< 5%   → Faible (0-3/10)
```

### GESTION (20% du score)

**R11. DÉLAI CLIENTS (DSO)**
```
Formule: (Créances Clients / CA TTC) × 360

Interprétation:
< 30j  → Excellent recouvrement (10/10)
30-60j → Bon recouvrement (7-9/10)
60-90j → Recouvrement lent (4-6/10)
> 90j  → Problèmes (0-3/10)
```

**R12. DÉLAI FOURNISSEURS**
```
Formule: (Dettes Fournisseurs / Achats TTC) × 360

Interprétation:
60-90j → Optimal (10/10)
45-60j → Bon (7-9/10)
30-45j → Acceptable (5-6/10)
< 30j  → Trop rapide (perte trésorerie)
> 90j  → Risque relations fournisseurs
```

**R13. ROTATION DES STOCKS**
```
Formule: CA / Stock Moyen

Interprétation:
≥ 10   → Excellente rotation (10/10)
5-10   → Bonne rotation (7-9/10)
2-5    → Rotation lente (4-6/10)
< 2    → Stock dormant (0-3/10)
```

### STRUCTURE FINANCIÈRE (10% du score)

**R14. FONDS DE ROULEMENT NET**
```
Formule: Capitaux Permanents - Actif Immobilisé

Interprétation:
> 0    → Positif (bon signe)
= 0    → Équilibré
< 0    → Négatif (attention)

Note attribuée selon ratio/CA
```

**R15. BFR (Besoin en Fonds de Roulement)**
```
Formule: Actif Circulant - Passif Circulant

Interprétation:
Faible BFR → Bon (moins de besoin trésorerie)
BFR élevé   → Attention (besoin trésorerie important)

Note selon BFR/CA
```

---

## Annexe B: Matrice de Recommandations

Voir section 2.3 (F-03.01)

---

## Annexe C: Glossaire

**ACTIF CIRCULANT**: Actifs convertibles en liquidités < 1 an (stocks, créances, trésorerie)

**BFR**: Besoin en Fonds de Roulement - Trésorerie nécessaire pour financer le cycle d'exploitation

**DSO**: Days Sales Outstanding - Délai moyen de paiement clients

**EBITDA**: Earnings Before Interest, Taxes, Depreciation and Amortization

**ICE**: Identifiant Commun de l'Entreprise (Maroc)

**JWT**: JSON Web Token - Standard pour tokens d'authentification

**Multi-Tenancy**: Architecture permettant à plusieurs clients d'utiliser la même instance applicative avec isolation des données

**RC**: Registre de Commerce

**ROA**: Return on Assets - Rentabilité des actifs

**ROE**: Return on Equity - Rentabilité des capitaux propres

**SaaS**: Software as a Service

---

**FIN DU CAHIER DES CHARGES**

═══════════════════════════════════════════════════════════════════════════════
Version: 3.0 Finale avec Microservices
Date: Décembre 2024
Pages: ~60
═══════════════════════════════════════════════════════════════════════════════
