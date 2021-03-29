module.exports = {
  plugins: [
    require('tailwindcss'),
    require('autoprefixer'),
    require('postcss-copy')({
      dest: 'public/build'
    })
  ]
}