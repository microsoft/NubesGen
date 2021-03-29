module.exports = {
  purge: [],
  darkMode: false, // or 'media' or 'class'
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#F0EEFF',
          100: '#EDE9FE',
          200: '#DDD6FE',
          800: '#5848D0',
          900: '#493675',
        },
      },
      fontFamily: {
        sans: ['Segoe UI', 'sans'],
      },
      zIndex: {
        '-5': '-5',
      },
    },
  },
  variants: {
    extend: {},
  },
  plugins: [
    require('@tailwindcss/forms')
  ],
  
};
