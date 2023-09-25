import { PreferenceDistributionPNDGraphModel } from '@cxm-smartflow/analytics/data-access';

export class DistributionPNGGraphUtils {
  static getLabels(data: PreferenceDistributionPNDGraphModel[], messages: any): Array<string> {

    return data.map(item => {
      switch (item.key) {
        case 'Unknown': {
          return messages.recipient_unknown
        }

        case 'Address_failure': {
          return messages.access_or_addressing_failure;
        }

        case 'Unclaimed': {
          return messages.unclaimed;
        }

        case 'NPAI': {
          return messages.npai;
        }

        case 'Refused': {
          return messages.refused;
        }

        default: {
          return item.key;
        }
      }
    });
  }


  static getColors(data: PreferenceDistributionPNDGraphModel[], messages: any): Array<string> {

    return data.map(item => {
      switch (item.key) {
        case 'Unknown': {
          return 'rgb(255, 111, 0)';
        }

        case 'Address_failure': {
          return 'rgb(0, 102, 204)';
        }

        case 'Unclaimed': {
          return 'rgb(128, 128, 128)';
        }

        case 'NPAI': {
          return 'rgb(255, 192, 0)';
        }

        case 'Refused': {
          return 'rgb(95, 144, 70)';
        }

        default: {
          return 'rgb(128, 128, 128)';
        }
      }
    });
  }
}
