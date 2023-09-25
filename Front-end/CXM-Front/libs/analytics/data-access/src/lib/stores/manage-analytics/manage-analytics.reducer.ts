import {
  ChartOptions, DistributionByStatus,
  ManageAnalyticsStateModel,
  ProcessedMailGraph,
  ProductionProgressModel,
  VolumeReceiveGraph
} from '../../models';
import {createReducer, on} from '@ngrx/store';
import * as fromActions$ from './manage-analytics.action';
import {ScaleType} from '@swimlane/ngx-charts';
import {AnalyticReportUtils, DistributionPNGGraphUtils} from '@cxm-smartflow/analytics/util';
import {exportCsvFile, fetchProcessedMailGraphFail} from "./manage-analytics.action";
import {DistributionByStatusUtil} from "../../../../../util/src/lib/distribution-by-status";
import {appRoute, StatisticReport} from "@cxm-smartflow/shared/data-access/model";
import {UserProfileUtil} from "@cxm-smartflow/shared/data-access/services";

export const manageAnalyticsReducerKey = `manage-analytics-resource-key`;

const initialStates: ManageAnalyticsStateModel = {
  filterCriteria: {
    channel: [],
    depositMode: [],
    flowStatus: [],
    subChannel: [],
  },
  fillers: [],
  filterCategoryDisabled: [],
  filterChannelDisabled: [],
  distributionCriteria: {
    customer: '',
    preferences: []
  },
  analyticsReportTabs: [],
  volumeReceive: [],
  globalProductionDetails: { metaData: [], result: [], loading: false },
  productionDetails: { metaData: [], data: [], total: {}, loading: false },
  distributionVolumeReceived: [],
  distributionVolumeReceivedGraph: {
    results: [],
    graphLabels: [],
    scheme: {
      domain: [],
      name: 'Volume received',
      group: ScaleType.Time,
      selectable: true,
    },
    color: 'rgb(255, 78, 131)',
    doughnut: false,
    name: 'Volume received',
    labels: true,
    legend: false,
    view: [410, 410],
    tooltipText: ``,
    empty: true,
    fetching: false,
  },
  refreshFetchGraphDate: new Date(Date.now()),
  volumeReceivedGraph: {
    results: [],
    graphLabels: [],
    scheme: {
      domain: [],
      name: 'Volume received',
      group: ScaleType.Time,
      selectable: true,
    },
    color: 'rgb(255, 78, 131)',
    doughnut: false,
    name: 'Volume received',
    labels: true,
    legend: false,
    view: [410, 410],
    tooltipText: ``,
    empty: true,
    fetching: false,
  },
  filterOption: {
    channels: [],
    categories: [],
    standAloneCategory: [],
    calendar: {
      startDate: new Date(Date.now() - 6 * 24 * 60 * 60 * 1000),
      endDate: new Date(),
      option: 3,
    },
    fillers: [],
    fillerSearchTerm: '',
    fillersGroup: [],
  },
  productionProgresses: {
    content: [],
    isFetching: false,
  },
  requestedAt: '',
  postalFilteringCriteria: {
    channels:[],
    categories: [],
    startDate: '',
    endDate: '',
    fillers: '',
    searchByFiller: '',
    secondFillerKey: '',
    secondFillerText: '',
    thirdFillerKey: '',
    thirdFillerText: '',
    requestedAt: '',
  },
  distributionPND: [],
  distributionPNDGraph: {
    chart: {
      type: 'donut',
      height: '400px',
      width: '400px',
      sparkline: {
        enabled: false,
      },
    },
    colors: [],
    dataLabels: {
      enabled: true,
      style: {
        fontWeight: '400',
        fontSize: '7px',
        color: '#000',
        fontFamily: 'Rubik',
      },
      formatter: function (val: any) {
        const numberFormatter = new Intl.NumberFormat(
          localStorage.getItem('locale') || 'fr',
          {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          }
        );
        return `${numberFormatter.format(val)}%`;
      },
    },
    series: [],
    tooltip: {
      enabled: false, // disable tooltip
    },
    legend: {
      show: false,
    },
    labels: [],
    plotOptions: {
      pie: {
        donut: {
          size: '40%', // Set the overall size of the donut chart
        },
        stroke: {
          show: true,
        },
        dataLabels: {
          offset: 0, // Set the arc width by modifying the offset value
        },
        expandOnClick: false,
      },
      series: {
        size: '100%',
        responsive: {
          enabled: false // disable dynamic sizing based on the number of items
        }
      }
    },
    stroke:{
      width: 0,
    },
    responsive: [
      {
        breakpoint: 1900,
        options: {
          chart: {
            width: 150,
            height: 150,
          },
          dataLabels: {
            enabled: true,
            style: {
              fontSize: '8px',
            },
          }
        },
      },
      {
        breakpoint: 2064,
        options: {
          chart: {
            width: 200,
            height: 200,
          },
        },
      },
      {
        breakpoint: 2298,
        options: {
          chart: {
            width: 230,
            height: 230,
          },
        },
      },
      {
        breakpoint: 4000,
        options: {
          chart: {
            width: 250,
            height: 250,
          },
          dataLabels: {
            enabled: true,
            style: {
              fontSize: '15px',
            },
          },
        },
      },
    ],
    isHidden: false
  },

  distributionPNDLoading: false,
  processedMail:[],
  processedMailGraph: {
    series:[],
    colors: ["rgb(255, 111, 0)", "rgb(0, 102, 204)"],
    chart: {
      type: "bar",
      stacked: true,
      stackType: "100%",
      toolbar: {
        show: false
      }
    },

    plotOptions: {
      bar: {
        columnWidth: '35%' // Set the desired width for each column
      }
    },
    xaxis: {
      categories: [
        "",
      ]
    },
    yaxis: {
      labels:{
        formatter: function (value: string) {
          return value + '%';
        }
      }
    },
    legend: {
      position: "right",
      offsetX: 0,
      offsetY:100,
      inverseOrder:true,
      onItemClick: {
        toggleDataSeries: false
      },
    },
    responsive: [
      {
        breakpoint: 1400,
        options: {
          chart: {
            width: '100%',
            height: '200px',
          },
          legend: {
            offsetY: 55,
          },
        },
      },
      {
        breakpoint: 2067,
        options: {
          chart: {
            width: '100%',
            height: '220px',
          },
          legend: {
            offsetY: 65,
          },
        },
      },
      {
        breakpoint: 2200,
        options: {
          chart: {
            width: '100%',
            height: '250px',
          },
        },
      },
      {
        breakpoint: 3000,
        options: {
          chart: {
            width: '100%',
            height: '300px',
          },
        },
      },
    ],
    tooltip:{
      enabled:true
    },
    dataLabels: {
      enabled: true,
      style: {
        fontWeight: '400',
        fontSize: '15px',
        fontFamily: 'Rubik',
        color: '#000',
      },
      formatter: function (val:string, opt:any) {
        return opt.w.config.series[opt.seriesIndex].data[0]
      }
    }
  },
  processedMailLoading:true,
  distributionByStatus:[],
  distributionByStatusGraph: {
    chart: {
      type: 'donut',
      height: '400px',
      width: '400px',
      sparkline: {
        enabled: false,
      },
    },
    colors: [],
    dataLabels: {
      enabled: true,
      style: {
        fontWeight: '400',
        fontSize: '7px',
        color: '#000',
        fontFamily: 'Rubik',
      },
      formatter: function (val: any) {
        const numberFormatter = new Intl.NumberFormat(
          localStorage.getItem('locale') || 'fr',
          {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          }
        );
        return `${numberFormatter.format(val)}%`;
      },
    },
    series: [],
    tooltip: {
      enabled: false, // disable tooltip
    },
    legend: {
      show: false,
    },
    labels: [],
    plotOptions: {
      pie: {
        donut: {
          size: '0%', // 40%
        },
        stroke: {
          show: true,
        },
        dataLabels: {
          offset: 0, // Set the arc width by modifying the offset value
        },
        expandOnClick: false,
      },
      series: {
        size: '100%',
        responsive: {
          enabled: false // disable dynamic sizing based on the number of items
        }
      }
    },
    stroke:{
      width: 0,
    },
    responsive: [
      {
        breakpoint: 1900,
        options: {
          chart: {
            width: 150,
            height: 150,
          },
          dataLabels: {
            enabled: true,
            style: {
              fontSize: '8px',
            },
          }
        },
      },
      {
        breakpoint: 2064,
        options: {
          chart: {
            width: 200,
            height: 200,
          },
        },
      },
      {
        breakpoint: 2298,
        options: {
          chart: {
            width: 230,
            height: 230,
          },
        },
      },
      {
        breakpoint: 4000,
        options: {
          chart: {
            width: 250,
            height: 250,
          },
          dataLabels: {
            enabled: true,
            style: {
              fontSize: '15px',
            },
          },
        },
      },
    ],
    isHidden: false
  },
  distributionByStatusLoading: false,
  isCanExport: false
};

export const manageAnalyticsReducer = createReducer(
  initialStates,
  on(fromActions$.fetchAnalyticsFilteringCriteriaSuccess, (state, props) => {
    const { analyticsReportTabs, filterCategoryDisabled, filterChannelDisabled } = AnalyticReportUtils.generateAnalyticsReportTabs(props);
    return { ...state, filterCriteria: props.filterCriteria, distributionCriteria: props.distributionCriteria, analyticsReportTabs, filterCategoryDisabled, filterChannelDisabled };

  }),
  on(fromActions$.fetchClientFillerListSuccess, (state, props) => {
    return { ...state, fillers: props.fillerList };
  }),
  on(fromActions$.fetchVolumeGraph, (state, props) => {
    return { ...state, filterOption: props.filterOptionModel, requestedAt: props.requestedAt };
  }),
  on(fromActions$.fetchVolumeReceiveGraph, (state) => {
    return {
      ...state,
      volumeReceivedGraph: {
        ...state.volumeReceivedGraph,
        empty: true,
        fetching: true,
      },
    };
  }),
  on(fromActions$.fetchProductionProgress, (state, props) => {
    return {
      ...state,
      productionProgresses: {
        ...state.productionProgresses,
        isFetching: true,
      }
    }
  }),
  on(fromActions$.fetchProductionProgressSuccess, (state, props) => {

    const content: ProductionProgressModel[] = props.productionProgress.map(item => {

      let color = '';
      switch (item.key) {
        case 'Postal': {
          color = 'rgb(0, 32, 96)';
          break;
        }
        case 'Email': {
          color = 'rgb(150, 78, 161)';
          break;
        }
        case 'SMS': {
          color = 'rgb(255, 78, 131)';
          break;
        }
      }

      return {
        label: item.key,
        value: item.value,
        volume: item.volume,
        color,
      };
    });

    return { ...state, productionProgresses: { content, isFetching: false } };
  }),
  on(fromActions$.fetchProductionProgressFail, (state) => {
    return { ...state, productionProgresses: { content: [], isFetching: false }};
  }),
  on(fromActions$.fetchVolumeReceiveGraphSuccess, (state, props) => {
    const volumeReceivedGraph: VolumeReceiveGraph = {
      ...state.volumeReceivedGraph,
      empty: props.volumeReceive.every((volume) => volume.value === 0),
      fetching: false,
      results: props.volumeReceive.map((result) => ({
        name: result.key,
        value: result.value,
        volume: result?.volume
      })),
      scheme: {
        ...state.volumeReceivedGraph.scheme,
        domain: props.volumeReceive.map((volume) => {
          switch (volume.key) {
            case 'Postal':
              return 'rgb(0, 32, 96)';
            case 'Email':
              return 'rgb(150, 78, 161)';
            case 'SMS':
              return 'rgb(255, 78, 131)';
            default:
              return 'gray';
          }
        }),
      },

      graphLabels: props.volumeReceive.map((result) => result.key),
    };

    const refreshFetchGraphDate = new Date(state.requestedAt);

    return {
      ...state,
      volumeReceive: props.volumeReceive,
      refreshFetchGraphDate,
      volumeReceivedGraph,
    };
  }),
  on(fromActions$.fetchVolumeReceiveGraphFail, (state) => {
    return { ...state, volumeReceivedGraph: initialStates.volumeReceivedGraph };
  }),
  on(fromActions$.fetchGlobalProductionDetailsGraph, (state) => {
    return { ... state, globalProductionDetails:  { ...state.globalProductionDetails, loading: true } };
  }),
  on(fromActions$.fetchGlobalProductionDetailsGraphSuccess, (state, props) => {
    return { ...state, globalProductionDetails: { ...props.productionDetails, loading: false }};
  }),
  on(fromActions$.fetchGlobalProductionDetailsGraphFail, (state) => {
    return { ...state, globalProductionDetails: initialStates.globalProductionDetails };
  }),
  on(fromActions$.fetchProductionDetailsGraph, (state) => {
    return { ... state, productionDetails:  { ...state?.productionDetails, loading: true } };
  }),
  on(fromActions$.fetchProductionDetailsGraphSuccess, (state, props) => {
    return { ...state, productionDetails: { ...props?.productionDetails, loading: false }};
  }),
  on(fromActions$.fetchProductionDetailsGraphFail, (state) => {
    return { ...state, productionDetails: initialStates?.productionDetails };
  }),
  on(fromActions$.fetchDistributionVolumeReceiveGraphSuccess, (state, props) => {

    const distributionVolumeReceivedGraph: VolumeReceiveGraph = {
      ...state.distributionVolumeReceivedGraph,
      empty: props.distributionVolumeReceive.every((volume) => volume.value === 0),
      fetching: false,
      results: props.distributionVolumeReceive.map((result) => ({
        name: result.key !== "Blank" ? result.key : props.message.blank,
        value: result.value,
        volume: result.volume
      })),
      scheme: {
        ...state.volumeReceivedGraph.scheme,
        domain: props.distributionVolumeReceive.map((volume, index) => {

          if (volume.key === "Blank") {
            return 'rgb(128,128,128)';
          } else if (volume.key === 'Total') {
            return 'rgb(0, 102, 204)';
          } else {
            return `#${Math.floor(Math.random() * 0xFFFFFF + 1).toString(16).padStart(6, '0')}`;
            // return colorRandom.getColor(index);
          }
        }),
      },
      graphLabels: props.distributionVolumeReceive.map((result) => result.key !== "Blank" ? result.key : props.message.blank),
    };

    const refreshFetchGraphDate = new Date(state.requestedAt);

    return {
      ...state,
      distributionVolumeReceived: props.distributionVolumeReceive,
      refreshFetchGraphDate,
      distributionVolumeReceivedGraph,
    };
  }),

  on(fromActions$.fetchDistributionVolumeReceiveGraph, (state, props) => {
    return { ...state,distributionVolumeReceivedGraph: {...state.volumeReceivedGraph,empty:true,fetching:true}, filterOption: props.filterOptionModel, requestedAt: props.requestedAt};
  }),
  on(fromActions$.fetchDistributionVolumeReceiveGraphFail, (state) => {
    return { ...state, distributionVolumeReceivedGraph: initialStates.volumeReceivedGraph };
  }),
  on(fromActions$.setPostalCriteriaToStore, (state, props) => {
    return { ...state, postalFilteringCriteria: props.filteringCriteria };
  }),
  on(fromActions$.fetchDistributionPNGGraph, (state, props) => {
    return { ...state, distributionPNDGraph: {...state.distributionPNDGraph }, requestedAt: props.requestedAt, distributionPNDLoading: true };
  }),
  on(fromActions$.fetchDistributionPNGGraphSuccess, (state, props) => {

    const data = props.distributionPND;

    const series = data.map(item => item.value);
    const labels = DistributionPNGGraphUtils.getLabels(props.distributionPND, props.messages);
    const colors = DistributionPNGGraphUtils.getColors(props.distributionPND, props.messages);

    const distributionPNDGraph: ChartOptions = {
      ...state.distributionPNDGraph,
      colors,
      series,
      labels,
      isHidden: false
    };

    const refreshFetchGraphDate = new Date(state.requestedAt);

    return {
      ...state,
      distributionPND: props.distributionPND,
      refreshFetchGraphDate,
      distributionPNDGraph:distributionPNDGraph,
      distributionPNDLoading: false,
    };
  }),
  on(fromActions$.fetchDistributionPNGGraphFail, (state) => {
    return {
      ...state,
      distributionPNDGraph: {
        ...initialStates.distributionPNDGraph,
        isHidden: true
      },
      distributionPNDLoading: false
    };
  }),
  on(fromActions$.fetchProcessedMailGraph, (state, props) => {
    return { ...state , requestedAt: props.requestedAt, processedMailLoading: true};
  }),
  on(fromActions$.fetchProcessedMailGraphFail, (state) => {
    return { ...state, processedMailGraph: initialStates.processedMailGraph, processedMailLoading: false };
  }),
  on(fromActions$.fetchProcessedMailGraphSuccess, (state, props) => {

    const colors = props.processedMail.map((volume,index) =>{
      if(volume.key=="PND"){
        return "rgb(255, 111, 0)";
      }else if(volume.key=="Non PND" || volume.key=="None PND"){
        return "rgb(0, 102, 204)";
      }else{
        return "rgb(128, 128, 128)";
      }
    });

    const series = props.processedMail.map((result) => ({
      name: result.key,
      data: [result.value]
    }));

    const processedMailGraph: ProcessedMailGraph = {
      ...state.processedMailGraph,
      series:series,
      colors:colors
    };

    const refreshFetchGraphDate = new Date(state.requestedAt);

    return {
      ...state,
      processedMail: props.processedMail,
      refreshFetchGraphDate,
      processedMailGraph:processedMailGraph,
      processedMailLoading: false
    };
  }),

  on(fromActions$.fetchDistributionByStatus, (state, props) => {
    return {...state, requestedAt: props.requestedAt, distributionByStatusLoading: true};
  }),
  on(fromActions$.fetchDistributionByStatusFail, (state) => {
    return {
      ...state,
      distributionByStatusGraph: {
        ... initialStates.distributionByStatusGraph,
        isHidden: true
      },
      distributionByStatusLoading: false
    };
  }),
  on(fromActions$.fetchDistributionByStatusSuccess, (state, props) => {


    const data = props.distributionByStatus;
    const series = data.map(item => item.value);
    let labels:any;
    let colors:any;

    if (location.pathname.includes(appRoute.cxmAnalytics.navigateToEmail)) {
      labels = DistributionByStatusUtil.getEmailLabels(props.distributionByStatus, props.messages);
      colors = DistributionByStatusUtil.getEmailColor(props.distributionByStatus, props.messages);
    } else {
      labels = DistributionByStatusUtil.getSmsLabels(props.distributionByStatus, props.messages);
      colors = DistributionByStatusUtil.getSmsColor(props.distributionByStatus, props.messages);
    }

    const distributionByStatusGraph: ChartOptions = {
      ...state.distributionByStatusGraph,
      colors,
      series,
      labels,
      isHidden: false
    };
    const refreshFetchGraphDate = new Date(state.requestedAt);

    return {
      ...state,
      distributionByStatus: props.distributionByStatus,
      refreshFetchGraphDate,
      distributionByStatusGraph:distributionByStatusGraph,
      distributionByStatusLoading: false,
    };
  }),
  on(fromActions$.exportCsvFile, (state, props) => {
    return {...state, requestedAt: props.requestedAt};
  }),

);
