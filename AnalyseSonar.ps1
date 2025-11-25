# Script d'analyse et correction des issues SonarCloud
# Organisation: ericfunman
# Projet: ericfunman_Maestor

param(
    [switch]$Fetch,
    [switch]$Fix,
    [int]$IssueNumber = 0,
    [string]$Commit = ""
)

$SONAR_PROJECT_KEY = "ericfunman_Maestor"
$SONAR_ORGANIZATION = "ericfunman"
$SONAR_URL = "https://sonarcloud.io"
$API_URL = "https://sonarcloud.io/api"

function Write-ColorOutput {
    param([string]$Color, [string]$Message)
    $colors = @{
        'Red' = [ConsoleColor]::Red
        'Green' = [ConsoleColor]::Green
        'Yellow' = [ConsoleColor]::Yellow
        'Cyan' = [ConsoleColor]::Cyan
        'White' = [ConsoleColor]::White
    }
    Write-Host $Message -ForegroundColor $colors[$Color]
}

function Get-SonarToken {
    if ($env:SONAR_TOKEN) {
        return $env:SONAR_TOKEN
    }
    Write-ColorOutput "Yellow" "Token SONAR_TOKEN non defini. Mode anonyme."
    return ""
}

function Get-SonarIssues {
    param([string]$Token, [string]$CommitSha)
    
    Write-ColorOutput "Cyan" "`n=== Recuperation des issues SonarCloud ===`n"
    
    if ($CommitSha) {
        Write-ColorOutput "Yellow" "Commit SHA: $CommitSha"
    }
    
    $headers = @{}
    if ($Token) {
        $base64Token = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("${Token}:"))
        $headers["Authorization"] = "Basic $base64Token"
    }
    
    try {
        # Construction de l'URL avec paramètres
        $apiUrl = "$API_URL/issues/search?componentKeys=$SONAR_PROJECT_KEY&organization=$SONAR_ORGANIZATION&ps=100&resolved=false"
        
        # Récupérer d'abord les informations de la dernière analyse
        if ($CommitSha) {
            $analysisUrl = "$API_URL/project_analyses/search?project=$SONAR_PROJECT_KEY&ps=10"
            $analysisResponse = Invoke-RestMethod -Uri $analysisUrl -Headers $headers -Method Get
            
            $targetAnalysis = $analysisResponse.analyses | Where-Object { $_.revision -eq $CommitSha } | Select-Object -First 1
            
            if ($targetAnalysis) {
                Write-ColorOutput "Green" "Analyse trouvee pour le commit $CommitSha (date: $($targetAnalysis.date))`n"
                # Filtrer les issues depuis cette analyse
                $apiUrl += "&sinceLeakPeriod=true"
            } else {
                Write-ColorOutput "Yellow" "Aucune analyse trouvee pour le commit $CommitSha"
                Write-ColorOutput "Yellow" "Dernier commit analyse: $($analysisResponse.analyses[0].revision)`n"
            }
        }
        
        $response = Invoke-RestMethod -Uri $apiUrl -Headers $headers -Method Get
        
        Write-ColorOutput "Green" "Nombre total d'issues: $($response.total)"
        Write-ColorOutput "Green" "Issues recuperees: $($response.issues.Count)`n"
        
        $bySeverity = $response.issues | Group-Object -Property severity
        Write-ColorOutput "White" "Repartition par severite:"
        foreach ($group in $bySeverity | Sort-Object -Property Name) {
            $color = switch ($group.Name) {
                "BLOCKER" { "Red" }
                "CRITICAL" { "Red" }
                "MAJOR" { "Yellow" }
                "MINOR" { "Cyan" }
                "INFO" { "White" }
                default { "White" }
            }
            Write-ColorOutput $color "  $($group.Name): $($group.Count)"
        }
        
        $byType = $response.issues | Group-Object -Property type
        Write-ColorOutput "White" "`nRepartition par type:"
        foreach ($group in $byType | Sort-Object -Property Name) {
            Write-ColorOutput "Cyan" "  $($group.Name): $($group.Count)"
        }
        
        $outputFile = "sonar-issues.json"
        $response | ConvertTo-Json -Depth 10 | Out-File -FilePath $outputFile -Encoding UTF8
        Write-ColorOutput "Green" "`nIssues sauvegardees dans: $outputFile"
        
        Create-IssuesReport $response.issues
        
        return $response.issues
    }
    catch {
        Write-ColorOutput "Red" "Erreur: $_"
        Write-ColorOutput "Yellow" "Consultez: $SONAR_URL/project/issues?id=$SONAR_PROJECT_KEY"
        return @()
    }
}

function Create-IssuesReport {
    param($Issues)
    
    $reportFile = "sonar-issues-report.txt"
    $report = "RAPPORT SONARCLOUD - MAESTROR`n"
    $report += "Date: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')`n"
    $report += "TOTAL: $($Issues.Count) issues`n`n"
    
    $index = 1
    foreach ($issue in $Issues | Sort-Object -Property severity, type) {
        $report += "ISSUE #$index - [$($issue.severity)] $($issue.type)`n"
        $report += "Regle: $($issue.rule)`n"
        $report += "Message: $($issue.message)`n"
        $report += "Fichier: $($issue.component -replace '.*:', '')`n"
        $report += "Ligne: $($issue.line)`n`n"
        $index++
    }
    
    $report | Out-File -FilePath $reportFile -Encoding UTF8
    Write-ColorOutput "Green" "Rapport sauvegarde dans: $reportFile"
}

function Show-Issue {
    param([int]$Number, [array]$Issues)
    
    if ($Number -lt 1 -or $Number -gt $Issues.Count) {
        Write-ColorOutput "Red" "Numero invalide: 1-$($Issues.Count)"
        return
    }
    
    $issue = $Issues[$Number - 1]
    
    Write-ColorOutput "Cyan" "`n=== ISSUE #$Number ==="
    Write-ColorOutput "Yellow" "Severite: $($issue.severity)"
    Write-ColorOutput "Yellow" "Type: $($issue.type)"
    Write-ColorOutput "White" "Regle: $($issue.rule)"
    Write-ColorOutput "White" "Message: $($issue.message)"
    Write-ColorOutput "White" "Fichier: $($issue.component -replace '.*:', '')"
    Write-ColorOutput "White" "Ligne: $($issue.line)"
}

function Show-Menu {
    param([array]$Issues)
    
    Write-ColorOutput "Cyan" "`n=== MENU ==="
    Write-ColorOutput "White" "1. Lister toutes les issues"
    Write-ColorOutput "White" "2. Afficher une issue"
    Write-ColorOutput "White" "3. Ouvrir SonarCloud"
    Write-ColorOutput "White" "0. Quitter"
    
    $choice = Read-Host "`nChoix"
    
    switch ($choice) {
        "1" {
            $index = 1
            foreach ($issue in $Issues) {
                $color = if ($issue.severity -in @("BLOCKER", "CRITICAL")) { "Red" } elseif ($issue.severity -eq "MAJOR") { "Yellow" } else { "White" }
                Write-ColorOutput $color "#$index [$($issue.severity)] $($issue.message)"
                $index++
            }
        }
        "2" {
            $num = Read-Host "Numero"
            Show-Issue -Number ([int]$num) -Issues $Issues
        }
        "3" {
            Start-Process "$SONAR_URL/project/issues?id=$SONAR_PROJECT_KEY"
        }
        "0" {
            return $false
        }
    }
    
    return $true
}

Write-ColorOutput "Green" "ANALYSE SONARCLOUD - MAESTROR"

$token = Get-SonarToken
$issues = Get-SonarIssues -Token $token -CommitSha $Commit

if ($issues.Count -eq 0) {
    Write-ColorOutput "Yellow" "Aucune issue trouvee"
    exit
}

$continue = $true
while ($continue) {
    $continue = Show-Menu -Issues $issues
}

Write-ColorOutput "Green" "`nRapports generes:"
Write-ColorOutput "White" "  - sonar-issues.json"
Write-ColorOutput "White" "  - sonar-issues-report.txt"
