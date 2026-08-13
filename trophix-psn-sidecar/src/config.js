const dotenv = require("dotenv");

dotenv.config();

const config = {
  npssoToken: process.env.NPSSO_TOKEN,
  port: Number(process.env.PORT) || 3000,
  trophyCacheTtlMs: Number(process.env.TROFEUS_CACHE_TTL_MS) || 600000
};

module.exports = config;