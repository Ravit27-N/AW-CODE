import react from '@vitejs/plugin-react';
import path from 'path';
import {defineConfig, loadEnv} from 'vite';

// https://vitejs.dev/config/
export default ({mode}) => {
  process.env = {...process.env, ...loadEnv(mode, process.cwd())};
  return defineConfig({
    server: {
      host: process.env.VITE_APP_HOST,
      port: Number(process.env.VITE_PORT),
    },
    resolve: {
      alias: {
        '@': path.resolve(__dirname, './src'),
        '@assets': path.resolve(__dirname, './src/assets'),
        '@components': path.resolve(__dirname, './src/components'),
        '@pages': path.resolve(__dirname, './src/pages'),
        '@i18n': path.resolve(__dirname, './src/i18n'),
        '@constant': path.resolve(__dirname, './src/constant'),
      },
    },
    plugins: [react()],
  });
};
