const express = require("express");
const { getTitleTrophies, getUserTrophiesEarnedForTitle } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");
const { createCache } = require("../cache");
const config = require("../config");

const router = express.Router();

const TIPO_TROFEU = {
  bronze: "Bronze",
  silver: "Silver",
  gold: "Gold",
  platinum: "Platinum"
};

const serviceCache = new Map();
const dataCache = createCache(config.trophyCacheTtlMs);

async function cachedFetch(key, fetchFn) {
  const cached = dataCache.get(key);
  if (cached !== undefined) return cached;

  try {
    const value = await fetchFn();
    dataCache.set(key, value);
    return value;
  } catch (error) {
    const stale = dataCache.getStale(key);
    if (stale !== undefined) {
      console.log(`[trofeus] ${key}: falha ao atualizar (${error.message}); servindo cache expirado.`);
      return stale;
    }
    throw error;
  }
}

async function withServiceProbe(auth, npCommunicationId, fetchFn) {
  const id = npCommunicationId.toUpperCase();
  const servicos = [
    serviceCache.get(id) ?? "trophy",
    "trophy2",
    "trophy"
  ].filter((s, i, arr) => arr.indexOf(s) === i);

  let lastError = null;
  for (const serviceName of servicos) {
    let response;
    try {
      response = await fetchFn(auth, id, serviceName);
    } catch (error) {
      lastError = error;
      console.log(`[trofeus] ${id}: servico "${serviceName}" falhou (${error.message}).`);
      continue;
    }
    if (!response.error) {
      serviceCache.set(id, serviceName);
      return response.trophies;
    }
    lastError = response.error;
    console.log(`[trofeus] ${id}: servico "${serviceName}" falhou (${response.error.message}).`);
  }

  throw new Error(lastError?.message || "Unexpected Error");
}

const fetchTrophies = (auth, npCommunicationId) =>
  withServiceProbe(auth, npCommunicationId, (auth, id, npServiceName) =>
    getTitleTrophies(auth, id, "all", { npServiceName })
  );

const fetchEarnedTrophies = (auth, accountId, npCommunicationId) =>
  withServiceProbe(auth, npCommunicationId, (auth, id, npServiceName) =>
    getUserTrophiesEarnedForTitle(auth, accountId, id, "all", { npServiceName })
  );

router.get("/api/jogos/:npCommunicationId/trofeus", async (req, res) => {
  const { npCommunicationId } = req.params;
  const cacheKey = `catalog:${npCommunicationId.toUpperCase()}`;
  console.log(`[trofeus] GET /api/jogos/${npCommunicationId}/trofeus`);

  try {
    const trofeus = await cachedFetch(cacheKey, async () => {
      const { result } = await withAuthorization((auth) =>
        fetchTrophies(auth, npCommunicationId)
      );

      return result.map((t) => ({
        idTrofeu: t.trophyId,
        nome: t.trophyName,
        descricao: t.trophyDetail,
        tipo: TIPO_TROFEU[t.trophyType] ?? t.trophyType,
        iconeUrl: t.trophyIconUrl
      }));
    });

    res.json(trofeus);
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[trofeus] Jogo nao encontrado: ${npCommunicationId}`);
      return res.status(404).json({ error: "Jogo nao encontrado na PSN" });
    }
    console.error(`[trofeus] Erro ao buscar trofeus de ${npCommunicationId}:`, error.message);
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

router.get("/api/jogos/:npCommunicationId/trofeus-conquistados/:accountId", async (req, res) => {
  const { npCommunicationId, accountId } = req.params;
  const cacheKey = `earned:${npCommunicationId.toUpperCase()}:${accountId}`;
  console.log(`[trofeus] GET /api/jogos/${npCommunicationId}/trofeus-conquistados/${accountId}`);

  try {
    const conquistados = await cachedFetch(cacheKey, async () => {
      const { result } = await withAuthorization((auth) =>
        fetchEarnedTrophies(auth, accountId, npCommunicationId)
      );

      return result.map((t) => ({
        idTrofeu: t.trophyId,
        conquistado: Boolean(t.earned),
        conquistadoEm: t.earnedDateTime || null,
        raridade: t.trophyEarnedRate ?? null
      }));
    });

    res.json(conquistados);
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[trofeus] Jogo ou usuario nao encontrado: ${npCommunicationId}/${accountId}`);
      return res.status(404).json({ error: "Jogo ou usuario nao encontrado na PSN" });
    }
    console.error(
      `[trofeus] Erro ao buscar trofeus conquistados de ${npCommunicationId}/${accountId}:`,
      error.message
    );
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

module.exports = router;
