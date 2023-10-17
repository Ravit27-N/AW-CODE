import {IGetDashboard} from '@/redux/slides/corporate-admin/corporateSettingSlide';

export const initialCorporate: IGetDashboard = {
  startDate: '2023-07-01T00:00:00Z',
  endDate: '2023-07-31T00:00:00Z',
  totalProjects: 0,
  contents: {
    cards: [
      {
        id: 'COMPLETED',
        label: 'Temps moyen de signture',
        value: 100.0,
      },
      {
        id: 'ABANDON',
        label: 'Taux d’abandon de dossiers',
        value: 20.0,
      },
      {
        id: 'REFUSED',
        label: 'Taux de refus de signer',
        value: 0.0,
      },
    ],
    statuses: [
      {
        id: 'IN_PROGRESS',
        label: 'URGENT',
        value: 10.0,
      },
      {
        id: 'URGENT',
        label: 'URGENT',
        value: 20.0,
      },
      {
        id: 'REFUSED',
        label: 'URGENT',
        value: 30.0,
      },
      {
        id: 'COMPLETED',
        label: 'URGENT',
        value: 40.0,
      },
      {
        id: 'DRAFT',
        label: 'URGENT',
        value: 50.0,
      },
      // {
      //   id: 'ABANDON',
      //   label: 'URGENT',
      //   value: 0.0,
      // },
      // {
      //   id: 'EXPIRED',
      //   label: 'URGENT',
      //   value: 0.0,
      // },
    ],
  },
};

export const validateCard = (s: 'COMPLETED' | 'REFUSED' | 'ABANDON'): any => {
  const status = {
    ['COMPLETED']: 'average-signing',
    ['REFUSED']: 'signing-refuse',
    ['ABANDON']: 'abandon',
  };

  return status[s];
};
