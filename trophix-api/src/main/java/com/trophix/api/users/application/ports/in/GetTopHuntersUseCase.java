package com.trophix.api.users.application.ports.in;

import com.trophix.api.users.model.User;

import java.util.List;

public interface GetTopHuntersUseCase {

    /**
     * Returns the top hunters (users with a synced PSN profile), ordered by
     * platinum count descending, up to the given limit.
     */
    List<User> getTopHunters(int limit);
}
