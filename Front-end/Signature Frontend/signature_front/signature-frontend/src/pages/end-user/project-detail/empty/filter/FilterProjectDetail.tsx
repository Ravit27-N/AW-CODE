import React from 'react';
import {Button, Stack} from '@mui/material';
import {NGFilter} from '@assets/Icon';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';
import NGText from '@components/ng-text/NGText';
import {
  NGCalendarTableParticipant,
  NGSetting,
} from '@assets/iconExport/ExportIcon';
import {useAppSelector} from '@/redux/config/hooks';
import {FigmaBody} from '@/constant/style/themFigma/Body';
import NGPopOver from '@components/ng-popover/NGPopOver';
import {ItemMenu} from '@pages/end-user/project-detail/empty/filter/ItemMenu';
import StatusPopOver from '@pages/end-user/project-detail/empty/filter/popOver/StatusPopOver';
import DateExpirationPopOver from '@pages/end-user/project-detail/empty/filter/popOver/DateExpirationPopOver';
import {StatusesInterface} from '@/utils/request/interface/Project.interface';
import {ITabs} from '@pages/end-user/project-detail/empty/TabProjectDetail';
import {ISelect} from '@pages/end-user/project-detail/empty/HeroProjectDetail';

interface FilterProjectDetailInterface {
  allStatus?: StatusesInterface[];
  setArrayOfStatus: React.Dispatch<React.SetStateAction<ITabs[]>>;
  tab: ITabs;
  setSelection?: React.Dispatch<React.SetStateAction<ISelect>>;
}

function FilterProjectDetail({
  allStatus,
  setArrayOfStatus,
  tab,
  setSelection,
}: FilterProjectDetailInterface) {
  const {t} = useTranslation();

  const {theme} = useAppSelector(state => state.enterprise);

  return (
    <div>
      <NGPopOver
        contain={
          <Stack
            display={tab !== 'ALL-PROJECTS' ? 'none' : 'flex'}
            width={'208px'}
            height={'96px'}
            borderRadius={'6px'}
            justifyContent={'center'}>
            {/** Item Menu Status  **/}

            <NGPopOver
              horizontal={'right'}
              horizontalT={'left'}
              verticalT={'top'}
              vertical={'top'}
              Sx={{ml: 2}}
              contain={
                <StatusPopOver
                  allStatus={allStatus}
                  setArrayOfStatus={setArrayOfStatus}
                  tab={tab}
                />
              }
              button={
                <ItemMenu
                  key={'status'}
                  icon={<NGSetting sx={{mb: 1}} />}
                  text={
                    <NGText
                      text={t(Localization('project-detail', 'statut'))}
                      myStyle={{...FigmaBody.BodyMedium}}
                    />
                  }
                  theme={theme}
                />
              }
            />
            {/** Item Menu Date Expiration  **/}
            <NGPopOver
              horizontal={'left'}
              horizontalT={'left'}
              verticalT={'top'}
              vertical={'bottom'}
              Sx={{ml: 2}}
              contain={<DateExpirationPopOver setSelection={setSelection!} />}
              button={
                <ItemMenu
                  key={'expiration'}
                  icon={<NGCalendarTableParticipant />}
                  text={
                    <NGText
                      // text={'Date expiration'}
                      text={t(Localization('project-detail', 'expiry-date'))}
                      myStyle={{...FigmaBody.BodyMedium}}
                    />
                  }
                  theme={theme}
                />
              }
            />
          </Stack>
        }
        button={
          <Button
            startIcon={
              <NGFilter fontSize="small" sx={{mt: '5px', mr: '-8px'}} />
            }
            variant="outlined"
            disabled={tab !== 'ALL-PROJECTS'}
            sx={{
              height: '40px',
              fontFamily: 'Poppins',
              fontWeight: 600,
              color: '#000000',
              width: '89px',
              fontSize: '11px',
              borderColor: '#000000',
              textTransform: 'none',
              boxShadow: 0,
              ':hover': {
                borderColor: 'Black.main',
              },
            }}>
            {t(Localization('project-detail', 'filter'))}
          </Button>
        }
      />
    </div>
  );
}

export default FilterProjectDetail;
