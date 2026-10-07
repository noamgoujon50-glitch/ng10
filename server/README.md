# VALHALLA RAGE Server

Ce dossier contient le premier vrai serveur de l'application.

Il sert à préparer la version en ligne de VALHALLA RAGE :

- comptes utilisateur ;
- profil sportif ;
- génération de programme personnalisé ;
- coach IA ;
- historique de séance ;
- amis, groupes, conversations et défis ;
- boutique, inventaire, pièces du Valhalla ;
- preuve vidéo privée pour les défis ;
- modération photo/vidéo prévue côté serveur.

## Lancer le serveur sur le PC

Depuis PowerShell :

```powershell
cd C:\Users\noamg\Documents\Codex\2026-10-04\referenced-chatgpt-conversation-this-is-an\work\FitAI-Android\FitAI\server
.\start-valhalla-server.ps1
```

Si une ancienne version du serveur tourne deja, utilise plutot :

```powershell
.\restart-valhalla-server.ps1
```

Le serveur affiche une adresse du type :

```text
http://192.168.x.x:8787
```

Dans l'application Android, cette adresse va dans :

`Profil -> Paramètre -> Serveur IA`

## Heberger le serveur en ligne

Pour que l'application fonctionne hors de ton Wi-Fi et plus tard sur le Play Store, il faudra mettre ce serveur en ligne avec une adresse HTTPS publique.

Les fichiers sont deja prepares :

```text
server/Dockerfile
server/.env.example
render.yaml
docs/server/HEBERGEMENT_RENDER.md
```

Quand le serveur sera en ligne, l'adresse publique ira dans :

```text
Profil -> Paramètre -> Serveur IA
```

## Activer la vraie IA plus tard

La clé ne doit jamais être mise dans l'APK Android.
Elle reste côté serveur.

Pour le moment, on peut laisser cette étape de côté : le serveur fonctionne avec le coach local de secours.

Depuis PowerShell :

```powershell
cd C:\Users\noamg\Documents\Codex\2026-10-04\referenced-chatgpt-conversation-this-is-an\work\FitAI-Android\FitAI\server
.\set-openai-key.ps1 -Restart
```

Ou plus simple : double-cliquer sur :

```text
ACTIVER-IA-OPENAI.cmd
```

Le script demande la clé OpenAI en masqué, l'enregistre dans Windows pour l'utilisateur, puis redémarre le serveur.

Pour vérifier que la vraie IA répond :

```powershell
.\test-openai-ia.ps1
```

Ou double-cliquer sur :

```text
TESTER-IA-OPENAI.cmd
```

Sans clé, le serveur fonctionne quand même avec une réponse locale de secours.

## Données

Les données de test sont enregistrées dans :

```text
server\data\db.json
```

Pour une publication Play Store, il faudra remplacer ce stockage local par une vraie base de données hébergée, par exemple PostgreSQL.

## Test rapide

```powershell
.\test-server.ps1
```

Si tout va bien, le test affiche :

```text
VALHALLA_SERVER_TEST_OK
```
