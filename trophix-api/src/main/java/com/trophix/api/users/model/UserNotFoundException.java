package com.trophix.api.users.model;

import com.trophix.api.shared.exception.ResourceNotFoundException;

public final class UserNotFoundException extends ResourceNotFoundException {

    public UserNotFoundException() {
        super("Usuario nao encontrado");
    }
}