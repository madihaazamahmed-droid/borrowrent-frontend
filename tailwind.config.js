/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        brand: { 50: "#fff4ed", 100: "#ffe6d5", 500: "#f97316", 600: "#ea580c", 700: "#c2410c" },
        ink: "#0f172a",
      },
      fontFamily: { sans: ["Inter", "system-ui", "sans-serif"] },
      boxShadow: { card: "0 8px 30px rgba(15,23,42,.08)" },
    },
  },
  plugins: [],
};
