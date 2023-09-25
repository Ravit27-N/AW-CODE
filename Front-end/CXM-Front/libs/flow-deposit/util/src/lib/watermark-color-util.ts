import {ColorPicker} from "@cxm-smartflow/flow-deposit/data-access";

export class WatermarkColorUtil {

  public static readonly COLOR: Array<ColorPicker> = [
    {hex: '#ffffff', value: 'WHITE'},
    {hex: '#c0c0c0', value: 'LIGHT_GRAY'},
    {hex: '#808080', value: 'GRAY'},
    {hex: '#404040', value: 'DARK_GRAY'},
    {hex: '#000000', value: 'BLACK'},
    {hex: '#ff0000', value: 'RED'},
    {hex: '#ffafaf', value: 'PINK'},
    {hex: '#ffc800', value: 'ORANGE'},
    {hex: '#ffff00', value: 'YELLOW'},
    {hex: '#00ff00', value: 'GREEN'},
    {hex: '#ff00ff', value: 'MAGENTA'},
    {hex: '#00ffff', value: 'CYAN'},
    {hex: '#0000ff', value: 'BLUE'},
  ];

  public static colorPickerToText(hex: string): string {
    return WatermarkColorUtil.COLOR.find(e => e.hex === hex)?.value || hex;
  }

  public static textToColorPicker(value: string): string {
    return WatermarkColorUtil.COLOR.find(e => e.value === value)?.hex || value;
  }

}
