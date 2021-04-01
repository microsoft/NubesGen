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
      keyframes: {
        'loader': {
          '0%': { transform: 'translateX(0px)' },
          '100%': { transform: 'translateX(280px)' }
        },
      },
      animation: {
        'left-right': 'loader .7s ease-in-out infinite alternate'
      }
    },
  },
  variants: {
    extend: {},
  },
  plugins: [require('@tailwindcss/forms')],
};
