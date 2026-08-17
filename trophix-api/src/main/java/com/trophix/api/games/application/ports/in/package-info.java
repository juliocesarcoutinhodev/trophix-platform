/**
 * Games inbound use cases — implemented by the module and also by dependent
 * modules (e.g. trophies implements GetGameDetailUseCase) to assemble read
 * models without creating a cycle.
 */
@org.springframework.modulith.NamedInterface("in")
package com.trophix.api.games.application.ports.in;
