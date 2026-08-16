package com.trophix.api.admin.application.usecases;

import com.trophix.api.admin.application.ports.in.GetSidecarStatusUseCase;
import com.trophix.api.admin.application.ports.out.SidecarHealthPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSidecarStatusUseCaseImpl implements GetSidecarStatusUseCase {

    private final SidecarHealthPort sidecarHealthPort;

    @Override
    public SidecarStatus getStatus() {
        return new SidecarStatus(sidecarHealthPort.isUp());
    }
}
