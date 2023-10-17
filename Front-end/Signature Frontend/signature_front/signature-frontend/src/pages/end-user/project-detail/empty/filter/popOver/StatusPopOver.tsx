import React from 'react';
import {Checkbox, List, ListItem, Stack} from '@mui/material';
import {StatusesInterface} from '@/utils/request/interface/Project.interface';
import ListItemText from '@mui/material/ListItemText';
import {ITabs} from '@pages/end-user/project-detail/empty/TabProjectDetail';
import {StyleConstant} from '@/constant/style/StyleConstant';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';

function StatusPopOver({
  allStatus,
  setArrayOfStatus,
  tab,
}: {
  allStatus?: StatusesInterface[];
  setArrayOfStatus: React.Dispatch<React.SetStateAction<ITabs[]>>;
  tab: ITabs;
}) {
  const [checked, setChecked] = React.useState<ITabs[]>([]);
  const {t} = useTranslation();
  setArrayOfStatus(checked);
  return (
    <Stack
      direction={'row'}
      width={'138px'}
      height={'264px'}
      borderRadius={'6px'}
      padding={'12px'}
      sx={{
        overflow: 'hidden',
        overflowY: 'scroll',
        ...StyleConstant.scrollNormal,
      }}
      gap={'12px'}>
      <List
        dense
        sx={{width: '100%', maxWidth: 360, bgcolor: 'background.paper'}}>
        {allStatus
          ?.filter(it => it.label.toUpperCase() !== tab)
          .map(item => {
            const labelId = `checkbox-list-secondary-label-${item.id}`;
            return (
              <ListItem key={item.id} disablePadding>
                <Checkbox
                  edge="start"
                  value={item.id}
                  onChange={event => {
                    const isHave = checked.find(
                      item => item === event.target.value,
                    );
                    setChecked([...checked, event.target.value as ITabs]);
                    if (isHave) {
                      const newChecked = checked.filter(
                        item => item !== event.target.value,
                      );
                      setChecked(newChecked);
                    }
                  }}
                  checked={!!checked.find(it => it === item.id)}
                  inputProps={{'aria-labelledby': labelId}}
                />
                <ListItemText
                  id={labelId}
                  primary={t(
                    Localization(
                      'project-detail',
                      item.label.toString().toLowerCase() as any,
                    ),
                  )}
                  sx={{textAlign: 'start'}}
                />
              </ListItem>
            );
          })}
      </List>
    </Stack>
  );
}

export default StatusPopOver;
