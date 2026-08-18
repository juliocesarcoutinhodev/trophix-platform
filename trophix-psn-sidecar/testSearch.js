const { makeUniversalSearch } = require('psn-api');
const { withAuthorization } = require('./src/psnClient');
const config = require('./src/config');

async function testSearch() {
  try {
    const { result } = await withAuthorization(async (auth) => {
      // Trying "ConceptGame", "Game", "GameVideo"
      return await makeUniversalSearch(auth, 'Elden Ring', 'ConceptGame');
    });
    console.log(JSON.stringify(result, null, 2));
  } catch (e) {
    console.error(e.response?.data || e);
  }
}

testSearch();
