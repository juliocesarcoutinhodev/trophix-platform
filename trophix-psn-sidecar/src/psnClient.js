const {
  exchangeNpssoForAccessCode,
  exchangeAccessCodeForAuthTokens,
  exchangeRefreshTokenForAuthTokens
} = require("psn-api");
const config = require("./config");

let authorization = null;
let accessTokenExpiresAt = null;
let refreshTokenExpiresAt = null;
let refreshInProgress = null;

const now = () => Date.now();
const toIso = (ms) => new Date(ms).toISOString();

function scheduleExpiration() {
  accessTokenExpiresAt = now() + authorization.expiresIn * 1000;
  refreshTokenExpiresAt = now() + authorization.refreshTokenExpiresIn * 1000;
}

async function authenticateFromNpsso() {
  console.log("[psn] Autenticando na PSN com NPSSO...");
  const accessCode = await exchangeNpssoForAccessCode(config.npssoToken);
  authorization = await exchangeAccessCodeForAuthTokens(accessCode);
  scheduleExpiration();
  console.log("[psn] Autenticacao PSN bem-sucedida.");
}

async function refreshAuthorization() {
  if (refreshInProgress) return refreshInProgress;
  refreshInProgress = (async () => {
    console.log("[psn] Token expirado. Renovando via refresh token...");
    authorization = await exchangeRefreshTokenForAuthTokens(authorization.refreshToken);
    scheduleExpiration();
    console.log("[psn] Refresh bem-sucedido. Novo access token obtido.");
  })().finally(() => {
    refreshInProgress = null;
  });
  return refreshInProgress;
}

async function ensureAuthorization() {
  if (!authorization) {
    await authenticateFromNpsso();
    return;
  }
  if (now() < accessTokenExpiresAt) return;

  if (now() >= refreshTokenExpiresAt) {
    throw new Error(
      "Refresh token expirado. Renove o NPSSO em https://ca.account.sony.com/api/v1/ssocookie e atualize o .env."
    );
  }
  await refreshAuthorization();
}

function isUnauthorizedError(error) {
  const msg = String(error.message);
  return /unauthorized|invalid access token/i.test(msg);
}

function isNotFoundError(error) {
  const msg = String(error.message);
  return /not found|does not exist/i.test(msg);
}

async function withAuthorization(fn) {
  try {
    await ensureAuthorization();
    return { result: await fn(authorization) };
  } catch (error) {
    if (isUnauthorizedError(error)) {
      console.log("[psn] Chamada falhou com token invalido. Tentando renovar...");
      await refreshAuthorization();
      const result = await fn(authorization);
      return { result };
    }
    throw error;
  }
}

function reportTokenHealth() {
  if (!authorization) return;
  const nowMs = now();
  const accessDays = (accessTokenExpiresAt - nowMs) / 86400000;
  const refreshDays = (refreshTokenExpiresAt - nowMs) / 86400000;
  console.log(
    `[psn] Access token valido por ${accessDays.toFixed(2)}h | Refresh token valido por ${refreshDays.toFixed(2)} dias.`
  );
  if (refreshDays < 7) {
    console.warn(
      "[psn] ATENCAO: refresh token expira em menos de 7 dias. Obtenha um novo NPSSO em https://ca.account.sony.com/api/v1/ssocookie."
    );
  }
}

module.exports = {
  authenticateFromNpsso,
  ensureAuthorization,
  withAuthorization,
  reportTokenHealth,
  isNotFoundError
};