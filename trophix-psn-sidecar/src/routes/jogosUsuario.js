const express = require("express");
const { getUserTitles, getUserPlayedGames } = require("psn-api");
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

function normalizarNome(nome) {
  return (nome || "")
    .toLowerCase()
    .replace(/[™®©]+/g, "")
    .replace(/[:]+/g, "")
    .replace(/[’`]/g, "'")
    .replace(/[""<>]/g, "")
    .trim();
}

const CATEGORIA_PLATAFORMA = {
  ps3_game: "PS3",
  ps4_game: "PS4",
  ps5_native_game: "PS5",
  ps5_full_game: "PS5",
  ps5_ps4_game: "PS4",
  psp_game: "PSP",
  ps_vita_game: "PS Vita",
  pspc_game: "PC"
};

// Categorias do trophy2 que NÃO são jogos (Netflix, Prime Video, Apple TV, etc.)
const CATEGORIA_NAO_JOGO = /media_app|videoservice|web_based|nongame|not_found|music|podcast/;

function ehJogo(titulo) {
  return !CATEGORIA_NAO_JOGO.test(titulo.category || "");
}

function plataformaDoTitulo(titulo) {
  const mapeada = CATEGORIA_PLATAFORMA[titulo.category];
  if (mapeada) {
    return mapeada;
  }
  const titleId = titulo.titleId || "";
  if (titleId.startsWith("PPSA")) return "PS5";
  if (titleId.startsWith("CUSA")) return "PS4";
  if (titleId.startsWith("PCKA") || titleId.startsWith("PCSA")) return "PC";
  return "Desconhecida";
}

function formatarJogoSemTrofeus(titulo) {
  return {
    npCommunicationId: titulo.titleId,
    nome: titulo.name,
    imagemUrl: titulo.imageUrl,
    plataforma: [plataformaDoTitulo(titulo)],
    progresso: 0,
    trofeusConquistados: 0,
    trofeusTotais: 0,
    ultimaJogadaEm: titulo.lastPlayedDateTime,
    trofeus: { bronze: 0, prata: 0, ouro: 0, platina: 0 }
  };
}

/**
 * Full library: merges the trophy-service titles (v1, with progress + NPWR id)
 * with the played-games list (trophy2), so games that the trophy service does
 * not track are still saved. Matching is done by normalized name; a trophy2
 * title already covered by v1 is skipped to avoid duplicates.
 */
function mesclarBibliotecas(v1, v2) {
  const resultados = v1.map(formatarJogo);
  const nomesV1 = new Set(v1.map((t) => normalizarNome(t.trophyTitleName)));

  for (const titulo of v2) {
    if (!ehJogo(titulo)) {
      continue;
    }
    if (nomesV1.has(normalizarNome(titulo.name))) {
      continue;
    }
    resultados.push(formatarJogoSemTrofeus(titulo));
  }

  return resultados;
}

async function buscarTodosOsJogosComTrofeus(auth, accountId) {
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

async function buscarTodosOsJogosJogados(auth, accountId) {
  const LIMITE = 200;
  const jogos = [];
  let offset = 0;

  while (true) {
    const { titles, totalItemCount } = await getUserPlayedGames(
      auth,
      accountId,
      { limit: LIMITE, offset }
    );
    jogos.push(...titles);
    offset += titles.length;
    if (offset >= totalItemCount || titles.length === 0) break;
  }

  return jogos;
}

router.get("/api/jogos-usuario/:accountId", async (req, res) => {
  const { accountId } = req.params;
  console.log(`[jogos-usuario] GET /api/jogos-usuario/${accountId}`);

  try {
    const { result } = await withAuthorization(async (auth) => {
      const [comTrofeus, jogados] = await Promise.all([
        buscarTodosOsJogosComTrofeus(auth, accountId),
        buscarTodosOsJogosJogados(auth, accountId)
      ]);
      return mesclarBibliotecas(comTrofeus, jogados);
    });

    res.json(result);
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
