const express = require("express");
const { getProfileFromUserName, getUserTrophyProfileSummary } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");

const router = express.Router();

router.get("/api/resumo/:psnId", async (req, res) => {
  const { psnId } = req.params;
  console.log(`[resumo] GET /api/resumo/${psnId}`);

  try {
    const { result } = await withAuthorization(async (auth) => {
      const { profile } = await getProfileFromUserName(auth, psnId);
      return getUserTrophyProfileSummary(auth, profile.accountId);
    });

    res.json({
      accountId: result.accountId,
      nivel: Number(result.trophyLevel),
      progresso: result.progress,
      trofeus: {
        bronze: result.earnedTrophies.bronze,
        prata: result.earnedTrophies.silver,
        ouro: result.earnedTrophies.gold,
        platina: result.earnedTrophies.platinum
      }
    });
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[resumo] Perfil nao encontrado ou privado: ${psnId}`);
      return res.status(404).json({ error: "Perfil nao encontrado ou privado" });
    }
    console.error(`[resumo] Erro ao buscar resumo de ${psnId}:`, error.message);
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

module.exports = router;