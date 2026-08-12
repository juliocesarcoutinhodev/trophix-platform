const dotenv = require("dotenv");

dotenv.config();

const config = {
  npssoToken: process.env.NPSSO_TOKEN,
  port: Number(process.env.PORT) || 3000
};

module.exports = config;