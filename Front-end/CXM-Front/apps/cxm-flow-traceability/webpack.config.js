const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");
const mf = require("@angular-architects/module-federation/webpack");
const path = require("path");

const sharedMappings = new mf.SharedMappings();
sharedMappings.register(
  path.join(__dirname, '../../tsconfig.base.json'),
  [/* mapped paths to share */]);

module.exports = {
  output: {
    uniqueName: "cxmFlowTraceability",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  resolve: {
    alias: {
      ...sharedMappings.getAliases(),
    }
  },
  plugins: [
    new ModuleFederationPlugin({

        // For remotes (please adjust)
        name: "cxmFlowTraceability",
        filename: "remoteEntry.js",
        exposes: {
            './Module': 'apps/cxm-flow-traceability/src/app/cxm-flow-traceability/cxm-flow-traceability.module.ts',
        },
        shared: {
          "@angular/core": { singleton: true, requiredVersion: ">=12.0.0"},
          "@angular/common": { singleton: true, requiredVersion: ">=12.0.0"},
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
