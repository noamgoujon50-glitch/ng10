import http from "node:http";
import os from "node:os";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";
import { existsSync, mkdirSync, readFileSync, writeFileSync } from "node:fs";
import { readFile, rename, writeFile } from "node:fs/promises";
import crypto from "node:crypto";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const APP_NAME = "VALHALLA RAGE";
const DEFAULT_PORT = 8787;
const DATA_DIR = path.resolve(process.env.VALHALLA_DATA_DIR || path.join(__dirname, "data"));
const DB_FILE = path.join(DATA_DIR, "db.json");
const SECRET_FILE = path.join(DATA_DIR, "server-secret.txt");
const PROOF_RETENTION_HOURS = Number(process.env.VALHALLA_PROOF_RETENTION_HOURS || 24);
const OPENAI_MODEL = process.env.OPENAI_MODEL || "gpt-5";
const OPENAI_MODERATION_MODEL = process.env.OPENAI_MODERATION_MODEL || "omni-moderation-latest";
const OPENAI_API_KEY = process.env.OPENAI_API_KEY || "";
const SERVER_SECRET = process.env.VALHALLA_SERVER_SECRET || getOrCreateSecret();

const JSON_HEADERS = {
  "content-type": "application/json; charset=utf-8",
  "access-control-allow-origin": "*",
  "access-control-allow-methods": "GET,POST,PUT,DELETE,OPTIONS",
  "access-control-allow-headers": "authorization,content-type",
};

const EXERCISES = [
  ex("Pompes inclinées", ["push", "chest"], "bodyweight", "home", false, false, "Corps gainé, mains sur support stable, descente contrôlée."),
  ex("Pompes", ["push", "chest"], "bodyweight", "both", false, false, "Corps aligné, ventre serré, descends sans laisser tomber les hanches."),
  ex("Développé couché", ["push", "chest"], "barbell", "gym", true, false, "Omoplates serrées, poignets droits, barre contrôlée jusqu'à la poitrine."),
  ex("Développé incliné", ["push", "chest"], "barbell", "gym", true, false, "Garde les épaules basses, trajectoire régulière, contrôle la descente."),
  ex("Pec deck", ["push", "chest"], "machine", "gym", true, false, "Dos contre le dossier, coudes légèrement fléchis, rapproche les bras sans à-coups."),
  ex("Développé épaules", ["push", "shoulders"], "dumbbell", "both", true, false, "Ventre gainé, poignets droits, ne hausse pas les épaules vers les oreilles."),
  ex("Élévations latérales", ["push", "shoulders"], "dumbbell", "both", true, false, "Lève jusqu'à hauteur d'épaule, coude souple, contrôle la descente."),
  ex("Tirage vertical", ["pull", "back"], "machine", "gym", true, false, "Tire les coudes vers les côtes, buste stable, remonte sans perdre le contrôle."),
  ex("Tirage horizontal", ["pull", "back"], "machine", "gym", true, false, "Dos neutre, tire vers le nombril, serre les omoplates puis relâche lentement."),
  ex("Rowing haltères", ["pull", "back"], "dumbbell", "both", true, false, "Dos neutre, coude vers la hanche, évite de tourner le buste."),
  ex("Rowing un bras", ["pull", "back"], "dumbbell", "both", true, false, "Appui solide, dos plat, tire le coude bas puis contrôle le retour."),
  ex("Rowing avec sac", ["pull", "back"], "bag", "home", true, false, "Sac proche du corps, dos neutre, tire sans donner d'élan."),
  ex("Curl biceps", ["pull", "arms"], "dumbbell", "both", true, false, "Dos droit, coudes près du corps, monte sans balancer puis descends lentement."),
  ex("Extension triceps", ["push", "arms"], "cable", "gym", true, false, "Coudes fixes, épaules basses, tends les bras sans verrouiller brutalement."),
  ex("Squat poids du corps", ["legs"], "bodyweight", "home", false, false, "Pieds stables, genoux alignés avec les pieds, buste solide."),
  ex("Squat haltères", ["legs"], "dumbbell", "both", true, false, "Haltères stables, dos neutre, pousse dans le sol pour remonter."),
  ex("Squat guidé", ["legs"], "machine", "gym", true, false, "Pieds bien placés, descente contrôlée, ne verrouille pas sèchement les genoux."),
  ex("Presse à cuisses", ["legs"], "machine", "gym", true, false, "Dos collé au dossier, genoux dans l'axe, contrôle la descente."),
  ex("Leg extension", ["legs"], "machine", "gym", true, false, "Dos contre le dossier, extension contrôlée, pause courte en haut."),
  ex("Leg curl", ["legs"], "machine", "gym", true, false, "Bassin stable, ramène les talons sans cambrer, retour lent."),
  ex("Fentes arrière", ["legs"], "bodyweight", "both", false, false, "Pas long, buste droit, pousse dans le talon avant."),
  ex("Fentes alternées", ["legs"], "bodyweight", "both", false, false, "Reste stable, genou avant dans l'axe, cadence contrôlée."),
  ex("Soulevé de terre roumain", ["legs", "pull"], "dumbbell", "both", true, false, "Charnière de hanches, dos neutre, charges proches des jambes."),
  ex("Hip thrust", ["legs"], "barbell", "gym", true, false, "Menton légèrement rentré, pousse dans les talons, serre les fessiers en haut."),
  ex("Pont fessier", ["legs"], "bodyweight", "home", false, false, "Pieds proches des fesses, pousse dans les talons, ne cambre pas."),
  ex("Mollets debout", ["legs"], "dumbbell", "both", true, false, "Monte haut, marque une pause, redescends lentement sans rebond."),
  ex("Gainage face", ["core"], "bodyweight", "both", false, true, "Épaules, bassin et chevilles alignés, respire sans laisser le dos s'affaisser."),
  ex("Planche latérale", ["core"], "bodyweight", "both", false, true, "Bassin haut, corps aligné, épaule stable."),
  ex("Gainage dynamique", ["core"], "bodyweight", "both", false, true, "Garde le bassin stable et bouge lentement."),
  ex("Crunch contrôlé", ["core"], "bodyweight", "both", false, false, "Monte avec les abdos, pas avec la nuque, contrôle la descente."),
  ex("Mountain climbers", ["cardio", "core"], "bodyweight", "both", false, true, "Position de pompe solide, hanches stables, accélère seulement si tu restes gainé."),
  ex("Chaise au mur", ["legs"], "bodyweight", "both", false, true, "Dos contre le mur, pieds sous les genoux, remonte avant la douleur."),
  ex("Farmer walk", ["core", "pull"], "dumbbell", "both", true, true, "Grandis-toi, épaules basses, marche sans te pencher."),
  ex("Burpees", ["cardio"], "bodyweight", "both", false, false, "Reste propre avant d'aller vite, option sans saut si nécessaire."),
  ex("Marche rapide", ["cardio"], "bodyweight", "both", false, true, "Intensité où tu peux encore parler, posture droite."),
  ex("Course", ["cardio"], "bodyweight", "both", false, true, "Augmente d'abord la durée, puis la vitesse."),
  ex("Vélo", ["cardio"], "machine", "gym", false, true, "Cadence régulière, respiration maîtrisée, résistance progressive."),
  ex("Mobilité épaules et hanches", ["mobility"], "bodyweight", "both", false, true, "Bouge lentement, sans chercher la douleur."),
];

const SHOP_ITEMS = [
  item("training_outfit", "Tenue viking simple", "Viking", "outfit", "Commun", 0, 1),
  item("fur_shoulders", "Épaulières de fourrure", "Viking", "outfit", "Commun", 260, 6),
  item("war_belt_north", "Ceinture du Nord", "Viking", "outfit", "Commun", 320, 8),
  item("iron_bracers", "Brassards de fer", "Viking", "accessory", "Rare", 650, 18),
  item("runic_axe", "Hache runique", "Viking", "accessory", "Légendaire", 1500, 45),
  item("valkyrie_training", "Tenue valkyrie simple", "Valkyrie", "outfit", "Commun", 0, 1),
  item("silver_circlet", "Diadème argenté", "Valkyrie", "accessory", "Commun", 330, 8),
  item("valkyrie_cape", "Cape céleste", "Valkyrie", "outfit", "Rare", 720, 20),
  item("epic_armor", "Armure épique de valkyrie", "Valkyrie", "outfit", "Très rare", 1150, 34),
  item("mythic_wings", "Ailes mythiques", "Valkyrie", "accessory", "Légendaire", 1500, 45),
  item("mage_training", "Robe runique de magicien", "Magicien", "outfit", "Commun", 0, 1),
  item("ember_staff", "Bâton de braise", "Magicien", "accessory", "Rare", 760, 22),
  item("eclipse_ring", "Anneau d'éclipse", "Magicien", "accessory", "Légendaire", 1500, 45),
  item("sorceress_training", "Robe runique de magicienne", "Magicienne", "outfit", "Commun", 0, 1),
  item("lunar_diadem", "Diadème lunaire", "Magicienne", "accessory", "Commun", 430, 10),
  item("mist_cape", "Cape de brume", "Magicienne", "outfit", "Légendaire", 1500, 45),
  item("bg_prairie", "Prairie", "Tous", "backdrop", "Commun", 0, 1),
  item("bg_sunset", "Coucher de soleil", "Tous", "backdrop", "Commun", 280, 6),
  item("bg_forest", "Forêt", "Tous", "backdrop", "Rare", 650, 18),
  item("bg_luxury_gym", "Salle de sport de luxe", "Tous", "backdrop", "Très rare", 1150, 34),
  item("bg_gods_temple", "Temple des dieux", "Tous", "backdrop", "Légendaire", 1500, 45),
];

function ex(name, groups, equipment, place, weighted, timed, advice) {
  return { id: slug(name), name, groups, equipment, place, weighted, timed, advice };
}

function item(id, name, hero, category, rarity, price, requiredLevel) {
  return { id, name, hero, category, rarity, price, requiredLevel };
}

function getOrCreateSecret() {
  mkdirSync(DATA_DIR, { recursive: true });
  if (existsSync(SECRET_FILE)) return readFileSync(SECRET_FILE, "utf8").trim();
  const secret = crypto.randomBytes(48).toString("base64url");
  writeFileSync(SECRET_FILE, secret, "utf8");
  return secret;
}

function emptyDb() {
  return {
    createdAt: now(),
    users: [],
    conversations: [],
    groups: [],
    challenges: [],
    moderationJobs: [],
    resetCodes: [],
  };
}

async function loadDb() {
  mkdirSync(DATA_DIR, { recursive: true });
  if (!existsSync(DB_FILE)) {
    const db = emptyDb();
    await saveDb(db);
    return db;
  }
  try {
    return JSON.parse(await readFile(DB_FILE, "utf8"));
  } catch {
    const backup = DB_FILE.replace(/\.json$/, `-${Date.now()}.broken.json`);
    await rename(DB_FILE, backup);
    const db = emptyDb();
    await saveDb(db);
    return db;
  }
}

async function saveDb(db) {
  db.updatedAt = now();
  mkdirSync(DATA_DIR, { recursive: true });
  const tmp = `${DB_FILE}.${process.pid}.${Date.now()}.tmp`;
  await writeFile(tmp, JSON.stringify(db, null, 2), "utf8");
  await rename(tmp, DB_FILE);
}

export function createValhallaServer(options = {}) {
  const dataFile = options.dataFile || DB_FILE;
  const dataDir = path.dirname(dataFile);

  async function scopedLoadDb() {
    mkdirSync(dataDir, { recursive: true });
    if (!existsSync(dataFile)) {
      const db = emptyDb();
      await scopedSaveDb(db);
      return db;
    }
    return JSON.parse(await readFile(dataFile, "utf8"));
  }

  async function scopedSaveDb(db) {
    db.updatedAt = now();
    mkdirSync(dataDir, { recursive: true });
    const tmp = `${dataFile}.${process.pid}.${Date.now()}.tmp`;
    await writeFile(tmp, JSON.stringify(db, null, 2), "utf8");
    await rename(tmp, dataFile);
  }

  return http.createServer(async (req, res) => {
    try {
      if (req.method === "OPTIONS") return sendJson(res, 200, { ok: true });
      const url = new URL(req.url || "/", `http://${req.headers.host || "localhost"}`);
      const db = await scopedLoadDb();
      cleanupExpiredProofs(db);

      if (req.method === "GET" && url.pathname === "/health") {
        return sendJson(res, 200, {
          ok: true,
          app: APP_NAME,
          model: OPENAI_MODEL,
          keyConfigured: Boolean(OPENAI_API_KEY),
          users: db.users.length,
          urls: localUrls(Number(process.env.PORT || process.env.VALHALLA_SERVER_PORT || DEFAULT_PORT)),
        });
      }

      if (req.method === "POST" && url.pathname === "/auth/register") {
        const body = await readJson(req);
        const username = cleanUsername(body.username);
        const password = String(body.password || "");
        if (username.length < 3) return problem(res, 400, "Nom d'utilisateur trop court.");
        if (password.length < 6) return problem(res, 400, "Mot de passe trop court : 6 caractères minimum.");
        if (findUserByUsername(db, username)) return problem(res, 409, "Ce nom d'utilisateur existe déjà.");
        const user = createUser(username, password);
        db.users.push(user);
        await scopedSaveDb(db);
        return sendJson(res, 201, sessionPayload(user));
      }

      if (req.method === "POST" && url.pathname === "/auth/login") {
        const body = await readJson(req);
        const user = findUserByUsername(db, cleanUsername(body.username));
        if (!user || !verifyPassword(String(body.password || ""), user.password)) {
          return problem(res, 401, "Nom d'utilisateur ou mot de passe incorrect.");
        }
        user.lastLoginAt = now();
        await scopedSaveDb(db);
        return sendJson(res, 200, sessionPayload(user));
      }

      if (req.method === "POST" && url.pathname === "/auth/forgot-password") {
        const body = await readJson(req);
        const user = findUserByUsername(db, cleanUsername(body.username));
        if (!user) return sendJson(res, 200, { ok: true, message: "Si le compte existe, une récupération est préparée." });
        const code = String(crypto.randomInt(100000, 999999));
        db.resetCodes = db.resetCodes.filter((entry) => entry.userId !== user.id);
        db.resetCodes.push({ userId: user.id, codeHash: hashText(code), createdAt: now(), expiresAt: futureMinutes(15) });
        await scopedSaveDb(db);
        return sendJson(res, 200, {
          ok: true,
          message: "Code de récupération créé. En production il sera envoyé par email ou SMS.",
          devResetCode: process.env.NODE_ENV === "production" ? undefined : code,
        });
      }

      if (req.method === "POST" && url.pathname === "/auth/reset-password") {
        const body = await readJson(req);
        const username = cleanUsername(body.username);
        const user = findUserByUsername(db, username);
        if (!user) return problem(res, 400, "Code invalide.");
        const entry = db.resetCodes.find((candidate) => candidate.userId === user.id && candidate.codeHash === hashText(String(body.code || "")));
        if (!entry || new Date(entry.expiresAt).getTime() < Date.now()) return problem(res, 400, "Code invalide ou expiré.");
        const password = String(body.newPassword || "");
        if (password.length < 6) return problem(res, 400, "Nouveau mot de passe trop court.");
        user.password = hashPassword(password);
        db.resetCodes = db.resetCodes.filter((candidate) => candidate !== entry);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true });
      }

      if (req.method === "POST" && url.pathname === "/coach") {
        const body = await readJson(req);
        const auth = optionalUser(req, db);
        const profile = body.profile || auth?.profile || {};
        const plan = body.currentPlan || body.plan || null;
        const result = await coachReply(body.message || "", profile, plan, body);
        return sendJson(res, 200, result);
      }

      if (req.method === "POST" && url.pathname === "/program/generate") {
        const body = await readJson(req);
        const auth = optionalUser(req, db);
        const profile = { ...(auth?.profile || {}), ...(body.profile || {}) };
        const program = generateProgram(profile, body.options || {});
        if (auth && body.save !== false) {
          auth.currentProgram = program;
          auth.currentProgramWeek = weekKey();
          await scopedSaveDb(db);
        }
        return sendJson(res, 200, { ok: true, program });
      }

      const user = authenticate(req, db);

      if (req.method === "GET" && url.pathname === "/me") {
        return sendJson(res, 200, publicUser(user));
      }

      if (req.method === "GET" && url.pathname === "/me/export") {
        return sendJson(res, 200, exportUserData(db, user));
      }

      if (req.method === "DELETE" && url.pathname === "/me") {
        deleteUserAccount(db, user);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, message: "Compte et donnees serveur supprimes." });
      }

      if (req.method === "PUT" && url.pathname === "/me/profile") {
        const body = await readJson(req);
        user.profile = mergeProfile(user.profile, body.profile || body);
        await scopedSaveDb(db);
        return sendJson(res, 200, publicUser(user));
      }

      if (req.method === "PUT" && url.pathname === "/me/password") {
        const body = await readJson(req);
        if (!verifyPassword(String(body.currentPassword || ""), user.password)) return problem(res, 401, "Mot de passe actuel incorrect.");
        if (String(body.newPassword || "").length < 6) return problem(res, 400, "Nouveau mot de passe trop court.");
        user.password = hashPassword(String(body.newPassword));
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true });
      }

      if (req.method === "PUT" && url.pathname === "/shop/state") {
        const body = await readJson(req);
        const state = syncShopState(user, body);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...state, user: publicUser(user) });
      }

      if (req.method === "GET" && url.pathname === "/program/current") {
        if (!user.currentProgram || user.currentProgramWeek !== weekKey()) {
          user.currentProgram = generateProgram(user.profile, {});
          user.currentProgramWeek = weekKey();
          await scopedSaveDb(db);
        }
        return sendJson(res, 200, { ok: true, program: user.currentProgram });
      }

      if (req.method === "POST" && url.pathname === "/program/accept") {
        const body = await readJson(req);
        user.currentProgram = body.program || generateProgram(user.profile, {});
        user.currentProgramWeek = user.currentProgram.week || weekKey();
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, program: user.currentProgram });
      }

      if (req.method === "POST" && url.pathname === "/sessions/complete") {
        const body = await readJson(req);
        const result = completeSession(user, body);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...result, user: publicUser(user) });
      }

      if (req.method === "GET" && url.pathname === "/history") {
        return sendJson(res, 200, { ok: true, sessions: user.sessions || [], streak: currentStreak(user.sessions || []) });
      }

      if (req.method === "GET" && url.pathname === "/weight") {
        return sendJson(res, 200, { ok: true, entries: user.weightEntries || [], targetWeight: user.profile?.weightTarget || "" });
      }

      if (req.method === "POST" && url.pathname === "/weight") {
        const body = await readJson(req);
        const result = upsertWeightEntry(user, body);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...result, user: publicUser(user) });
      }

      if (req.method === "GET" && url.pathname === "/battle/arena") {
        return sendJson(res, 200, { ok: true, arena: arenaForLevel(playerLevel(user.xp)), streak: currentStreak(user.sessions || []) });
      }

      if (req.method === "GET" && url.pathname === "/battle/community-challenges") {
        return sendJson(res, 200, { ok: true, challenges: communityChallenges(playerLevel(user.xp)) });
      }

      if (req.method === "GET" && url.pathname === "/friends") {
        return sendJson(res, 200, { ok: true, friends: friendList(db, user) });
      }

      if (req.method === "POST" && url.pathname === "/friends") {
        const body = await readJson(req);
        const friend = findUserByUsername(db, cleanUsername(body.username));
        if (!friend) return problem(res, 404, "Aucun utilisateur trouvé avec ce nom.");
        if (friend.id === user.id) return problem(res, 400, "Tu es déjà dans ta propre équipe.");
        user.friends = addUnique(user.friends || [], friend.id);
        friend.friends = addUnique(friend.friends || [], user.id);
        const conversation = directConversation(db, user.id, friend.id);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, friend: publicMiniUser(friend), conversationId: conversation.id });
      }

      const friendDelete = url.pathname.match(/^\/friends\/([^/]+)$/);
      if (req.method === "DELETE" && friendDelete) {
        const friendId = friendDelete[1];
        user.friends = (user.friends || []).filter((id) => id !== friendId);
        const friend = db.users.find((candidate) => candidate.id === friendId);
        if (friend) friend.friends = (friend.friends || []).filter((id) => id !== user.id);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true });
      }

      if (req.method === "GET" && url.pathname === "/friends/suggestions") {
        return sendJson(res, 200, { ok: true, suggestions: friendSuggestions(db, user) });
      }

      if (req.method === "GET" && url.pathname === "/groups") {
        return sendJson(res, 200, { ok: true, groups: groupList(db, user) });
      }

      if (req.method === "POST" && url.pathname === "/groups") {
        const body = await readJson(req);
        const memberUserIds = (body.memberUsernames || [])
          .map((username) => findUserByUsername(db, cleanUsername(username))?.id)
          .filter(Boolean);
        const memberIds = [user.id, ...(body.memberIds || []), ...memberUserIds].filter(Boolean);
        const allowed = new Set([user.id, ...(user.friends || [])]);
        const members = [...new Set(memberIds.filter((id) => allowed.has(id)))];
        if (members.length < 2) return problem(res, 400, "Ajoute au moins un ami au groupe.");
        const group = { id: id("grp"), name: String(body.name || "Groupe Valhalla").slice(0, 60), ownerId: user.id, members, createdAt: now() };
        db.groups.push(group);
        const conversation = { id: id("conv"), type: "group", groupId: group.id, members, messages: [], createdAt: now() };
        db.conversations.push(conversation);
        await scopedSaveDb(db);
        return sendJson(res, 201, { ok: true, group: groupView(db, group, conversation) });
      }

      const messagesMatch = url.pathname.match(/^\/conversations\/([^/]+)\/messages$/);
      if (messagesMatch && req.method === "GET") {
        const conversation = visibleConversation(db, user, messagesMatch[1]);
        return sendJson(res, 200, { ok: true, conversationId: conversation.id, messages: messageList(db, conversation.messages || []) });
      }

      if (messagesMatch && req.method === "POST") {
        const conversation = visibleConversation(db, user, messagesMatch[1]);
        const body = await readJson(req);
        const message = {
          id: id("msg"),
          senderId: user.id,
          text: String(body.text || "").slice(0, 1500),
          attachment: body.attachment ? moderationAttachment(body.attachment) : null,
          createdAt: now(),
        };
        conversation.messages = [...(conversation.messages || []), message];
        await scopedSaveDb(db);
        return sendJson(res, 201, { ok: true, message: messageView(db, message) });
      }

      if (req.method === "POST" && url.pathname === "/battle/challenges") {
        const body = await readJson(req);
        const challenge = createChallenge(db, user, body);
        db.challenges.push(challenge);
        await scopedSaveDb(db);
        return sendJson(res, 201, { ok: true, challenge });
      }

      const proofMatch = url.pathname.match(/^\/battle\/challenges\/([^/]+)\/proof$/);
      if (proofMatch && req.method === "POST") {
        const body = await readJson(req);
        const challenge = db.challenges.find((entry) => entry.id === proofMatch[1]);
        if (!challenge) return problem(res, 404, "Défi introuvable.");
        if (!challenge.participants.includes(user.id)) return problem(res, 403, "Tu n'es pas dans ce défi.");
        const proof = createProofJob(challenge, user, body);
        await scopedSaveDb(db);
        return sendJson(res, 202, { ok: true, proof, message: "Vidéo reçue. Elle est privée, vérifiée par IA, puis supprimée après vérification." });
      }

      const claimMatch = url.pathname.match(/^\/battle\/challenges\/([^/]+)\/claim$/);
      if (claimMatch && req.method === "POST") {
        const challenge = db.challenges.find((entry) => entry.id === claimMatch[1]);
        if (!challenge) return problem(res, 404, "Défi introuvable.");
        const reward = claimChallengeReward(user, challenge);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...reward, user: publicUser(user) });
      }

      if (req.method === "GET" && url.pathname === "/shop/items") {
        return sendJson(res, 200, { ok: true, items: shopForHero(user.avatar.hero) });
      }

      if (req.method === "GET" && url.pathname === "/shop/inventory") {
        return sendJson(res, 200, { ok: true, inventory: user.inventory || [], equipped: user.avatar.equipped || [], coins: user.coins });
      }

      if (req.method === "POST" && url.pathname === "/shop/purchase") {
        const body = await readJson(req);
        const result = purchaseItem(user, String(body.itemId || ""));
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...result, user: publicUser(user) });
      }

      if (req.method === "POST" && url.pathname === "/shop/equip") {
        const body = await readJson(req);
        const result = toggleEquip(user, String(body.itemId || ""));
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...result, user: publicUser(user) });
      }

      if (req.method === "POST" && url.pathname === "/coins/free-ad") {
        const result = claimFreeCoins(user);
        await scopedSaveDb(db);
        return sendJson(res, 200, { ok: true, ...result, user: publicUser(user) });
      }

      if (req.method === "POST" && url.pathname === "/coins/purchase-intent") {
        const body = await readJson(req);
        const pack = coinPack(String(body.pack || ""));
        return sendJson(res, 200, { ok: true, pack, message: "Paiement test préparé. En production, Google Play Billing finalisera l'achat." });
      }

      if (req.method === "POST" && url.pathname === "/moderation/photo-check") {
        const body = await readJson(req);
        const moderation = await moderateSharedMedia(body);
        db.moderationJobs.push(moderation);
        await scopedSaveDb(db);
        return sendJson(res, 202, { ok: true, moderation });
      }

      return problem(res, 404, "Route inconnue.");
    } catch (error) {
      const status = Number(error.status || 500);
      return problem(res, status, status >= 500 ? `Erreur serveur : ${error.message}` : error.message);
    }
  });
}

function createUser(username, password) {
  const hero = "Viking";
  return {
    id: id("usr"),
    username,
    password: hashPassword(password),
    createdAt: now(),
    lastLoginAt: null,
    xp: 0,
    coins: 300,
    friends: [],
    sessions: [],
    weightEntries: [],
    currentProgram: null,
    currentProgramWeek: null,
    inventory: ["training_outfit", "bg_prairie"],
    avatar: {
      hero,
      ownedHeroes: ["Viking", "Valkyrie", "Magicien", "Magicienne"],
      equipped: ["training_outfit", "bg_prairie"],
      backdrop: "bg_prairie",
    },
    profile: {
      userName: username,
      goal: "Se remettre en forme",
      level: "Débutant",
      frequency: "3 fois par semaine",
      place: "À la maison",
      equipment: "Poids du corps",
      sessionTime: "45 minutes",
      eating: "Correcte",
      foodChange: "Petit à petit",
      gender: "Homme",
      height: "",
      weight: "",
      weightTarget: "",
      exercisePerformanceProfile: "",
    },
  };
}

function mergeProfile(current, incoming) {
  const allowed = [
    "userName", "goal", "level", "frequency", "place", "equipment", "sessionTime",
    "eating", "foodChange", "height", "weight", "weightTarget", "gender",
    "exercisePerformanceProfile", "gymStrengthProfile", "exercisePerformances",
  ];
  const next = { ...(current || {}) };
  for (const key of allowed) {
    if (incoming[key] !== undefined) next[key] = incoming[key];
  }
  return next;
}

export function generateProgram(profile = {}, options = {}) {
  const frequency = clamp(trainingDaysFromFrequency(profile.frequency), 1, 6);
  const minutes = sessionMinutes(profile.sessionTime);
  const performances = parsePerformances(profile);
  const available = availableExercises(profile);
  const templates = templatesForFrequency(frequency, profile.goal);
  const days = ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"];
  const programDays = [];
  let templateIndex = 0;

  for (let i = 0; i < days.length; i++) {
    if (!trainingDayIndex(i, frequency)) {
      programDays.push({
        day: days[i],
        focus: i === 6 ? "Récupération et bilan" : "Repos actif",
        exercises: [displayExercise(restExercise(i === 6 ? "Bilan + mobilité douce" : "Marche légère", i === 6 ? "10 min" : "20 min"))],
      });
      continue;
    }
    const template = templates[templateIndex % templates.length];
    templateIndex++;
    const picked = pickExercises(template.groups, available, minutes, i);
    const exercises = picked.map((exercise, index) => prescribeExercise(exercise, profile, performances, index, options.adjustment || 0));
    programDays.push({ day: days[i], focus: template.focus, exercises: exercises.map(displayExercise) });
  }

  return {
    id: id("plan"),
    week: weekKey(),
    createdAt: now(),
    summary: programSummary(profile, performances),
    days: programDays,
    notes: [
      "Échauffement conseillé : 5 à 8 minutes avant chaque séance.",
      "Garde 1 à 3 répétitions en réserve sur les premières semaines.",
      "Si douleur nette ou malaise, arrête la séance et demande un avis médical si nécessaire.",
    ],
  };
}

function templatesForFrequency(frequency, goal = "") {
  const lower = normalize(goal);
  const cardioBias = lower.includes("poids") || lower.includes("cardio") || lower.includes("tonique");
  const base = [
    { focus: "Haut du corps", groups: ["push", "pull", "core"] },
    { focus: "Jambes + gainage", groups: ["legs", "legs", "core"] },
    { focus: cardioBias ? "Cardio + renforcement" : "Full body contrôlé", groups: cardioBias ? ["cardio", "legs", "core", "push"] : ["push", "pull", "legs", "core"] },
    { focus: "Dos + bras", groups: ["pull", "pull", "arms", "core"] },
    { focus: "Jambes + cardio", groups: ["legs", "legs", "cardio", "core"] },
    { focus: "Full body technique", groups: ["push", "pull", "legs", "mobility"] },
  ];
  return base.slice(0, Math.max(1, frequency));
}

function trainingDayIndex(index, frequency) {
  const sets = {
    1: [2],
    2: [0, 3],
    3: [0, 2, 4],
    4: [0, 1, 3, 5],
    5: [0, 1, 2, 4, 5],
    6: [0, 1, 2, 3, 4, 5],
  };
  return sets[frequency].includes(index);
}

function availableExercises(profile) {
  const place = normalize(profile.place || "");
  const equipment = normalize(profile.equipment || "");
  return EXERCISES.filter((exercise) => {
    const placeOk = exercise.place === "both"
      || (exercise.place === "gym" && (place.includes("salle") || place.includes("deux")))
      || (exercise.place === "home" && (!place.includes("salle") || place.includes("deux")));
    if (!placeOk) return false;
    if (exercise.equipment === "bodyweight") return true;
    if (exercise.equipment === "machine" || exercise.equipment === "barbell" || exercise.equipment === "cable") return place.includes("salle") || place.includes("deux");
    if (exercise.equipment === "dumbbell") return equipment.includes("haltere") || equipment.includes("poids") || place.includes("salle") || place.includes("deux");
    if (exercise.equipment === "bag") return equipment.includes("sac") || equipment.includes("maison") || !place.includes("salle");
    return true;
  });
}

function pickExercises(groups, available, minutes, salt) {
  const maxCount = minutes <= 25 ? 4 : minutes <= 40 ? 5 : minutes <= 55 ? 6 : 7;
  const picked = [];
  const used = new Set();
  for (const group of groups) {
    const candidates = available.filter((exercise) => exercise.groups.includes(group) && !used.has(exercise.id));
    if (candidates.length === 0) continue;
    const candidate = candidates[(salt + picked.length) % candidates.length];
    picked.push(candidate);
    used.add(candidate.id);
  }
  const fillerGroups = ["core", "mobility", "cardio", "push", "pull", "legs"];
  for (const group of fillerGroups) {
    if (picked.length >= maxCount) break;
    const candidates = available.filter((exercise) => exercise.groups.includes(group) && !used.has(exercise.id));
    if (candidates.length === 0) continue;
    const candidate = candidates[(salt + picked.length) % candidates.length];
    picked.push(candidate);
    used.add(candidate.id);
  }
  return picked.slice(0, maxCount);
}

function prescribeExercise(exercise, profile, performances, index, adjustment) {
  const perf = matchPerformance(performances, exercise.name);
  const goal = normalize(profile.goal || "");
  const level = normalize(profile.level || "");
  const advanced = level.includes("avance");
  const beginner = level.includes("debut");
  const muscle = goal.includes("muscle") || goal.includes("masse");
  const weightLoss = goal.includes("poids") || goal.includes("maigr") || goal.includes("tonique");

  let sets = beginner ? 2 : advanced ? 4 : 3;
  if (muscle && !beginner) sets += 1;
  if (adjustment < 0) sets -= 1;
  if (adjustment > 0 && !beginner) sets += 1;
  sets = clamp(sets, 2, 5);

  if (exercise.timed) {
    const seconds = timedTarget(exercise, perf, beginner, advanced, weightLoss, adjustment);
    return { ...exercise, sets, reps: `${seconds} s`, weight: exercise.weighted ? targetWeight(exercise, perf, goal, level) : "Aucun", rest: restSeconds(goal, level, adjustment), advice: exercise.advice };
  }

  const reps = repsTarget(exercise, perf, goal, level, adjustment);
  return {
    ...exercise,
    sets,
    reps: `${reps} reps`,
    weight: exercise.weighted ? targetWeight(exercise, perf, goal, level) : "Poids du corps",
    rest: restSeconds(goal, level, adjustment),
    advice: exercise.advice,
    order: index + 1,
  };
}

function repsTarget(exercise, perf, goal, level, adjustment) {
  const normalizedGoal = normalize(goal);
  let target = normalizedGoal.includes("muscle") || normalizedGoal.includes("masse") ? 10 : 12;
  if (normalizedGoal.includes("poids") || normalizedGoal.includes("tonique")) target = 14;
  if (normalize(level).includes("avance") && exercise.weighted) target = Math.min(target, 10);
  target += adjustment * 2;

  const maxReps = perf?.repsPerSet || perf?.maxTotal || 0;
  if (maxReps > 0) target = Math.min(target, Math.max(2, maxReps - 2));
  if (maxReps > 0 && maxReps <= 5) target = Math.max(2, maxReps - 1);
  return clamp(target, 3, 20);
}

function timedTarget(exercise, perf, beginner, advanced, weightLoss, adjustment) {
  let seconds = beginner ? 25 : advanced ? 50 : 35;
  if (weightLoss || exercise.groups.includes("cardio")) seconds += 10;
  if (perf?.maxTotal) seconds = Math.min(seconds, Math.max(15, perf.maxTotal - 10));
  seconds += adjustment * 10;
  return clamp(seconds, 15, 120);
}

function targetWeight(exercise, perf, goal, level) {
  if (!exercise.weighted) return "Poids du corps";
  if (!perf?.weightKg) return "À renseigner";
  const maxReps = perf.repsPerSet || perf.maxTotal || 10;
  const oneRm = perf.weightKg * (1 + maxReps / 30);
  const normalizedGoal = normalize(goal);
  const advanced = normalize(level).includes("avance");
  let percent = normalizedGoal.includes("muscle") || normalizedGoal.includes("masse") ? 0.72 : 0.65;
  if (advanced) percent += 0.04;
  if (normalizedGoal.includes("poids") || normalizedGoal.includes("tonique")) percent -= 0.05;
  const weight = roundToStep(oneRm * percent, 2.5);
  return `${Math.max(1, weight)} kg`;
}

function restSeconds(goal, level, adjustment) {
  const normalizedGoal = normalize(goal);
  let rest = normalizedGoal.includes("muscle") || normalizedGoal.includes("masse") ? 90 : 75;
  if (normalize(level).includes("avance")) rest += 15;
  if (normalizedGoal.includes("poids") || normalizedGoal.includes("tonique")) rest -= 15;
  rest -= adjustment * 10;
  return `${clamp(rest, 45, 150)} s`;
}

function restExercise(name, reps) {
  return { name, sets: 1, reps, weight: "Aucun", rest: "Libre", advice: "Reste léger, respire, prépare la prochaine séance." };
}

function displayExercise(exercise) {
  return `${exercise.name}\n${exercise.sets} séries x ${exercise.reps}  •  poids ${exercise.weight}  •  repos ${exercise.rest}`;
}

function parsePerformances(profile) {
  if (Array.isArray(profile.exercisePerformances)) {
    return dedupePerformances(profile.exercisePerformances.map((entry) => normalizePerformance(entry)).filter(Boolean));
  }
  const entries = [];
  const detailed = String(profile.exercisePerformanceProfile || "");
  const legacyGym = String(profile.gymStrengthProfile || "");
  if (detailed.trim()) entries.push(...parsePerformanceLines(detailed, false));
  if (legacyGym.trim()) entries.push(...parsePerformanceLines(legacyGym, true));
  return dedupePerformances(entries);
}

function parsePerformanceLines(raw, legacyGymFormat) {
  return raw.split(";").map((line) => {
    const [name, maxTotal, repsPerSet, series, weightKg] = line.split("|");
    if (legacyGymFormat && weightKg === undefined) {
      return normalizePerformance({ name, repsPerSet, weightKg: maxTotal });
    }
    return normalizePerformance({ name, maxTotal, repsPerSet, series, weightKg });
  }).filter(Boolean);
}

function normalizePerformance(entry) {
  const name = String(entry.exercise || entry.name || "").trim();
  if (!name) return null;
  return {
    name,
    key: slug(name),
    maxTotal: numberOrZero(entry.maxTotal),
    repsPerSet: numberOrZero(entry.repsPerSet || entry.reps),
    series: numberOrZero(entry.series),
    weightKg: numberOrZero(entry.weightKg || entry.load || entry.weight),
  };
}

function dedupePerformances(entries) {
  const seen = new Set();
  return entries.filter((entry) => {
    if (!entry || seen.has(entry.key)) return false;
    seen.add(entry.key);
    return true;
  });
}

function matchPerformance(performances, exerciseName) {
  const key = slug(exerciseName);
  return performances.find((entry) => entry.key === key || entry.key.includes(key) || key.includes(entry.key));
}

async function coachReply(message, profile, currentPlan, rawPayload) {
  const cleanMessage = String(message || "").trim();
  const generated = generateProgram(profile, {
    adjustment: difficultyDirection(cleanMessage),
  });
  if (!OPENAI_API_KEY) return localCoachReply(cleanMessage, profile, generated);

  const instructions = [
    `Tu es le coach IA de ${APP_NAME}.`,
    "Réponds en français, court, clair, motivant et utile.",
    "Tu peux expliquer les exercices, adapter le programme, ou confirmer le plan.",
    "Si l'utilisateur demande un tableau, réponds avec action=show_plan.",
    "Si c'est trop dur, action=easier. Si c'est trop facile, action=harder. S'il valide, action=keep.",
    "Pour douleur, malaise ou blessure : recommande d'arrêter, de réduire l'intensité et de demander un avis médical si nécessaire. Ne pose pas de diagnostic.",
    "Réponds uniquement en JSON valide au format {\"reply\":\"...\",\"action\":\"none|easier|harder|keep|show_plan\"}.",
  ].join("\n");

  try {
    const response = await fetch("https://api.openai.com/v1/responses", {
      method: "POST",
      headers: {
        authorization: `Bearer ${OPENAI_API_KEY}`,
        "content-type": "application/json",
      },
      body: JSON.stringify({
        model: OPENAI_MODEL,
        instructions,
        input: JSON.stringify({ message: cleanMessage, profile, currentPlan, suggestedProgram: generated, rawPayload }),
        store: false,
        max_output_tokens: 700,
      }),
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.error?.message || `OpenAI HTTP ${response.status}`);
    const parsed = parseJsonFromText(extractOutputText(data));
    return {
      reply: String(parsed.reply || "").trim() || localCoachReply(cleanMessage, profile, generated).reply,
      action: String(parsed.action || "none").trim().toLowerCase(),
      source: "openai",
      program: ["show_plan", "easier", "harder"].includes(String(parsed.action || "").trim().toLowerCase()) ? generated : undefined,
    };
  } catch (error) {
    const fallback = localCoachReply(cleanMessage, profile, generated);
    return { ...fallback, source: "local-after-openai-error", serverNote: error.message };
  }
}

function localCoachReply(message, profile, program) {
  const lower = normalize(message);
  if (lower.includes("tableau") || lower.includes("programme") || lower.includes("planning")) {
    return {
      reply: "Je t'ai préparé un tableau personnalisé avec ton objectif, ton niveau, ton matériel et tes performances. Vérifie-le, puis dis-moi s'il faut le rendre plus facile ou plus intense.",
      action: "show_plan",
      source: "local",
      program,
    };
  }
  const advice = adviceForMessage(lower);
  if (advice) return { reply: advice, action: "none", source: "local" };
  if (difficultyDirection(message) < 0) {
    return { reply: "On allège : baisse une série, garde plus de repos et choisis une charge qui laisse une technique propre. La régularité vaut mieux qu'une séance héroïque impossible à refaire.", action: "easier", source: "local", program };
  }
  if (difficultyDirection(message) > 0) {
    return { reply: "Tu peux progresser : ajoute un peu de charge ou une série sur les exercices maîtrisés, sans perdre la qualité du mouvement.", action: "harder", source: "local", program };
  }
  return {
    reply: `Je suis prêt. Demande-moi un tableau, un conseil sur un exercice, ou une adaptation plus facile/plus difficile selon ton ressenti.`,
    action: "none",
    source: "local",
  };
}

function adviceForMessage(lower) {
  const exercise = EXERCISES.find((entry) => lower.includes(normalize(entry.name).split(" ")[0]) || normalize(entry.name).split(" ").some((part) => part.length > 4 && lower.includes(part)));
  if (!exercise) return null;
  return `Conseil ${exercise.name} : ${exercise.advice}`;
}

async function moderateSharedMedia(body) {
  const base = {
    id: id("mod"),
    createdAt: now(),
    type: body.type || "photo",
    status: "queued",
    visibility: "private",
    deleteAfter: futureHours(PROOF_RETENTION_HOURS),
    policy: "Pas de nudité explicite. Les zones intimes doivent rester cachées. Les vidéos de défi ne sont pas publiques.",
  };
  if (!OPENAI_API_KEY || !body.text) {
    return { ...base, status: "queued_ai_review", aiChecked: false, note: "La vérification réelle sera active avec une clé IA et un stockage média sécurisé." };
  }
  try {
    const response = await fetch("https://api.openai.com/v1/moderations", {
      method: "POST",
      headers: {
        authorization: `Bearer ${OPENAI_API_KEY}`,
        "content-type": "application/json",
      },
      body: JSON.stringify({ model: OPENAI_MODERATION_MODEL, input: String(body.text).slice(0, 2000) }),
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) throw new Error(data.error?.message || `Moderation HTTP ${response.status}`);
    const flagged = Boolean(data.results?.[0]?.flagged);
    return { ...base, status: flagged ? "blocked" : "approved", aiChecked: true, flagged };
  } catch (error) {
    return { ...base, status: "queued_ai_review", aiChecked: false, note: error.message };
  }
}

function completeSession(user, body) {
  const date = String(body.date || dateKey(new Date()));
  const status = body.status || (body.noCourage ? "missed" : body.complete ? "complete" : "partial");
  let xpGain = 0;
  let coinGain = 0;
  let coinPenalty = 0;
  if (status === "complete") {
    xpGain = 120;
    coinGain = 20;
  } else if (status === "partial") {
    xpGain = 65;
    coinGain = 8;
  } else {
    coinPenalty = 15;
  }
  user.xp = Math.max(0, (user.xp || 0) + xpGain);
  user.coins = Math.max(0, (user.coins || 0) + coinGain - coinPenalty);
  user.sessions = (user.sessions || []).filter((entry) => entry.date !== date);
  user.sessions.push({
    id: id("ses"),
    date,
    status,
    exercises: Array.isArray(body.exercises) ? body.exercises : [],
    xpGain,
    coinGain,
    coinPenalty,
    createdAt: now(),
  });
  const streak = currentStreak(user.sessions);
  const bonus = streakBonus(streak);
  if (bonus.coins > 0 || bonus.xp > 0) {
    user.xp += bonus.xp;
    user.coins += bonus.coins;
  }
  return {
    status,
    xpGain: xpGain + bonus.xp,
    coinGain: coinGain + bonus.coins,
    coinPenalty,
    streak,
    message: status === "complete"
      ? "Séance validée. Le coach te félicite et propose d'augmenter progressivement la cadence ou la charge."
      : status === "partial"
        ? "Séance partielle validée. Le coach va adapter sans te casser le rythme."
        : "Ça arrive. Le coach te motive à revenir demain, avec une petite pénalité pour garder l'enjeu.",
  };
}

function upsertWeightEntry(user, body) {
  const date = String(body.date || dateKey(new Date())).slice(0, 10);
  const weightKg = numberOrZero(body.weightKg || body.weight || body.value);
  if (!weightKg) throw httpError(400, "Poids invalide.");
  const targetWeight = body.targetWeight !== undefined ? String(body.targetWeight).trim().slice(0, 16) : undefined;
  user.weightEntries = (user.weightEntries || []).filter((entry) => entry.date !== date);
  user.weightEntries.push({ date, weightKg, savedAt: now() });
  user.weightEntries.sort((a, b) => String(a.date).localeCompare(String(b.date)));
  user.profile = user.profile || {};
  user.profile.weight = String(weightKg);
  if (targetWeight !== undefined) user.profile.weightTarget = targetWeight;
  return { entry: user.weightEntries.find((entry) => entry.date === date), entries: user.weightEntries };
}

function createChallenge(db, user, body) {
  const targetIds = [...new Set([...(body.targetUserIds || []), ...(body.targetUserId ? [body.targetUserId] : [])])];
  let participants = [user.id, ...targetIds];
  if (body.groupId) {
    const group = db.groups.find((entry) => entry.id === body.groupId && entry.members.includes(user.id));
    if (!group) throw httpError(404, "Groupe introuvable.");
    participants = [...new Set([...participants, ...group.members])];
  }
  if (participants.length < 2) throw httpError(400, "Choisis au moins un ami ou un groupe.");
  const difficulty = String(body.difficulty || "Normal");
  return {
    id: id("chl"),
    ownerId: user.id,
    participants,
    groupId: body.groupId || null,
    exercise: String(body.exercise || "Pompes").slice(0, 80),
    reps: numberOrZero(body.reps),
    timeSeconds: numberOrZero(body.timeSeconds),
    difficulty,
    rewardCoins: rewardForDifficulty(difficulty),
    proofRequired: true,
    status: "waiting_proof",
    proofs: [],
    createdAt: now(),
  };
}

function createProofJob(challenge, user, body) {
  const proof = {
    id: id("proof"),
    userId: user.id,
    fileName: String(body.fileName || "preuve-video").slice(0, 160),
    status: "queued_ai_review",
    aiChecked: false,
    visibleToPublic: false,
    storedMedia: false,
    deleteAfter: futureHours(PROOF_RETENTION_HOURS),
    note: "Le prototype ne conserve pas la vidéo. En production, elle sera stockée temporairement, vérifiée par IA, puis supprimée.",
    createdAt: now(),
  };
  challenge.proofs.push(proof);
  return proof;
}

function claimChallengeReward(user, challenge) {
  if (!challenge.participants.includes(user.id)) throw httpError(403, "Tu n'es pas dans ce défi.");
  challenge.claimedBy = challenge.claimedBy || [];
  if (challenge.claimedBy.includes(user.id)) throw httpError(409, "Récompense déjà récupérée.");
  const hasProof = (challenge.proofs || []).some((proof) => proof.userId === user.id);
  if (!hasProof) throw httpError(400, "Preuve vidéo obligatoire avant récompense.");
  const reward = challenge.rewardCoins || rewardForDifficulty(challenge.difficulty);
  user.coins = (user.coins || 0) + reward;
  user.xp = (user.xp || 0) + Math.round(reward * 2);
  challenge.claimedBy.push(user.id);
  return {
    rewardCoins: reward,
    rewardXp: Math.round(reward * 2),
    message: `Félicitations, défi validé : +${reward} pièces du Valhalla.`,
    canDoubleWithAd: true,
  };
}

function directConversation(db, a, b) {
  const members = [a, b].sort();
  let conversation = db.conversations.find((entry) => entry.type === "direct" && sameMembers(entry.members, members));
  if (!conversation) {
    conversation = { id: id("conv"), type: "direct", members, messages: [], createdAt: now() };
    db.conversations.push(conversation);
  }
  return conversation;
}

function visibleConversation(db, user, conversationId) {
  const conversation = db.conversations.find((entry) => entry.id === conversationId);
  if (!conversation) throw httpError(404, "Conversation introuvable.");
  if (!conversation.members.includes(user.id)) throw httpError(403, "Conversation privée.");
  return conversation;
}

function groupConversation(db, group) {
  let conversation = db.conversations.find((entry) => entry.type === "group" && entry.groupId === group.id);
  if (!conversation) {
    conversation = { id: id("conv"), type: "group", groupId: group.id, members: [...(group.members || [])], messages: [], createdAt: now() };
    db.conversations.push(conversation);
  }
  return conversation;
}

function groupList(db, user) {
  return (db.groups || [])
    .filter((group) => (group.members || []).includes(user.id))
    .map((group) => groupView(db, group, groupConversation(db, group)));
}

function groupView(db, group, conversation) {
  return {
    id: group.id,
    name: group.name,
    ownerId: group.ownerId,
    conversationId: conversation.id,
    members: (group.members || [])
      .map((memberId) => db.users.find((candidate) => candidate.id === memberId))
      .filter(Boolean)
      .map(publicMiniUser),
    createdAt: group.createdAt,
  };
}

function messageList(db, messages) {
  return messages.map((message) => messageView(db, message));
}

function messageView(db, message) {
  const sender = db.users.find((candidate) => candidate.id === message.senderId);
  return {
    ...message,
    senderUsername: sender?.username || "Sportif",
  };
}

function friendList(db, user) {
  return (user.friends || []).map((id) => db.users.find((candidate) => candidate.id === id)).filter(Boolean).map((friend) => {
    const conversation = directConversation(db, user.id, friend.id);
    return { ...publicMiniUser(friend), conversationId: conversation.id };
  });
}

function friendSuggestions(db, user) {
  const level = playerLevel(user.xp);
  const friendSet = new Set(user.friends || []);
  return db.users
    .filter((candidate) => candidate.id !== user.id && !friendSet.has(candidate.id))
    .map((candidate) => ({ ...publicMiniUser(candidate), levelDistance: Math.abs(playerLevel(candidate.xp) - level) }))
    .sort((a, b) => a.levelDistance - b.levelDistance)
    .slice(0, 10);
}

function deleteUserAccount(db, user) {
  db.users = db.users.filter((candidate) => candidate.id !== user.id);
  for (const other of db.users) {
    other.friends = (other.friends || []).filter((id) => id !== user.id);
  }
  db.conversations = (db.conversations || []).filter((conversation) => !(conversation.members || []).includes(user.id));
  db.groups = (db.groups || [])
    .map((group) => ({ ...group, members: (group.members || []).filter((id) => id !== user.id) }))
    .filter((group) => group.members.length > 0 && group.ownerId !== user.id);
  db.challenges = (db.challenges || []).filter((challenge) => !(challenge.participants || []).includes(user.id));
  db.moderationJobs = (db.moderationJobs || []).filter((job) => job.userId !== user.id);
  db.resetCodes = (db.resetCodes || []).filter((entry) => entry.userId !== user.id);
}

function exportUserData(db, user) {
  return {
    ok: true,
    exportedAt: now(),
    user: publicUser(user),
    sessions: user.sessions || [],
    weightEntries: user.weightEntries || [],
    friends: friendList(db, user),
    groups: (db.groups || []).filter((group) => (group.members || []).includes(user.id)),
    conversations: (db.conversations || []).filter((conversation) => (conversation.members || []).includes(user.id)),
    challenges: (db.challenges || []).filter((challenge) => (challenge.participants || []).includes(user.id)),
  };
}

function communityChallenges(level) {
  if (level <= 20) {
    return [
      community("Facile", "Pompes propres", 10, 0, "Pompes"),
      community("Normal", "Gainage stable", 0, 30, "Gainage face"),
      community("Difficile", "Squats contrôlés", 35, 0, "Squat poids du corps"),
    ];
  }
  if (level <= 35) {
    return [
      community("Normal", "Pompes de guerrier", 35, 0, "Pompes"),
      community("Difficile", "Gainage long", 0, 90, "Gainage face"),
      community("Extrême", "Circuit jambes", 80, 0, "Fentes alternées"),
    ];
  }
  return [
    community("Difficile", "Pompes élite", 70, 0, "Pompes"),
    community("Extrême", "Gainage mythique", 0, 180, "Gainage face"),
    community("Extrême", "Burpees du Valhalla", 60, 0, "Burpees"),
  ];
}

function community(difficulty, title, reps, timeSeconds, exercise) {
  return { id: id("com"), difficulty, title, exercise, reps, timeSeconds, rewardCoins: rewardForDifficulty(difficulty), proofRequired: true };
}

function rewardForDifficulty(difficulty) {
  const value = normalize(difficulty);
  if (value.includes("extreme")) return 100;
  if (value.includes("difficile")) return 50;
  if (value.includes("normal")) return 30;
  return 15;
}

function arenaForLevel(level) {
  if (level <= 20) return { status: "Débutant", name: "Prairie tranquille", animated: false };
  if (level <= 25) return { status: "Intermédiaire", name: "Désert bouillant", animated: false };
  if (level <= 30) return { status: "Intermédiaire", name: "Montagne enneigée", animated: false };
  if (level <= 35) return { status: "Intermédiaire", name: "Porte du paradis", animated: false };
  if (level <= 45) return { status: "Avancé", name: "Vaisseau spatial de musculation", animated: true };
  return { status: "Légende", name: "Mont Olympe flamboyant", animated: true };
}

function shopForHero(hero) {
  return SHOP_ITEMS.filter((entry) => entry.hero === "Tous" || entry.hero === hero);
}

function purchaseItem(user, itemId) {
  const entry = SHOP_ITEMS.find((candidate) => candidate.id === itemId);
  if (!entry) throw httpError(404, "Objet introuvable.");
  if (entry.hero !== "Tous" && entry.hero !== user.avatar.hero) throw httpError(400, "Cet objet n'est pas compatible avec ce héros.");
  if ((user.inventory || []).includes(entry.id)) return { item: entry, inventory: user.inventory, message: "Objet déjà acquis." };
  if (playerLevel(user.xp) < entry.requiredLevel) throw httpError(403, "Vous n'avez pas le niveau requis.");
  if ((user.coins || 0) < entry.price) throw httpError(402, "Vous n'avez pas assez de pièces.");
  user.coins -= entry.price;
  user.inventory = addUnique(user.inventory || [], entry.id);
  return { item: entry, inventory: user.inventory, message: `${entry.name} ajouté à l'inventaire.` };
}

function toggleEquip(user, itemId) {
  const entry = SHOP_ITEMS.find((candidate) => candidate.id === itemId);
  if (!entry) throw httpError(404, "Objet introuvable.");
  if (!(user.inventory || []).includes(itemId)) throw httpError(403, "Objet non acquis.");
  user.avatar.equipped = user.avatar.equipped || [];
  if (user.avatar.equipped.includes(itemId)) {
    user.avatar.equipped = user.avatar.equipped.filter((id) => id !== itemId);
    return { equipped: user.avatar.equipped, message: `${entry.name} retiré.` };
  }
  if (entry.category === "outfit" || entry.category === "backdrop") {
    user.avatar.equipped = user.avatar.equipped.filter((id) => {
      const old = SHOP_ITEMS.find((candidate) => candidate.id === id);
      return !old || old.category !== entry.category;
    });
  }
  user.avatar.equipped.push(itemId);
  if (entry.category === "backdrop") user.avatar.backdrop = itemId;
  return { equipped: user.avatar.equipped, message: `${entry.name} équipé.` };
}

function syncShopState(user, body) {
  user.avatar = user.avatar || {};
  const inventory = cleanStringList(body.inventory);
  const equipped = cleanStringList(body.equipped);
  const ownedHeroes = cleanStringList(body.ownedHeroes);
  if (Number.isFinite(Number(body.coins)) && Number(body.coins) >= 0) {
    user.coins = Math.floor(Number(body.coins));
  }
  for (const itemId of inventory) user.inventory = addUnique(user.inventory || [], itemId);
  user.avatar.equipped = equipped.filter((itemId) => (user.inventory || []).includes(itemId));
  user.avatar.ownedHeroes = unionStrings(user.avatar.ownedHeroes || [], ownedHeroes);
  const hero = String(body.hero || user.avatar.hero || "Viking").trim().slice(0, 32);
  if (hero) {
    user.avatar.hero = hero;
    user.avatar.ownedHeroes = addUnique(user.avatar.ownedHeroes || [], hero);
  }
  const backdrop = String(body.backdrop || "").trim().slice(0, 64);
  if (backdrop) user.avatar.backdrop = backdrop;
  return {
    coins: user.coins || 0,
    inventory: user.inventory || [],
    equipped: user.avatar.equipped || [],
    avatar: user.avatar,
  };
}

function claimFreeCoins(user) {
  const today = dateKey(new Date());
  user.coinAds = user.coinAds || { date: today, count: 0 };
  if (user.coinAds.date !== today) user.coinAds = { date: today, count: 0 };
  if (user.coinAds.count >= 2) throw httpError(429, "Limite atteinte : 2 pubs gratuites par jour.");
  user.coinAds.count++;
  user.coins = (user.coins || 0) + 75;
  return { coinsAdded: 75, remainingToday: 2 - user.coinAds.count };
}

function coinPack(pack) {
  const packs = {
    small: { pack: "small", coins: 350, price: "1,99 €" },
    medium: { pack: "medium", coins: 1000, price: "5,99 €" },
    large: { pack: "large", coins: 2000, price: "9,99 €" },
  };
  return packs[pack] || packs.small;
}

function publicUser(user) {
  return {
    id: user.id,
    username: user.username,
    profile: user.profile,
    xp: user.xp || 0,
    level: playerLevel(user.xp || 0),
    coins: user.coins || 0,
    avatar: user.avatar,
    inventory: user.inventory || [],
    weightEntries: user.weightEntries || [],
    createdAt: user.createdAt,
  };
}

function publicMiniUser(user) {
  return { id: user.id, username: user.username, level: playerLevel(user.xp || 0), hero: user.avatar?.hero || "Viking" };
}

function sessionPayload(user) {
  return { ok: true, token: signToken(user), user: publicUser(user) };
}

function authenticate(req, db) {
  const user = optionalUser(req, db);
  if (!user) throw httpError(401, "Connexion requise.");
  return user;
}

function optionalUser(req, db) {
  const header = req.headers.authorization || "";
  const match = /^Bearer\s+(.+)$/i.exec(header);
  if (!match) return null;
  const payload = verifyToken(match[1]);
  if (!payload?.sub) return null;
  return db.users.find((user) => user.id === payload.sub) || null;
}

function signToken(user) {
  const payload = { sub: user.id, username: user.username, iat: Date.now(), exp: Date.now() + 1000 * 60 * 60 * 24 * 30 };
  const encoded = base64url(JSON.stringify(payload));
  const signature = hmac(encoded);
  return `${encoded}.${signature}`;
}

function verifyToken(token) {
  const [encoded, signature] = String(token || "").split(".");
  if (!encoded || !signature || hmac(encoded) !== signature) return null;
  try {
    const payload = JSON.parse(Buffer.from(encoded, "base64url").toString("utf8"));
    if (payload.exp && payload.exp < Date.now()) return null;
    return payload;
  } catch {
    return null;
  }
}

function hashPassword(password) {
  const salt = crypto.randomBytes(16).toString("base64url");
  const hash = crypto.scryptSync(String(password), salt, 64).toString("base64url");
  return { salt, hash };
}

function verifyPassword(password, stored) {
  if (!stored?.salt || !stored?.hash) return false;
  const hash = crypto.scryptSync(String(password), stored.salt, 64).toString("base64url");
  return timingSafeEqual(hash, stored.hash);
}

function timingSafeEqual(a, b) {
  const left = Buffer.from(String(a));
  const right = Buffer.from(String(b));
  if (left.length !== right.length) return false;
  return crypto.timingSafeEqual(left, right);
}

function hmac(value) {
  return crypto.createHmac("sha256", SERVER_SECRET).update(value).digest("base64url");
}

function hashText(value) {
  return crypto.createHash("sha256").update(String(value)).digest("base64url");
}

function cleanupExpiredProofs(db) {
  const cutoff = Date.now();
  for (const challenge of db.challenges || []) {
    challenge.proofs = (challenge.proofs || []).map((proof) => {
      if (proof.deleteAfter && new Date(proof.deleteAfter).getTime() < cutoff) {
        return { ...proof, fileName: null, status: proof.status === "approved" ? "approved_deleted" : "expired_deleted", storedMedia: false };
      }
      return proof;
    });
  }
}

function moderationAttachment(attachment) {
  return {
    id: id("att"),
    type: String(attachment.type || "photo").slice(0, 40),
    fileName: String(attachment.fileName || "media").slice(0, 160),
    status: "queued_ai_review",
    visibleToPublic: false,
    deleteAfter: futureHours(PROOF_RETENTION_HOURS),
  };
}

function currentStreak(sessions) {
  const completeDays = new Set((sessions || []).filter((entry) => entry.status === "complete").map((entry) => entry.date));
  let streak = 0;
  const day = new Date();
  for (let i = 0; i < 120; i++) {
    const key = dateKey(day);
    if (!completeDays.has(key)) break;
    streak++;
    day.setDate(day.getDate() - 1);
  }
  return streak;
}

function streakBonus(streak) {
  if (streak >= 50) return { xp: 250, coins: 100 };
  if (streak >= 30) return { xp: 150, coins: 30 };
  if (streak >= 15) return { xp: 90, coins: 5 };
  if (streak >= 10) return { xp: 120, coins: 0 };
  return { xp: 0, coins: 0 };
}

function playerLevel(xp) {
  return Math.max(1, Math.floor((Number(xp) || 0) / 2000) + 1);
}

function trainingDaysFromFrequency(value) {
  const text = normalize(value || "");
  const match = text.match(/\d+/);
  if (match) return Number(match[0]);
  if (text.includes("plus")) return 5;
  return 3;
}

function sessionMinutes(value) {
  const match = String(value || "").match(/\d+/);
  if (!match) return 45;
  return clamp(Number(match[0]), 15, 90);
}

function programSummary(profile, performances) {
  return {
    goal: profile.goal || "Objectif général",
    level: profile.level || "Débutant",
    frequency: profile.frequency || "3 fois par semaine",
    sessionTime: profile.sessionTime || "45 minutes",
    performanceEntries: performances.length,
  };
}

function difficultyDirection(message) {
  const lower = normalize(message);
  if (lower.includes("trop dur") || lower.includes("difficile") || lower.includes("impossible") || lower.includes("facile")) {
    if (lower.includes("trop facile") || lower.includes("plus dur") || lower.includes("augmente")) return 1;
    return -1;
  }
  if (lower.includes("plus intense") || lower.includes("plus lourd")) return 1;
  return 0;
}

function findUserByUsername(db, username) {
  const key = normalize(username);
  return db.users.find((user) => normalize(user.username) === key);
}

function cleanUsername(value) {
  return String(value || "").trim().replace(/\s+/g, "_").slice(0, 32);
}

function sameMembers(a, b) {
  return [...a].sort().join("|") === [...b].sort().join("|");
}

function slug(value) {
  return normalize(value).replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "");
}

function normalize(value) {
  return String(value || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .trim();
}

function numberOrZero(value) {
  const normalized = String(value ?? "").replace(",", ".").trim();
  if (!normalized) return 0;
  const number = Number(normalized);
  return Number.isFinite(number) && number > 0 ? number : 0;
}

function roundToStep(value, step) {
  return Math.round(value / step) * step;
}

function clamp(value, min, max) {
  return Math.max(min, Math.min(max, Number(value) || min));
}

function addUnique(list, value) {
  return [...new Set([...(list || []), value])];
}

function unionStrings(first, second) {
  let result = [...(first || [])];
  for (const value of second || []) result = addUnique(result, value);
  return result;
}

function cleanStringList(value) {
  const source = Array.isArray(value) ? value : String(value || "").split("|");
  const result = [];
  for (const raw of source) {
    const clean = String(raw || "").trim().slice(0, 64);
    if (clean && !result.includes(clean)) result.push(clean);
  }
  return result.slice(0, 300);
}

function id(prefix) {
  return `${prefix}_${crypto.randomUUID().replace(/-/g, "").slice(0, 18)}`;
}

function now() {
  return new Date().toISOString();
}

function futureMinutes(minutes) {
  return new Date(Date.now() + minutes * 60 * 1000).toISOString();
}

function futureHours(hours) {
  return new Date(Date.now() + hours * 60 * 60 * 1000).toISOString();
}

function dateKey(date) {
  return date instanceof Date ? date.toISOString().slice(0, 10) : String(date).slice(0, 10);
}

function weekKey() {
  const date = new Date();
  const day = date.getDay() || 7;
  date.setDate(date.getDate() - day + 1);
  return dateKey(date);
}

function base64url(value) {
  return Buffer.from(value).toString("base64url");
}

function extractOutputText(data) {
  if (typeof data.output_text === "string") return data.output_text;
  const parts = [];
  for (const item of data.output || []) {
    for (const content of item.content || []) {
      if (content.text) parts.push(content.text);
    }
  }
  return parts.join("\n").trim();
}

function parseJsonFromText(text) {
  const cleaned = String(text || "").replace(/^```json\s*/i, "").replace(/^```\s*/i, "").replace(/\s*```$/i, "").trim();
  return JSON.parse(cleaned);
}

async function readJson(req) {
  const text = await readBody(req);
  if (!text) return {};
  try {
    return JSON.parse(text);
  } catch {
    throw httpError(400, "JSON invalide.");
  }
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    let body = "";
    req.setEncoding("utf8");
    req.on("data", (chunk) => {
      body += chunk;
      if (body.length > 2_000_000) {
        reject(httpError(413, "Requête trop lourde."));
        req.destroy();
      }
    });
    req.on("end", () => resolve(body));
    req.on("error", reject);
  });
}

function sendJson(res, status, data) {
  res.writeHead(status, JSON_HEADERS);
  res.end(JSON.stringify(data));
}

function problem(res, status, message) {
  return sendJson(res, status, { ok: false, error: message });
}

function httpError(status, message) {
  const error = new Error(message);
  error.status = status;
  return error;
}

function localUrls(port) {
  const urls = [`http://127.0.0.1:${port}`];
  for (const entries of Object.values(os.networkInterfaces())) {
    for (const entry of entries || []) {
      if (entry.family === "IPv4" && !entry.internal) urls.push(`http://${entry.address}:${port}`);
    }
  }
  return [...new Set(urls)];
}

export async function startServer(port = Number(process.env.PORT || process.env.VALHALLA_SERVER_PORT || DEFAULT_PORT)) {
  await loadDb();
  const server = createValhallaServer();

  await new Promise((resolve, reject) => {
    const onError = (error) => {
      server.off("listening", onListening);
      reject(error);
    };
    const onListening = () => {
      server.off("error", onError);
      resolve();
    };
    server.once("error", onError);
    server.listen(port, "0.0.0.0", onListening);
  });

  const address = server.address();
  const actualPort = typeof address === "object" && address ? address.port : port;
  console.log(`${APP_NAME} server started`);
  console.log(`OpenAI key configured: ${Boolean(OPENAI_API_KEY)}`);
  console.log(`Model: ${OPENAI_MODEL}`);
  for (const url of localUrls(actualPort)) console.log(`URL: ${url}`);
  return server;
}

if (process.argv[1] && import.meta.url === pathToFileURL(process.argv[1]).href) {
  startServer().catch((error) => {
    console.error(error);
    process.exit(1);
  });
}
