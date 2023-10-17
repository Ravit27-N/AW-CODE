import {NGLogout, NGRollback, NGStopTraffic} from '@assets/iconExport/Allicon';
import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';

/** validate on status box**/
export const NGSwitchCaseBoxStatus = ({
  key,
  label,
  value,
}: {
  /**  status form backend **/
  key: keyof Pick<ProjectStatusInterfaces, 'COMPLETED' | 'REFUSED'> | 'ABANDON';
  label: string;
  value: string | number;
}) => {
  switch (key) {
    case 'COMPLETED': {
      return {
        colorBorder: '#1C8752',
        title: label,
        time:
          (typeof value === 'string' ? 0 : value / 60).toFixed(0) + ' minutes',
        bgColor: '#EEF6F2',
        icon: <NGRollback fontSize="small" sx={{color: '#197B4A'}} />,
      };
    }
    case 'REFUSED': {
      return {
        colorBorder: '#CE0500',
        title: label,
        time: Math.round(typeof value === 'string' ? 0 : value) + ' %',
        bgColor: '#FEEEED',
        icon: <NGStopTraffic fontSize="small" sx={{color: '#CE0500'}} />,
      };
    }
    case 'ABANDON': {
      return {
        colorBorder: '#D14900',
        title: label,
        time: Math.round(typeof value === 'string' ? 0 : value) + ' %',
        bgColor: '#FEF2ED',
        icon: <NGLogout fontSize="small" sx={{color: '#D14900'}} />,
      };
    }
    default: {
      return {
        colorBorder: 'white',
        title: 'UNKNOWN' + label,
        time: '0 %',
        bgColor: 'white',
        icon: <NGStopTraffic fontSize="small" sx={{color: 'white'}} />,
      };
    }
  }
};
