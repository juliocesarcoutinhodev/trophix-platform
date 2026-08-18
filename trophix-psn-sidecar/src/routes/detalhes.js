const express = require("express");
const { getTitleTrophyGroups } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");
const { createCache } = require("../cache");
const config = require("../config");

const router = express.Router();

// The PSN trophy API only understands NPWR ids (e.g. NPWR24170_00). Titles
// saved from the trophy2 catalog use titleIds (PPSA/CUSA) and have no trophy
// metadata, so we answer 400 instead of trying to query the PSN.
const EH_NPWR = /^NPWR\d+_00$/i;

function isNpwr(npCommunicationId) {
  return EH_NPWR.test(npCommunicationId || "");
}

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
      console.log(`[detalhes] ${key}: falha ao atualizar (${error.message}); servindo cache expirado.`);
      return stale;
    }
    throw error;
  }
}

async function fetchTitleInfo(auth, npCommunicationId) {
  const id = npCommunicationId.toUpperCase();
  const services = [
    serviceCache.get(id) ?? "trophy",
    "trophy2",
    "trophy"
  ].filter((s, i, arr) => arr.indexOf(s) === i);

  let lastError = null;
  for (const npServiceName of services) {
    try {
      const response = await getTitleTrophyGroups(auth, id, { npServiceName });
      serviceCache.set(id, npServiceName);
      return response;
    } catch (error) {
      lastError = error;
      console.log(`[detalhes] ${id}: servico "${npServiceName}" falhou (${error.message}).`);
    }
  }

  throw lastError ?? new Error("Unexpected Error");
}

router.get("/api/jogos/:npCommunicationId/details", async (req, res) => {
  const { npCommunicationId } = req.params;
  const cacheKey = `details:${npCommunicationId.toUpperCase()}`;
  console.log(`[detalhes] GET /api/jogos/${npCommunicationId}/details`);

  if (!isNpwr(npCommunicationId)) {
    console.log(`[detalhes] id sem detalhes de trofeus (${npCommunicationId}); retornando 400.`);
    return res.status(400).json({
      error: "npCommunicationId invalido: esperado um id NPWR (ex.: NPWR24170_00)"
    });
  }

  try {
    const info = await cachedFetch(cacheKey, async () => {
      const { result } = await withAuthorization((auth) =>
        fetchTitleInfo(auth, npCommunicationId)
      );

      const { bronze, silver, gold, platinum } = result.definedTrophies;
      return {
        name: result.trophyTitleName.trim(),
        coverUrl: result.trophyTitleIconUrl,
        platform: result.trophyTitlePlatform,
        totalTrophies: bronze + silver + gold + platinum
      };
    });

    res.json(info);
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[detalhes] Jogo nao encontrado: ${npCommunicationId}`);
      return res.status(404).json({ error: "Jogo nao encontrado na PSN" });
    }
    console.error(`[detalhes] Erro ao buscar detalhes de ${npCommunicationId}:`, error.message);
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

module.exports = router;
