import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  define: {
    // global이 정의되지 않았을 때 window로 대체
    global: "window",
  },
});
