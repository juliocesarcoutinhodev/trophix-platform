# Automação de Ofertas (Roadmap Futuro)

Este documento registra ideias e estratégias para automatizar a curadoria e publicação de ofertas no Trophix e em grupos de Telegram/WhatsApp.

## Visão Geral
Em vez de depender do cadastro manual de ofertas no painel administrativo, o objetivo futuro é criar "Workers" ou "Cron Jobs" no backend (`Spring Boot`) que farão a busca automática, inserção no banco de dados e disparo em redes sociais.

## Estratégias de Automação

### 1. Integração com APIs Oficiais de Afiliados (Recomendado)
A forma mais profissional de obter ofertas diárias com links já "trackeados".
*   **Amazon PA-API (Product Advertising API):** Permite buscar categorias específicas (Ex: Consoles, Jogos de PS5) ordenando por maior porcentagem de desconto no dia. Retorna JSON com fotos, preço original e preço com desconto.
*   **Redes de Afiliados (Awin, Lomadee, Admitad):** Agregam lojas como KaBuM!, Nuuvem, Casas Bahia, etc. Possuem APIs de "Cupons e Ofertas" que trazem descontos pré-aprovados.

### 2. Automação de Disparo (Telegram Bot)
Uma vez que a oferta for inserida no Trophix, o sistema pode avisar a comunidade instantaneamente:
*   **Spring Boot + Telegram API:** O backend envia um `POST` para a API do Telegram (`https://api.telegram.org/bot<TOKEN>/sendMessage`) no canal oficial do Trophix.
*   **Formato da Mensagem:** Pode incluir título, emojis (🚨🎮), preço antigo cortado, novo preço, link de afiliado encurtado e a foto do produto.

### 3. Automação via Espelhamento de Canais
Uma técnica mais cinzenta, mas muito utilizada: um robô em Python lê postagens de grandes canais abertos de ofertas (via bibliotecas como `Telethon`), extrai a oferta, substitui o link original pelo link de afiliado do Trophix, e posta no grupo próprio ou via API no banco de dados do Trophix.

## Passos para Implementação no Spring Boot
1.  **Criar Módulo Modulith:** `com.trophix.api.bot` ou `com.trophix.api.integrations`.
2.  **Spring @Scheduled:** Criar rotinas (`@Scheduled(cron = "0 0 */2 * * *")`) para buscar ofertas a cada 2 horas.
3.  **Filtro e Validação:** Garantir que o robô cadastre apenas ofertas cujo `discountPercentage > 20%` e filtre palavras-chave como `PlayStation`, `PS5`, `DualSense`.
4.  **Integração Bot:** Usar webhooks ou a biblioteca oficial `telegrambots-spring-boot-starter`.

---
*Este é um documento de referência vivo. Ao iniciar o desenvolvimento desta fase, utilize-o como base para a criação dos Requisitos Técnicos.*
