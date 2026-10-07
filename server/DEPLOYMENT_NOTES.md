# Deploiement serveur - VALHALLA RAGE

Le serveur est pret pour les tests locaux et pour une premiere mise en ligne de test.

## Ce qui est pret

- Route de sante : `GET /health`.
- Comptes, profils, historique, poids, programme, boutique, amis, groupes, messages et defis.
- Demarrage via `VALHALLA_SERVER_PORT` en local ou `PORT` chez un hebergeur.
- Configuration par variables d'environnement.
- Image Docker de base avec `Dockerfile`.
- Blueprint Render avec `render.yaml`.
- Donnees locales separees via `VALHALLA_DATA_DIR`.

## Fichiers utiles

- `server.mjs` : serveur principal.
- `.env.example` : variables a configurer chez l'hebergeur.
- `Dockerfile` : image serveur deployable.
- `.dockerignore` : evite d'envoyer les donnees locales de test.
- `../render.yaml` : configuration Render pour creer le serveur depuis le depot.
- `start-valhalla-server.ps1` : demarrage local sur PC.
- `restart-valhalla-server.ps1` : redemarrage local propre.
- `test-server.ps1` : test serveur local.

## Variables d'environnement

```text
NODE_ENV=production
PORT=8787
VALHALLA_DATA_DIR=/app/data
VALHALLA_SERVER_SECRET=secret-long-en-production
VALHALLA_PROOF_RETENTION_HOURS=24
OPENAI_MODEL=gpt-5
OPENAI_MODERATION_MODEL=omni-moderation-latest
```

La cle OpenAI reste optionnelle pour le moment. Sans cle, le coach utilise le mode local.
Si on reactive la vraie IA plus tard, ajouter `OPENAI_API_KEY` manuellement chez l'hebergeur.

## Commandes locales

```powershell
.\start-valhalla-server.ps1
.\test-server.ps1
```

## Hebergement Render

Le fichier `render.yaml` permet de creer un service web Docker nomme `valhalla-rage-server`.
Le serveur lit automatiquement `PORT` si l'hebergeur le fournit, puis repond sur `/health`.

Apres deploiement, Render donnera une adresse publique en HTTPS du type :

```text
https://valhalla-rage-server.onrender.com
```

Cette adresse sera a coller dans l'application Android :

```text
Profil -> Parametre -> Serveur IA
```

## Commandes Docker locales

Depuis le dossier `server` :

```powershell
docker build -t valhalla-rage-server .
docker run --rm -p 8787:8787 -e VALHALLA_SERVER_SECRET="change-moi" -v valhalla-data:/app/data valhalla-rage-server
```

Puis tester :

```text
http://127.0.0.1:8787/health
```

## A remplacer avant production

- Remplacer `data/db.json` par une vraie base de donnees hebergee.
- Mettre le serveur derriere HTTPS public.
- Generer un vrai `VALHALLA_SERVER_SECRET` long et secret.
- Ajouter des limites de requetes par compte/IP.
- Brancher Google Play Billing pour les pieces payantes.
- Remplacer la preuve video simulee par un stockage temporaire securise.
- Ajouter une vraie moderation photo/video avant publication sociale.
- Envoyer les codes de recuperation par email ou SMS.

## Regles de securite

- Ne jamais mettre `OPENAI_API_KEY` dans l'APK Android.
- Ne jamais exposer `data/db.json`.
- Ne jamais publier une cle de signature Android.
- Garder les videos de defis privees et temporaires.
- Supprimer les medias de verification apres controle.
