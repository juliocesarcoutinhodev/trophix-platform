const express = require("express");
const { getProfileFromUserName } = require("psn-api");
const { withAuthorization, isNotFoundError } = require("../psnClient");

const router = express.Router();

router.get("/api/perfil/:psnId", async (req, res) => {
  const { psnId } = req.params;
  console.log(`[perfil] GET /api/perfil/${psnId}`);

  try {
    const { result } = await withAuthorization((auth) =>
      getProfileFromUserName(auth, psnId)
    );

    const profile = result.profile;
    const avatarUrl =
      profile.avatarUrls?.find((a) => a.size === "xl")?.avatarUrl ??
      profile.avatarUrls?.slice(-1)[0]?.avatarUrl ??
      null;

    res.json({
      psnId: profile.onlineId,
      aboutMe: profile.aboutMe,
      avatarUrl
    });
  } catch (error) {
    if (isNotFoundError(error)) {
      console.log(`[perfil] Perfil nao encontrado ou privado: ${psnId}`);
      return res.status(404).json({ error: "Perfil nao encontrado ou privado" });
    }
    console.error(`[perfil] Erro ao buscar perfil ${psnId}:`, error.message);
    res.status(502).json({ error: "Falha ao consultar a PSN", detalhe: error.message });
  }
});

module.exports = router;