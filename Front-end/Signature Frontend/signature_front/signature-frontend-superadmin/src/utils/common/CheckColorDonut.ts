import {ProjectStatusInterfaces} from '@components/ng-switch-case-status/interface';

export const CheckColorDonut = ({
  statusId,
}: {
  statusId: keyof ProjectStatusInterfaces | 'URGENT';
}) => {
  switch (statusId) {
    case 'IN_PROGRESS':
      return '#3892FF';
    case 'REFUSED':
      return '#F38E57';
    case 'DRAFT':
      return '#676767';
    case 'COMPLETED':
      return '#2EB571';
    case 'URGENT':
      return '#DA3D39';
  }
};
