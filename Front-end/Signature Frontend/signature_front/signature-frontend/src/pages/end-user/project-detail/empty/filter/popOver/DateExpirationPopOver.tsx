import React, {useEffect, useState} from 'react';
import {MenuItem, Select, SelectChangeEvent, Stack} from '@mui/material';
import {BootstrapInput} from '@/theme';
import {LocalizationProvider} from '@mui/x-date-pickers/LocalizationProvider';
import {AdapterDayjs} from '@mui/x-date-pickers/AdapterDayjs';
import {CalendarPicker} from '@mui/x-date-pickers';
import {setStartEndDate} from '@/redux/counter/CounterSlice';
import dayjs, {Dayjs} from 'dayjs';
import {ISelect} from '@pages/end-user/project-detail/empty/HeroProjectDetail';
import {FilterBy} from '@/constant/NGContant';
import {useAppDispatch, useAppSelector} from '@/redux/config/hooks';
import {useTranslation} from 'react-i18next';
import {Localization} from '@/i18n/lan';

function DateExpirationPopOver({
  setSelection,
}: {
  setSelection: React.Dispatch<React.SetStateAction<ISelect>>;
}) {
  const startEndDate = useAppSelector(state => state.counter.startEndDate);
  const {theme} = useAppSelector(state => state.enterprise);
  const dispatch = useAppDispatch();
  const {t} = useTranslation();
  const [dateStart, setDateStart] = React.useState<Dayjs | null>(
    dayjs(
      new Date(Date.now())
        .toLocaleDateString('zh-Hans-CN', {
          month: '2-digit',
          day: '2-digit',
          year: 'numeric',
        })
        .replace(/\//g, '-'),
    ),
  );

  const [dateFinish, setDateFinish] = React.useState<Dayjs | null>(
    dayjs(
      new Date(Date.now())
        .toLocaleDateString('zh-Hans-CN', {
          month: '2-digit',
          day: '2-digit',
          year: 'numeric',
        })
        .replace(/\//g, '-'),
    ),
  );
  const [select, setSelect] = useState<ISelect>({
    active: 0,
    items: [
      {
        key: 0,
        project: t(Localization('drop-down', 'precise-date')),
        selectedFilterBy: FilterBy.ENDUSER,
      },
      {
        key: 1,
        project: t(Localization('drop-down', 'period')),
        selectedFilterBy: FilterBy.DEPARTMENT,
      },
    ],
  });
  React.useEffect(() => {
    setSelection(prev => ({...prev, active: select.active}));
  }, [select]);

  const handleChangeSelect = (e: SelectChangeEvent) => {
    setSelect({...select, active: e.target.value});
  };
  useEffect(() => {
    dispatch(
      setStartEndDate({
        start: dateStart,
        end: startEndDate.end ?? dateStart,
      }),
    );
  }, [dateStart]);
  return (
    <Stack spacing={2} p={2}>
      <Select
        sx={{
          '& .MuiInputBase-input': {
            width: '100%',
            gap: '8px',
            fontFamily: 'Poppins',
            padding: '8px 12px 8px 10px',
            fontSize: '14px',
            fontWeight: 500,
            borderColor: theme[0].mainColor,
            '&:focus': {
              borderColor: theme[0].mainColor,
            },
          },
        }}
        input={<BootstrapInput />}
        value={select.active.toString()}
        onChange={handleChangeSelect}
        displayEmpty
        inputProps={{'aria-label': 'Without label'}}>
        {select.items.map(item => (
          <MenuItem
            key={item.key}
            value={item.key}
            sx={{
              fontSize: '14px',
              fontWeight: 500,
              fontFamily: 'Poppins',
            }}>
            {item.project}
          </MenuItem>
        ))}
      </Select>
      <Stack>
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <CalendarPicker
            disableHighlightToday={true}
            date={startEndDate.start ?? dateStart}
            onChange={newDate => {
              setDateStart(newDate);
              dispatch(
                setStartEndDate({
                  start: newDate,
                  end: select.active === 0 ? null : newDate,
                }),
              );
            }}
          />
        </LocalizationProvider>
        {select.active === 1 && (
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <CalendarPicker
              disableHighlightToday={true}
              minDate={dateStart}
              date={startEndDate.end ?? dateFinish}
              onChange={newDate => {
                const d = newDate!
                  .set('hour', 23)
                  .set('minute', 59)
                  .set('second', 59);
                setDateFinish(d);
                dispatch(
                  setStartEndDate({
                    start: startEndDate.start,
                    end: newDate,
                  }),
                );
              }}
            />
          </LocalizationProvider>
        )}
      </Stack>
    </Stack>
  );
}

export default DateExpirationPopOver;
