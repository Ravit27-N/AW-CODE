export class colorRandom{

  private static color:string[] = ['rgb(0, 102, 204)','rgb(255, 111, 0)','rgb(128,128,128)','rgb(95, 144, 70)','rgb(255,192,0)','rgb(92, 152, 108)','rgb(30, 85, 222)','rgb(220, 40, 48)'];

  constructor() {}

  static getColor(index:number): string{
    return this.color[index];
  }

}
