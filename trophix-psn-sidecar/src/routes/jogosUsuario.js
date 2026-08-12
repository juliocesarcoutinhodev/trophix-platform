const express = require("express");
const { getUserTitles } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");

const router = express.Router();

const somar = (counts) =>
  counts.bronze + counts.silver + counts.gold + counts.platinum;

function formatarJogo(title) {
  return {
    npCommunicationId: title.npCommunicationId,
    nome: title.trophyTitleName,
    imagemUrl: title.trophyTitleIconUrl,
    plataforma: title.trophyTitlePlatform.split(",").map((p) => p.trim()),
    progresso: title.progress,
    trofeusConquistados: somar(title.earnedTrophies),
    trofeusTotais: somar(title.definedTrophies),
    ultimaJogadaEm: title.lastUpdatedDateTime,
    trofeus: {
      bronze: title.earnedTrophies.bronze,
      prata: title.earnedTrophies.silver,
      ouro: title.earnedTrophies.gold,
      platina: title.earnedTrophies.platinum
    }
  };
}

async function buscarTodosOsJogos(auth, accountId) {
  const LIMITE = 800;
  const jogos = [];
  let offset = 0;

  while (true) {
    const { trophyTitles, totalItemCount } = await getUserTitles(
      auth,
      accountId,
      { limit: LIMITE, offset }
    );
    jogos.push(...trophyTitles);
    offset += trophyTitles.length;
    if (offset >= totalItemCount || trophyTitles.length === 0) break;
  }

  return jogos;
}

router.get("/api/jogos-usuario/:accountId", async (req, res) => {
  const { accountId } = req.params;
  console.log(`[jogos-usuario] GET /api/jogos-usuario/${accountId}`);

  try {
    const { result } = await withAuthorization((auth) =>
      buscarTodosOsJogos(auth, accountId)
    );

    res.json(result.map(formatarJogo));
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[jogos-usuario] Conta nao encontrada ou privada: ${accountId}`);
      return res.status(404).json({ error: "Conta nao encontrada ou privada" });
    }
    console.error(`[jogos-usuario] Erro ao buscar jogos de ${accountId}:`, error.message);
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

module.exports = router;