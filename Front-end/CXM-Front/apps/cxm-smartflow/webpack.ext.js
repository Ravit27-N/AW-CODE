const fs = require('fs');
const path = require('path');

const generateHash = function (number) {
  let hash = 0,
    i,
    chr;
  if (number.length === 0) return hash;
  for (i = 0; i < number.length; i++) {
    chr = number.charCodeAt(i);
    hash = (hash << 5) - hash + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return Math.abs(hash);
};

const applyBuildVersion = function (config) {
  const builderVersion = generateHash('' + Date.now());

  return Object.keys(config)
    .map((k) => {
      let withVersion = config[k] + '?v=' + builderVersion;
      return { [k]: withVersion };
    })
    .reduce((p, c) => Object.assign(p, c), {});
};

const createBuildVersion = function () {
  const hash = generateHash('' + Date.now());
  const version = { buildVersion: hash };
  console.log({ version });

  fs.writeFileSync(
    path.resolve('apps/cxm-smartflow/src/build-version.json'),
    JSON.stringify(version, null, 2)
  );
};

module.exports = {
  applyBuildVersion,
  createBuildVersion,
};
