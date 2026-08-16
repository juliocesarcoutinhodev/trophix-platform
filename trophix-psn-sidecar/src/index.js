const express = require("express");
const config = require("./config");
const { authenticateFromNpsso, reportTokenHealth } = require("./psnClient");
const perfilRouter = require("./routes/perfil");
const trofeusRouter = require("./routes/trofeus");
const resumoRouter = require("./routes/resumo");
const jogosUsuarioRouter = require("./routes/jogosUsuario");

async function bootstrap() {
  if (!config.npssoToken) {
    console.error(
      "ERRO: A variavel de ambiente NPSSO_TOKEN nao foi injetada no container. " +
        "Defina NPSSO_TOKEN no arquivo .env da raiz do repositorio e execute: docker compose up -d"
    );
    process.exit(1);
  }

  await authenticateFromNpsso();
  reportTokenHealth();

  const app = express();
  app.use(express.json());

  app.use((req, res, next) => {
    console.log(`[http] ${new Date().toISOString()} ${req.method} ${req.originalUrl}`);
    next();
  });

  app.use(perfilRouter);
  app.use(trofeusRouter);
  app.use(resumoRouter);
  app.use(jogosUsuarioRouter);

  app.get("/health", (req, res) => {
    res.json({ status: "UP" });
  });

  app.use((req, res) => {
    res.status(404).json({ error: "Rota nao encontrada" });
  });

  app.use((err, req, res, next) => {
    console.error("[http] Erro interno:", err.message);
    res.status(500).json({ error: "Erro interno do sidecar" });
  });

  app.listen(config.port, () => {
    console.log(`[http] Sidecar PSn rodando na porta ${config.port}`);
  });
}

bootstrap().catch((error) => {
  console.error("Falha ao inicializar o sidecar:", error.message);
  process.exit(1);
});