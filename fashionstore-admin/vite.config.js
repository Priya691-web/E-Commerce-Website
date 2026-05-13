import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';

// Vite dev server runs on http://localhost:5173
// Backend (Tomcat) runs on configurable backend URL
// All /api requests are transparently proxied so the JSESSIONID cookie is shared.
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const backendTarget = env.VITE_BACKEND_TARGET || 'http://localhost:8080';
  
  return {
    base: '/admin/',
    plugins: [react()],
    server: {
      port: 5173,
      strictPort: false,
      host: true,
      proxy: {
        '/api': {
          target: backendTarget,
          changeOrigin: true,
          secure: false,
          configure: (proxy, options) => {
            proxy.on('proxyReq', (proxyReq, req, res) => {
              // Log proxy requests for debugging
              console.log(`[Proxy] ${req.method} ${req.url} -> ${options.target}${req.url}`);
            });
          },
        },
      },
    },
    build: {
      outDir: 'dist',
      sourcemap: true,
      target: 'es2020',
      rollupOptions: {
        output: {
          manualChunks: {
            react: ['react', 'react-dom', 'react-router-dom'],
            charts: ['recharts'],
          },
        },
      },
    },
  };
});
