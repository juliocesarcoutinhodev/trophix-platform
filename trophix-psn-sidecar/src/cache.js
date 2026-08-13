function createCache(ttlMs) {
  const store = new Map();

  function get(key) {
    const entry = store.get(key);
    if (!entry) return undefined;
    if (Date.now() > entry.expiresAt) {
      store.delete(key);
      return undefined;
    }
    return entry.value;
  }

  function set(key, value) {
    store.set(key, { value, expiresAt: Date.now() + ttlMs });
  }

  function getStale(key) {
    const entry = store.get(key);
    return entry ? entry.value : undefined;
  }

  return { get, set, getStale };
}

module.exports = { createCache };
