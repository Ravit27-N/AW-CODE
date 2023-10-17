import {NGMinus, NGPlus} from '@/assets/Icon';
import {FONT_TYPE} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {store} from '@/redux';
import {pixelToRem} from '@/utils/common/pxToRem';
import {
  Card,
  CardContent,
  IconButton,
  InputAdornment,
  OutlinedInput,
  Radio,
  Stack,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import React from 'react';
import {
  Control,
  Controller,
  FieldErrors,
  UseFormGetValues,
  UseFormSetValue,
} from 'react-hook-form';
import {IPayloadScenario} from './CreateModel';

type IModelDetail = {
  control: Control<IPayloadScenario, any>;
  errors: FieldErrors<IPayloadScenario>;
  setValue: UseFormSetValue<IPayloadScenario>;
  getValues: UseFormGetValues<IPayloadScenario>;
};

const ModelScenario = (props: IModelDetail) => {
  const {control, errors, setValue, getValues} = props;
  const {createModel} = store.getState().authentication;
  const theme = store.getState().enterprise.theme[0];
  const [tag, setTag] = React.useState<1 | 2>(getValues('tag'));

  React.useEffect(() => {
    setValue('tag', tag);
  }, [tag]);

  React.useEffect(() => {
    if (createModel) {
      const {approval, signature, recipient, signProcess} = createModel;

      setValue('approval', approval);
      setValue('signature', signature);
      setValue('recipient', recipient);
      setValue('tag', signProcess === 'cosign' || !signProcess ? 1 : 2);
      setTag(signProcess === 'cosign' || !signProcess ? 1 : 2);
    }
  }, [createModel]);

  return (
    <Stack p="60px 270px">
      <Card sx={{p: '40px', width: '100%'}}>
        <CardContent
          sx={{
            p: 0,
          }}>
          <Stack gap="40px">
            <Stack gap="6px">
              <Typography
                sx={{
                  fontSize: '18px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'create-a-project'))}
              </Typography>
              <Typography
                sx={{
                  fontSize: '12px',
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'add-participant-by-role'))}
              </Typography>
            </Stack>

            <Stack gap="16px">
              <Typography
                sx={{
                  fontSize: '14px',
                  fontWeight: 600,
                  fontFamily: 'Poppins',
                }}>
                {t(Localization('models-corporate', 'number-participant'))}
              </Typography>
              <Stack gap="18px" direction="row" flexWrap="wrap">
                {/* Number of signatories */}
                <Stack gap="8px" width="261px">
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(
                      Localization('models-corporate', 'number-of-signatories'),
                    )}
                  </Typography>
                  <Controller
                    control={control}
                    render={({field: {onChange, onBlur, value}}) => (
                      <OutlinedInput
                        type="tel"
                        size="small"
                        color="secondary"
                        error={!!errors}
                        fullWidth={true}
                        inputProps={{
                          sx: {
                            textAlign: 'center',
                          },
                        }}
                        sx={{
                          '& input::placeholder': {
                            fontSize: pixelToRem(14),
                          },
                          '&.MuiOutlinedInput-root': {
                            '& .MuiOutlinedInput-notchedOutline': {
                              borderColor:
                                value > 0 ? 'Primary.main' : '#E9E9E9',
                              borderWidth: '0.3px',
                            },

                            '& fieldset': {
                              borderColor: '#000000',
                            },
                            '&:hover fieldset': {
                              borderColor: '#E9E9E9',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#E9E9E9',
                              borderWidth: '0.2px',
                            },
                          },
                        }}
                        value={Number(value.toString())}
                        onBlur={onBlur}
                        onPaste={e => e.preventDefault()}
                        onChange={onChange}
                        onKeyPress={e => {
                          if (!/\d/.test(e.nativeEvent.key)) {
                            e.preventDefault();
                          }
                        }}
                        style={{
                          fontFamily: FONT_TYPE.POPPINS,
                          fontSize: 14,
                          fontWeight: 400,
                        }}
                        startAdornment={
                          <InputAdornment position="start">
                            <IconButton
                              onClick={() =>
                                value > 0 ? onChange(Number(value) - 1) : value
                              }
                              aria-label="toggle password visibility"
                              edge="start">
                              <NGMinus
                                sx={{fontSize: '12px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                        endAdornment={
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => onChange(Number(value) + 1)}
                              aria-label="toggle password visibility"
                              edge="end">
                              <NGPlus
                                sx={{fontSize: '11px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                      />
                    )}
                    name="signature"
                  />
                  {errors && (
                    <Typography
                      paragraph
                      sx={{color: 'red', fontSize: 12, margin: 0}}>
                      {errors.signature?.message}
                    </Typography>
                  )}
                </Stack>

                {/* Number of approves */}
                <Stack gap="8px" width="261px">
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'number-of-approvers'))}
                  </Typography>
                  <Controller
                    control={control}
                    render={({field: {onChange, onBlur, value}}) => (
                      <OutlinedInput
                        type="tel"
                        size="small"
                        color="secondary"
                        error={!!errors}
                        fullWidth={true}
                        inputProps={{
                          sx: {
                            textAlign: 'center',
                          },
                        }}
                        sx={{
                          '& input::placeholder': {
                            fontSize: pixelToRem(14),
                          },
                          '&.MuiOutlinedInput-root': {
                            '& .MuiOutlinedInput-notchedOutline': {
                              borderColor:
                                value > 0 ? 'Primary.main' : '#E9E9E9',
                              borderWidth: '0.3px',
                            },

                            '& fieldset': {
                              borderColor: '#000000',
                            },
                            '&:hover fieldset': {
                              borderColor: '#E9E9E9',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#E9E9E9',
                              borderWidth: '0.2px',
                            },
                          },
                        }}
                        value={Number(value.toString())}
                        onBlur={onBlur}
                        onPaste={e => e.preventDefault()}
                        onChange={onChange}
                        onKeyPress={e => {
                          if (!/\d/.test(e.nativeEvent.key)) {
                            e.preventDefault();
                          }
                        }}
                        style={{
                          fontFamily: FONT_TYPE.POPPINS,
                          fontSize: 14,
                          fontWeight: 400,
                        }}
                        startAdornment={
                          <InputAdornment position="start">
                            <IconButton
                              onClick={() =>
                                value > 0 ? onChange(Number(value) - 1) : value
                              }
                              aria-label="toggle password visibility"
                              edge="start">
                              <NGMinus
                                sx={{fontSize: '12px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                        endAdornment={
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => onChange(Number(value) + 1)}
                              aria-label="toggle password visibility"
                              edge="end">
                              <NGPlus
                                sx={{fontSize: '11px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                      />
                    )}
                    name="approval"
                  />
                  {errors && (
                    <Typography
                      paragraph
                      sx={{color: 'red', fontSize: 12, margin: 0}}>
                      {errors.signature?.message}
                    </Typography>
                  )}
                </Stack>

                {/* Number of recipients */}
                <Stack gap="8px" width="261px">
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(
                      Localization('models-corporate', 'number-of-recipients'),
                    )}
                  </Typography>
                  <Controller
                    control={control}
                    render={({field: {onChange, onBlur, value}}) => (
                      <OutlinedInput
                        type="tel"
                        size="small"
                        color="secondary"
                        error={!!errors}
                        fullWidth={true}
                        inputProps={{
                          sx: {
                            textAlign: 'center',
                          },
                        }}
                        sx={{
                          '& input::placeholder': {
                            fontSize: pixelToRem(14),
                          },
                          '&.MuiOutlinedInput-root': {
                            '& .MuiOutlinedInput-notchedOutline': {
                              borderColor:
                                value > 0 ? 'Primary.main' : '#E9E9E9',
                              borderWidth: '0.3px',
                            },

                            '& fieldset': {
                              borderColor: '#000000',
                            },
                            '&:hover fieldset': {
                              borderColor: '#E9E9E9',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#E9E9E9',
                              borderWidth: '0.2px',
                            },
                          },
                        }}
                        value={Number(value.toString())}
                        onBlur={onBlur}
                        onPaste={e => e.preventDefault()}
                        onChange={onChange}
                        onKeyPress={e => {
                          if (!/\d/.test(e.nativeEvent.key)) {
                            e.preventDefault();
                          }
                        }}
                        style={{
                          fontFamily: FONT_TYPE.POPPINS,
                          fontSize: 14,
                          fontWeight: 400,
                        }}
                        startAdornment={
                          <InputAdornment position="start">
                            <IconButton
                              onClick={() =>
                                value > 0 ? onChange(Number(value) - 1) : value
                              }
                              aria-label="toggle password visibility"
                              edge="start">
                              <NGMinus
                                sx={{fontSize: '12px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                        endAdornment={
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => onChange(Number(value) + 1)}
                              aria-label="toggle password visibility"
                              edge="end">
                              <NGPlus
                                sx={{fontSize: '11px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                      />
                    )}
                    name="recipient"
                  />
                  {errors && (
                    <Typography
                      paragraph
                      sx={{color: 'red', fontSize: 12, margin: 0}}>
                      {errors.signature?.message}
                    </Typography>
                  )}
                </Stack>

                {/* Number of viewers */}
                <Stack gap="8px" width="261px">
                  <Typography
                    sx={{
                      fontSize: '12px',
                      fontFamily: 'Poppins',
                    }}>
                    {t(Localization('models-corporate', 'number-of-viewers'))}
                  </Typography>
                  <Controller
                    control={control}
                    render={({field: {onChange, onBlur, value}}) => (
                      <OutlinedInput
                        type="tel"
                        size="small"
                        color="secondary"
                        error={!!errors}
                        fullWidth={true}
                        inputProps={{
                          sx: {
                            textAlign: 'center',
                          },
                        }}
                        sx={{
                          '& input::placeholder': {
                            fontSize: pixelToRem(14),
                          },
                          '&.MuiOutlinedInput-root': {
                            '& .MuiOutlinedInput-notchedOutline': {
                              borderColor:
                                value > 0 ? 'Primary.main' : '#E9E9E9',
                              borderWidth: '0.3px',
                            },

                            '& fieldset': {
                              borderColor: '#000000',
                            },
                            '&:hover fieldset': {
                              borderColor: '#E9E9E9',
                            },
                            '&.Mui-focused fieldset': {
                              borderColor: '#E9E9E9',
                              borderWidth: '0.2px',
                            },
                          },
                        }}
                        value={Number(value.toString())}
                        onBlur={onBlur}
                        onPaste={e => e.preventDefault()}
                        onChange={onChange}
                        onKeyPress={e => {
                          if (!/\d/.test(e.nativeEvent.key)) {
                            e.preventDefault();
                          }
                        }}
                        style={{
                          fontFamily: FONT_TYPE.POPPINS,
                          fontSize: 14,
                          fontWeight: 400,
                        }}
                        startAdornment={
                          <InputAdornment position="start">
                            <IconButton
                              onClick={() =>
                                value > 0 ? onChange(Number(value) - 1) : value
                              }
                              aria-label="toggle password visibility"
                              edge="start">
                              <NGMinus
                                sx={{fontSize: '12px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                        endAdornment={
                          <InputAdornment position="end">
                            <IconButton
                              onClick={() => onChange(Number(value) + 1)}
                              aria-label="toggle password visibility"
                              edge="end">
                              <NGPlus
                                sx={{fontSize: '11px', color: '#000000'}}
                              />
                            </IconButton>
                          </InputAdornment>
                        }
                      />
                    )}
                    name="viewer"
                  />
                  {errors && (
                    <Typography
                      paragraph
                      sx={{color: 'red', fontSize: 12, margin: 0}}>
                      {errors.signature?.message}
                    </Typography>
                  )}
                </Stack>
              </Stack>
            </Stack>

            <Stack gap="18px" direction="row" flexWrap="wrap">
              {/* Co-signature */}
              <IconButton
                onClick={() => setTag(1)}
                sx={{color: '#000000', p: 0}}
                disableFocusRipple
                disableTouchRipple
                disableRipple>
                <Stack
                  width="401px"
                  height="100px"
                  direction="row"
                  justifyContent="space-between"
                  border={`0.5px solid ${
                    tag === 1 ? theme.mainColor : '#E9E9E9'
                  }`}
                  borderRadius="6px">
                  <Stack
                    gap="4px"
                    alignItems="start"
                    sx={{
                      position: 'relative',
                      top: '20px',
                      left: '20px',
                    }}>
                    <Typography
                      sx={{
                        fontWeight: 600,
                        fontSize: '14px',
                        fontFamily: 'Poppins',
                      }}>
                      {t(Localization('models-corporate', 'co-signature'))}
                    </Typography>
                    <Typography
                      sx={{
                        fontWeight: 300,
                        fontSize: '12px',
                        fontFamily: 'Poppins',
                      }}>
                      {t(Localization('models-corporate', 'join-signatories'))}
                    </Typography>
                  </Stack>

                  <Radio
                    checked={tag === 1}
                    sx={{
                      position: 'relative',
                      top: 32,
                      right: '23px',
                      height: '30px',
                    }}
                    value={1}
                    name="radio-buttons"
                    inputProps={{'aria-label': 'A'}}
                  />
                </Stack>
              </IconButton>

              {/* Counter-signature */}
              <IconButton
                onClick={() => setTag(2)}
                sx={{color: '#000000', p: 0}}
                disableFocusRipple
                disableTouchRipple
                disableRipple>
                <Stack
                  width="401px"
                  height="100px"
                  direction="row"
                  justifyContent="space-between"
                  border={`0.5px solid ${
                    tag === 2 ? theme.mainColor : '#E9E9E9'
                  }`}
                  borderRadius="6px">
                  <Stack
                    gap="4px"
                    alignItems="start"
                    sx={{
                      position: 'relative',
                      top: '20px',
                      left: '20px',
                    }}>
                    <Typography
                      sx={{
                        fontWeight: 600,
                        fontSize: '14px',
                        fontFamily: 'Poppins',
                      }}>
                      {t(Localization('models-corporate', 'counter-signature'))}
                    </Typography>
                    <Typography
                      sx={{
                        fontWeight: 300,
                        fontSize: '12px',
                        fontFamily: 'Poppins',
                      }}>
                      {t(Localization('models-corporate', 'after-sign'))}
                    </Typography>
                  </Stack>

                  <Radio
                    checked={tag === 2}
                    sx={{
                      position: 'relative',
                      top: 32,
                      right: '23px',
                      height: '30px',
                    }}
                    value={2}
                    name="radio-buttons"
                    inputProps={{'aria-label': 'A'}}
                  />
                </Stack>
              </IconButton>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Stack>
  );
};

export default ModelScenario;
