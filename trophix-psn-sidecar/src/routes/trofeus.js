const express = require("express");
const { getTitleTrophies, getUserTrophiesEarnedForTitle } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");

const router = express.Router();

const TIPO_TROFEU = {
  bronze: "Bronze",
  silver: "Silver",
  gold: "Gold",
  platinum: "Platinum"
};

const serviceCache = new Map();

async function withServiceProbe(auth, npCommunicationId, fetchFn) {
  const id = npCommunicationId.toUpperCase();
  const servicos = [
    serviceCache.get(id) ?? "trophy",
    "trophy2",
    "trophy"
  ].filter((s, i, arr) => arr.indexOf(s) === i);

  let lastError = null;
  for (const serviceName of servicos) {
    const response = await fetchFn(auth, id, serviceName);
    if (!response.error) {
      serviceCache.set(id, serviceName);
      return response.trophies;
    }
    lastError = response.error;
    console.log(`[trofeus] ${id}: servico "${serviceName}" falhou (${response.error.message}).`);
  }

  throw new Error(lastError.message || "Unexpected Error");
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
  console.log(`[trofeus] GET /api/jogos/${npCommunicationId}/trofeus`);

  try {
    const { result } = await withAuthorization((auth) =>
      fetchTrophies(auth, npCommunicationId)
    );

    const trofeus = result.map((t) => ({
      idTrofeu: t.trophyId,
      nome: t.trophyName,
      descricao: t.trophyDetail,
      tipo: TIPO_TROFEU[t.trophyType] ?? t.trophyType,
      iconeUrl: t.trophyIconUrl
    }));

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
  console.log(`[trofeus] GET /api/jogos/${npCommunicationId}/trofeus-conquistados/${accountId}`);

  try {
    const { result } = await withAuthorization((auth) =>
      fetchEarnedTrophies(auth, accountId, npCommunicationId)
    );

    const conquistados = result.map((t) => ({
      idTrofeu: t.trophyId,
      conquistado: Boolean(t.earned),
      conquistadoEm: t.earnedDateTime || null
    }));

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