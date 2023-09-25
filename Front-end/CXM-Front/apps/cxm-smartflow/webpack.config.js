const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");
const mf = require("@angular-architects/module-federation/webpack");
const path = require("path");
const { applyBuildVersion, createBuildVersion } = require("./webpack.ext");

// Configure smartflow
// let remotes = {
//   "cxmCampaign": "cxmCampaign@http://localhost:3000/remoteEntry.js",
//   "cxmTemplate": "cxmTemplate@http://localhost:4000/remoteEntry.js",
//   "cxmFlowTraceability": "cxmFlowTraceability@http://localhost:9000/remoteEntry.js",
//   "cxmDeposit": "cxmDeposit@http://localhost:5001/remoteEntry.js",
//   "cxmProfile": "cxmProfile@http://localhost:5002/remoteEntry.js",
//   "cxmDirectory": "cxmDirectory@http://localhost:5003/remoteEntry.js"
// }

// // Add hash build version
// remotes = applyBuildVersion(remotes);

createBuildVersion();

const sharedMappings = new mf.SharedMappings();
sharedMappings.register(
  path.join(__dirname, '../../tsconfig.base.json'),
  [/* mapped paths to share */]);

module.exports = {
  output: {
    uniqueName: "cxmSmartflow",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  // devServer: { host: '0.0.0.0',port: 5000 },
  resolve: {
    alias: {
      ...sharedMappings.getAliases(),
    }
  },
  plugins: [
    new ModuleFederationPlugin({
        // remotes,

        shared: {
          "@angular/core": { singleton: true, requiredVersion: ">=12.0.0"},
          "@angular/common": { singleton: true , requiredVersion: ">=12.0.0"},
          "@angular/common/http": { singleton: true, requiredVersion: ">=12.0.0"},
          "@angular/router": { singleton: true, requiredVersion: ">=12.0.0"},

          ...sharedMappings.getDescriptors()
        }

    }),
    sharedMappings.getPlugin()
  ],
  module: {
    rules: [
      {
        test: /\.m?js$/,
        exclude: /[\\/]node_modules[\\/](?!(incompatible-module1|incompatible_module_2|some_other_nested_module)[\\/])/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: [
              ['@babel/preset-env', { targets: "es5", browsers: ['> 0.5%', ' last 2 versions', ' Firefox ESR', ' not dead', ' IE 11'] }]
            ],
            plugins: ['@babel/plugin-proposal-class-properties']
          }
        }
      }
    ]
  }

};
