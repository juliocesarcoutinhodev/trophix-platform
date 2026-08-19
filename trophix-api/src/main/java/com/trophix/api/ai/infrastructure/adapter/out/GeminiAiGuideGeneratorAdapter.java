package com.trophix.api.ai.infrastructure.adapter.out;

import com.trophix.api.ai.application.ports.out.AiGuideGeneratorPort;
import com.trophix.api.ai.domain.GuideAiPrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * {@link AiGuideGeneratorPort} backed by Google Gemini via Spring AI. Uses the
 * auto-configured {@link ChatClient} (spring-ai-starter-model-google-genai).
 * Google Search Grounding is enabled at the application level
 * ({@code spring.ai.google.genai.chat.google-search-retrieval=true}), so the
 * model grounds its answers on real web search results instead of guessing.
 */
@Slf4j
@Component
public class GeminiAiGuideGeneratorAdapter implements AiGuideGeneratorPort {

    private static final String ROADMAP_SYSTEM_PROMPT = """
            Você é um especialista em guias de troféus da PSN. Sua tarefa é criar um
            guia de platina completo e direto ao ponto para um jogo.

            Regras obrigatórias:
            - Responda APENAS em Markdown limpo e renderizável, SEM blocos de código
              markdown (não use ```markdown ou ```). Use títulos, listas e ênfase.
            - Use o Google Search (Search Grounding) para pesquisar em sites focados
              em troféus, como PSNProfiles, PowerPyx, PlayStationTrophies e similares.
              Baseie as informações em fontes reais; nunca invente troféus ou
              requisitos.
            - O conteúdo deve ser prático e focado em como conquistar cada troféu da
              forma mais direta possível (estratégia, ordem recomendada, dicas por
              dificuldade, colecionáveis se aplicável).
            - Estruture com: visão geral da dificuldade/tempo, lista de troféus
              organizada por seção, e dicas passo a passo para os mais trabalhosos.
            - Escreva em português do Brasil, em tom claro e objetivo.
            """;

    private static final String ROADMAP_USER_PROMPT = """
            Jogo: %s
            Plataforma: %s

            Gere o guia de platina completo deste jogo seguindo as regras do sistema.
            """;

    private static final String TROPHY_TIP_SYSTEM_PROMPT = """
            Você é um especialista em troféus da PSN. Sua tarefa é criar uma dica
            curta, direta e prática sobre como conquistar UM troféu específico.

            Regras obrigatórias:
            - Responda APENAS em Markdown limpo e renderizável, SEM blocos de código
              markdown (não use ```markdown ou ```). Pode usar listas e negrito.
            - Use o Google Search (Search Grounding) para pesquisar em sites focados
              em troféus, como PSNProfiles, PowerPyx, PlayStationTrophies e similares.
              Baseie-se em fontes reais; nunca invente requisitos ou métodos.
            - Foque em como conquistar o troféu da forma mais direta possível:
              método, passo a passo, dificuldade, erros comuns e truques.
            - Escreva em português do Brasil, em tom claro e objetivo, com no máximo
              3 a 6 parágrafos.
            """;

    private static final String TROPHY_TIP_USER_PROMPT = """
            Jogo: %s
            Plataforma: %s
            Troféu: %s
            Descrição do troféu: %s

            Gere a dica deste troféu seguindo as regras do sistema.
            """;

    private final ChatClient chatClient;

    public GeminiAiGuideGeneratorAdapter(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public String generateRoadmapContent(GuideAiPrompt prompt) {
        log.info("Gerando roadmap via Gemini para game={}", prompt.gameName());
        return chatClient.prompt()
                .system(ROADMAP_SYSTEM_PROMPT)
                .user(ROADMAP_USER_PROMPT.formatted(
                        safe(prompt.gameName()),
                        safe(prompt.platform())))
                .call()
                .content();
    }

    @Override
    public String generateTrophyTipContent(GuideAiPrompt prompt) {
        log.info("Gerando dica via Gemini para game={} trophy={}", prompt.gameName(), prompt.trophyName());
        return chatClient.prompt()
                .system(TROPHY_TIP_SYSTEM_PROMPT)
                .user(TROPHY_TIP_USER_PROMPT.formatted(
                        safe(prompt.gameName()),
                        safe(prompt.platform()),
                        safe(prompt.trophyName()),
                        safe(prompt.trophyDescription())))
                .call()
                .content();
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
