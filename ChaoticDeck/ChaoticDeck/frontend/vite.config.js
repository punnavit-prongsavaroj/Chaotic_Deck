import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/Player': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/Room': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/Game': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
});
