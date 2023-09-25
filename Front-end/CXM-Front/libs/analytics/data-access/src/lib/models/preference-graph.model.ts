import { Color } from '@swimlane/ngx-charts/lib/utils/color-sets';

export interface PreferenceGraphModel {
  key: 'Postal' | 'Email' | 'SMS';
  value: number;
  volume?: number;
}

export interface VolumeReceiveGraph {
  name: string;
  color: string;
  view: [number, number];
  scheme: Color;
  results: { name: string, value: number, volume?: number }[];
  graphLabels: string [];
  legend: boolean;
  labels: boolean;
  doughnut: boolean;
  tooltipText: string;
  fetching: boolean;
  empty: boolean;
}

export interface PreferenceDistributionGraphModel {
  key: string;
  value: number;
  volume?:number;
}

export interface PreferenceDistributionPNDGraphModel {
  key: string;
  value: number;
}
export interface PreferenceProcessedMailModel {
  key: string;
  value: number;
}



export interface ProcessedMailGraph {
  series: {
    name: string;
    data: number[];
  }[];
  colors: string[];
  chart: {
    height?: number,
    width?: number,
    type: string;
    stacked: boolean;
    stackType: string;
    toolbar: {
      show: boolean;
    };
  };
  plotOptions: {
    bar: {
      columnWidth: string;
    };
  };
  xaxis: {
    categories: string[];
  };
  yaxis: {
    labels: {
      formatter(value: string) : string
    };
  };
  legend: {
    position: string;
    offsetX: number;
    offsetY: number;
    inverseOrder: boolean;
    onItemClick?: {
      toggleDataSeries: boolean;
    },
  };
  responsive: {
    breakpoint: number;
    options: {
      chart: {
        width: string;
        height: string;
      };
    };
  }[];
  tooltip?: any;
  dataLabels: {
    style: {
      fontSize: string;
      fontWeight:string;
      fontFamily:string;
      color: string;
    };
    enabled: boolean;
    formatter(value: string, option:any) : string
  };
}
export interface DistributionByStatus {
  key: string;
  value: number;
};
