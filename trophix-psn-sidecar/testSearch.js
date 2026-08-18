const { makeUniversalSearch } = require('psn-api');
const { withAuthorization } = require('./src/psnClient');
const config = require('./src/config');

async function testSearch() {
  try {
    const { result } = await withAuthorization(async (auth) => {
      // `makeUniversalSearch` takes auth, search term, and domain (e.g. "Game")
      return makeUniversalSearch(auth, 'the witcher', 'Game');
    });
    console.log(JSON.stringify(result, null, 2));
  } catch (error) {
    console.error('Error:', error);
  }
}

testSearch();
