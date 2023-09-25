

export const COLOR_SCHEMES = [
  '#002060',
  '#964EA1',
  '#FF4E83',
  '#0060A5',
  '#d33f9a',
  '#a6c52f',
  '#93f6ff',
  '#cf5754',
  '#ef57a1',
  '#335166',
  '#332cad',
  '#b78d47',
  '#9c471f',
  '#4b657f',
  '#2e0443',
  '#a267b1',
  '#290d1f'
];

export const pickColor = (size: number) => {
  return COLOR_SCHEMES.slice(0, size);
}


export const numberFormatter = new Intl.NumberFormat(localStorage.getItem('locale') || 'fr' , {
  minimumFractionDigits: 2,
  maximumFractionDigits: 2
});
