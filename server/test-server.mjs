import assert from "node:assert/strict";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { mkdir, rm } from "node:fs/promises";
import { createValhallaServer } from "./server.mjs";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const tempDir = path.join(__dirname, `.tmp-test-${Date.now()}`);
await mkdir(tempDir, { recursive: true });

const server = createValhallaServer({ dataFile: path.join(tempDir, "db.json") });
await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
const port = server.address().port;
const base = `http://127.0.0.1:${port}`;

async function api(method, url, body, token) {
  const response = await fetch(base + url, {
    method,
    headers: {
      "content-type": "application/json",
      ...(token ? { authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const data = await response.json();
  if (!response.ok) {
    throw new Error(`${method} ${url} failed: ${response.status} ${JSON.stringify(data)}`);
  }
  return data;
}

try {
  const health = await api("GET", "/health");
  assert.equal(health.ok, true);

  const thomas = await api("POST", "/auth/register", { username: "Thomas", password: "secret123" });
  assert.ok(thomas.token);

  const profile = await api("PUT", "/me/profile", {
    goal: "Prendre du muscle",
    level: "Intermédiaire",
    frequency: "4 fois par semaine",
    place: "En salle",
    equipment: "Machines, haltères et barre",
    sessionTime: "50 minutes",
    exercisePerformanceProfile: "Développé couché|12|||50;Curl biceps||10|3|14;Presse à cuisses||12|4|90",
  }, thomas.token);
  assert.equal(profile.profile.goal, "Prendre du muscle");

  const plan = await api("POST", "/program/generate", { save: true }, thomas.token);
  assert.equal(plan.program.days.length, 7);
  assert.ok(plan.program.days.some((day) => day.exercises.some((exercise) => exercise.includes("poids"))));

  const customPlan = await api("POST", "/program/accept", {
    program: {
      source: "custom",
      days: [{ day: "Lundi", focus: "Test perso", exercises: ["Pompes\n3 séries x 12 reps  •  poids Poids du corps  •  repos 60 s"] }],
    },
  }, thomas.token);
  assert.equal(customPlan.program.source, "custom");

  const currentPlan = await api("GET", "/program/current", null, thomas.token);
  assert.equal(currentPlan.program.source, "custom");

  const coach = await api("POST", "/coach", {
    message: "Donne-moi un tableau adapté",
    profile: profile.profile,
  }, thomas.token);
  assert.equal(coach.ok ?? true, true);
  assert.ok(["show_plan", "none", "easier", "harder"].includes(coach.action));

  const session = await api("POST", "/sessions/complete", { complete: true, exercises: [{ name: "Développé couché", reps: 10, weightKg: 45 }] }, thomas.token);
  assert.equal(session.status, "complete");
  assert.ok(session.xpGain > 0);

  const weight = await api("POST", "/weight", { date: "2026-10-05", weightKg: 74.5, targetWeight: "70" }, thomas.token);
  assert.equal(weight.entry.weightKg, 74.5);

  const weights = await api("GET", "/weight", null, thomas.token);
  assert.equal(weights.entries.length, 1);

  const passwordChange = await api("PUT", "/me/password", { currentPassword: "secret123", newPassword: "secret456" }, thomas.token);
  assert.equal(passwordChange.ok, true);

  const relogin = await api("POST", "/auth/login", { username: "Thomas", password: "secret456" });
  assert.ok(relogin.token);

  const shopState = await api("PUT", "/shop/state", {
    coins: 900,
    hero: "Valkyrie",
    ownedHeroes: ["Viking", "Valkyrie"],
    inventory: ["training_outfit", "valkyrie_training", "bg_prairie", "valkyrie_astral_armor"],
    equipped: ["valkyrie_training", "bg_prairie"],
    backdrop: "bg_prairie",
  }, thomas.token);
  assert.equal(shopState.user.coins, 900);
  assert.equal(shopState.user.avatar.hero, "Valkyrie");
  assert.ok(shopState.user.inventory.includes("valkyrie_astral_armor"));

  const friend = await api("POST", "/auth/register", { username: "Lagertha", password: "secret123" });
  const added = await api("POST", "/friends", { username: "Lagertha" }, thomas.token);
  assert.ok(added.conversationId);

  const message = await api("POST", `/conversations/${added.conversationId}/messages`, { text: "Prêt pour un défi ?" }, thomas.token);
  assert.ok(message.message.id);
  assert.equal(message.message.senderUsername, "Thomas");

  const messages = await api("GET", `/conversations/${added.conversationId}/messages`, null, thomas.token);
  assert.equal(messages.messages.length, 1);
  assert.equal(messages.messages[0].senderUsername, "Thomas");

  const group = await api("POST", "/groups", { name: "Clan du matin", memberUsernames: ["Lagertha"] }, thomas.token);
  assert.ok(group.group.conversationId);
  assert.equal(group.group.members.length, 2);

  const groups = await api("GET", "/groups", null, thomas.token);
  assert.equal(groups.groups.length, 1);
  assert.equal(groups.groups[0].conversationId, group.group.conversationId);

  const challenge = await api("POST", "/battle/challenges", { targetUserId: friend.user.id, exercise: "Pompes", reps: 20, difficulty: "Normal" }, thomas.token);
  assert.equal(challenge.challenge.proofRequired, true);

  const proof = await api("POST", `/battle/challenges/${challenge.challenge.id}/proof`, { fileName: "pompes-test.mp4" }, thomas.token);
  assert.equal(proof.proof.visibleToPublic, false);

  const items = await api("GET", "/shop/items", null, thomas.token);
  assert.ok(items.items.length > 0);

  const exported = await api("GET", "/me/export", null, thomas.token);
  assert.equal(exported.ok, true);
  assert.equal(exported.user.username, "Thomas");
  assert.equal(exported.weightEntries.length, 1);

  const deleted = await api("DELETE", "/me", null, thomas.token);
  assert.equal(deleted.ok, true);

  console.log("VALHALLA_SERVER_TEST_OK");
} finally {
  await new Promise((resolve) => server.close(resolve));
  await rm(tempDir, { recursive: true, force: true });
}
