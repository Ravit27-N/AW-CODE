export interface ChartOptions {
  chart: {
    width?: number | string;
    height?: number | string;
    type: 'donut';
    events: {
      click: boolean;
    };
  };
  colors: string[];
  dataLabels: {
    enabled: boolean;
    style: {
      fontSize: string,
      fontFamily: 'Rubik'
      fontWeight: string
    }
  };
  series: number[];
  tooltip: {
    enabled: boolean;
  };
  legend: {
    show: boolean;
    fontSize: string;
    markers: {
      shape: 'square' // set the shape of the legend markers to square
    },
    onItemHover: {
      highlightDataSeries: boolean;
    };
  };
  labels: string[];
  plotOptions: {
    pie: {
      donut: {
        size: string;
      };
      stroke: {
        show: boolean,
      };
      dataLabels: {
        offset: number;
      };
    };
    series: any;
  };
  responsive: any[],
  isEmpty?: boolean,
  isHidden?: boolean;
}
