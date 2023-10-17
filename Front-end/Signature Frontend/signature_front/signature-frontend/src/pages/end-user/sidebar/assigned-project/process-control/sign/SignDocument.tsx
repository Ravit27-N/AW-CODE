import logo from '@/assets/image/LOGO.png';
import NGDialog from '@/components/ng-dialog-corporate/NGDialog';
import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useAppSelector} from '@/redux/config/hooks';
import {IGetFlowId} from '@/redux/slides/project-management/project';
import {ArrowBack} from '@mui/icons-material';
import {TabContext, TabPanel} from '@mui/lab';
import {
  Box,
  Button,
  IconButton,
  MenuItem,
  Paper,
  Select,
  SelectChangeEvent,
  Stack,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import React from 'react';

type ISignDocument = {
  open: boolean;
  projectFlowId: IGetFlowId;
  submit: () => Promise<void>;
};

const SignDocument = (props: ISignDocument) => {
  const {open, projectFlowId, submit} = props;
  const [value, setValue] = React.useState('1');
  const [select, setSelect] = React.useState<string>('italianno');

  const handleChange = (event: React.SyntheticEvent, newValue: string) => {
    setValue(newValue);
  };

  const handleSelectChange = (e: SelectChangeEvent): void => {
    setSelect(e.target.value);
  };

  const menuItems: {
    key: 'italianno' | 'poppins' | '';
    font: 'Italianno' | 'Poppins';
  }[] = [
    {key: 'italianno', font: 'Italianno'},
    {key: 'poppins', font: 'Poppins'},
  ];

  return (
    <NGDialog
      open={open}
      fullScreen
      sxProp={{
        titleSx: {
          p: 0,
        },
        contentsSx: {
          p: 0,
        },
      }}
      titleDialog={
        <Stack>
          <Header projectName={projectFlowId.projectName} />
        </Stack>
      }
      contentDialog={
        <Stack
          sx={{
            bgcolor: '#F0F1F3',
          }}>
          <Stack
            height="44px"
            px="18px"
            justifyContent="center"
            alignItems="flex-start">
            <IconButton
              disableFocusRipple
              disableRipple
              disableTouchRipple
              onClick={() => window.history.go(0)}>
              <Stack direction="row" alignItems="center" gap="10px">
                <ArrowBack
                  sx={{
                    fontSize: '20px',
                    color: 'Primary.main',
                  }}
                />
                <Typography
                  sx={{
                    fontSize: 13,
                    fontFamily: FONT_TYPE.POPPINS,
                    fontWeight: 600,
                    color: '#000000',
                  }}>
                  {t(Localization('text', 'Revenir au document'))}
                </Typography>
              </Stack>
            </IconButton>
          </Stack>

          <Stack
            width="100%"
            height={`calc(100vh - 100px)`}
            justifyContent="center"
            alignItems="center">
            <Stack width="549px" height="571px" gap="29px" alignItems="center">
              <Typography
                sx={{
                  fontSize: 27,
                  fontFamily: FONT_TYPE.POPPINS,
                  fontWeight: 600,
                }}>
                {t(Localization('end-user-assigned-project', 'sign-doc'))}
              </Typography>

              <Paper
                elevation={1}
                sx={{
                  '&.MuiPaper-root': {
                    borderRadius: '16px',
                  },
                  width: '483px',
                  height: '388px',
                  alignItems: 'center',
                }}>
                <Stack p="14px 10px 0px 10px" alignItems="center" gap="6px">
                  <Typography
                    sx={{
                      fontSize: 14,
                      fontFamily: FONT_TYPE.POPPINS,
                    }}>
                    {t(Localization('text', 'Choisissez votre mode signature'))}
                  </Typography>
                  <TabContext value={value}>
                    <Box sx={{borderBottom: 1, borderColor: 'divider'}}>
                      {/*<TabList*/}
                      {/*  sx={{*/}
                      {/*    width: '463px',*/}
                      {/*  }}*/}
                      {/*  onChange={handleChange}*/}
                      {/*  aria-label="lab API tabs example">*/}
                      {/*  <Tab*/}
                      {/*    sx={{*/}
                      {/*      width: '145px',*/}
                      {/*      textTransform: 'none',*/}
                      {/*      fontFamily: FONT_TYPE.POPPINS,*/}
                      {/*      fontWeight: 500,*/}
                      {/*      flexGrow: 1,*/}
                      {/*      alignItems: 'center',*/}
                      {/*    }}*/}
                      {/*    label={t(Localization('text', 'Écrire'))}*/}
                      {/*    value="1"*/}
                      {/*  />*/}
                      {/*  <Tab*/}
                      {/*    sx={{*/}
                      {/*      width: '145px',*/}
                      {/*      textTransform: 'none',*/}
                      {/*      fontFamily: FONT_TYPE.POPPINS,*/}
                      {/*      fontWeight: 500,*/}
                      {/*      flexGrow: 1,*/}
                      {/*    }}*/}
                      {/*    label={t(Localization('text', 'Dessiner'))}*/}
                      {/*    value="2"*/}
                      {/*  />*/}
                      {/*  <Tab*/}
                      {/*    sx={{*/}
                      {/*      width: '145px',*/}
                      {/*      textTransform: 'none',*/}
                      {/*      fontFamily: FONT_TYPE.POPPINS,*/}
                      {/*      fontWeight: 500,*/}
                      {/*      flexGrow: 1,*/}
                      {/*      alignItems: 'center',*/}
                      {/*    }}*/}
                      {/*    label={t(Localization('text', 'Importer'))}*/}
                      {/*    value="3"*/}
                      {/*  />*/}
                      {/*</TabList>*/}
                    </Box>
                    {/* Écrire */}
                    <TabPanel
                      value="1"
                      sx={{
                        width: '100%',
                      }}>
                      <Stack gap="6px">
                        <Typography
                          sx={{
                            fontSize: 14,
                            fontFamily: FONT_TYPE.POPPINS,
                          }}>
                          {t(Localization('text', 'Choix de la typographie'))}
                        </Typography>
                        <Select
                          size="small"
                          value={select}
                          onChange={handleSelectChange}
                          displayEmpty
                          sx={{
                            flexGrow: 1,
                            '&.MuiInputBase-root': {
                              color: 'black.main', // set the color of the text
                              fieldset: {
                                borderColor: 'Primary.main',
                              },
                              '&.Mui-focused fieldset': {
                                borderColor: select.length
                                  ? 'Primary.main'
                                  : 'inherit',
                                borderWidth: '0.2px',
                              },
                              '& .MuiSelect-icon': {
                                color: 'black.main', // set the color of the arrow icon
                              },
                            },
                          }}
                          inputProps={{'aria-label': 'Without label'}}>
                          {menuItems.map(item => (
                            <MenuItem key={item.key} value={item.key}>
                              {item.font}
                            </MenuItem>
                          ))}
                        </Select>
                        <Stack
                          sx={{height: '200px', width: '100%'}}
                          justifyContent={'center'}
                          alignItems={'center'}>
                          <Typography
                            sx={{fontFamily: select, fontSize: '40px'}}>
                            {`${projectFlowId.actor?.firstName} ${projectFlowId.actor?.lastName}`}
                          </Typography>
                          <Typography sx={{fontSize: '14px', mt: 2}}>
                            {t(
                              Localization(
                                'end-user-assigned-project',
                                'enter-your-first-last',
                              ),
                            )}
                          </Typography>
                        </Stack>
                      </Stack>
                    </TabPanel>
                    {/* Dessiner */}
                    <TabPanel value="2">Item Two</TabPanel>
                    {/* Importer */}
                    <TabPanel value="3">Item Three</TabPanel>
                  </TabContext>
                </Stack>
              </Paper>

              <Button
                variant="contained"
                onClick={submit}
                sx={{
                  p: '16px 32px',
                  width: '327px',
                  textTransform: 'none',
                  fontSize: '16px',
                  fontWeight: 600,
                }}>
                {t(Localization('form', 'accept-and-sign'))}
              </Button>
            </Stack>
          </Stack>
        </Stack>
      }
    />
  );
};

type IHeader = {
  projectName: string;
};

export const Header = (props: IHeader) => {
  const {projectName} = props;
  const theme = useAppSelector(state => state.enterprise.theme);
  return (
    <Stack
      p="6px "
      justifyContent="space-between"
      alignItems="center"
      direction="row"
      sx={{
        position: 'relative',
      }}
      height="56px">
      <img
        src={`${theme[0].logo ?? logo}`}
        alt={`LOGO`}
        loading="lazy"
        width={'auto'}
        height={'32px'}
        style={{
          paddingLeft: '15px',
        }}
      />
      <Typography
        sx={{
          position: 'absolute',
          transform: 'translate(-50%, -50%)',
          top: '50%',
          left: '50%',
          fontSize: 18,
          fontFamily: FONT_TYPE.POPPINS,
          fontWeight: 600,
        }}>
        {projectName}
      </Typography>
      <Typography>{''}</Typography>
    </Stack>
  );
};

export default SignDocument;
