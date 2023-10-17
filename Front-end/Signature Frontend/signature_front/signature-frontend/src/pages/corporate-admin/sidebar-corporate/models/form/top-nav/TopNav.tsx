import {NGLeftArrowIcon, NGRightArrowIcon} from '@/assets/Icon';
import NGText from '@/components/ng-text/NGText';
import {Localization} from '@/i18n/lan';
import {pixelToRem} from '@/utils/common/pxToRem';
import KeyboardArrowRightIcon from '@mui/icons-material/KeyboardArrowRight';
import {
  Button,
  Stack,
  Step,
  StepLabel,
  Stepper,
  Typography,
} from '@mui/material';
import {t} from 'i18next';
import React from 'react';

const steps = ['detail', 'signature-scenario', 'parameter'];

const style = {
  muiActive: {
    '& .MuiSvgIcon-root.Mui-active': {
      color: 'Primary.main',
      borderStyle: 'none',
    },
  },
  muiComplete: {
    '& .MuiSvgIcon-root.Mui-completed': {
      color: '#1C8752',
      borderStyle: 'none',
      backgroundColor: '#ffffff',
    },
  },
  // muiArrowNext: {
  //   '& .css-kyqqjw-MuiSvgIcon-root': {
  //     color: '#ffffff',
  //   },
  // },
  muiStepIcon: {
    '& .MuiStepIcon-root': {
      borderStyle: 'solid',
      borderColor: '#ffffff',
      borderWidth: '1px',
      borderRadius: '50%',
    },
  },
};

type ITopNav = {
  activeStep: number;
  setActiveStep: React.Dispatch<React.SetStateAction<number>>;
  setTrigger: React.Dispatch<React.SetStateAction<boolean>>;
};

const TopNav = (props: ITopNav) => {
  const {setActiveStep, activeStep, setTrigger} = props;
  const [completed, setCompleted] = React.useState<{
    [k: number]: boolean;
  }>({0: false, 1: false, 2: false, 3: false});

  const handleComplete = React.useCallback(() => {
    const defaultStep: {[k: number]: boolean} = {
      0: false,
      1: false,
      2: false,
      3: false,
    };
    for (let i = 0; i < activeStep; i++) {
      defaultStep[i] = true;
    }
    setCompleted(defaultStep);
  }, [activeStep]);

  React.useEffect(() => {
    handleComplete();
  }, [activeStep]);

  return (
    <Stack
      height="60px"
      width="100%"
      bgcolor="#121232"
      direction="row"
      alignItems="center"
      justifyContent="space-between">
      <Stack width="227px" direction="row" alignItems="center">
        <Stack width="60px" alignItems="center">
          <NGLeftArrowIcon
            sx={{mt: '3px', cursor: 'pointer'}}
            onClick={() => {
              if (activeStep > 0) {
                setActiveStep(p => p - 1);
              } else {
                setTrigger(false);
              }
            }}
          />
        </Stack>
        <Typography
          sx={{
            fontWeight: 700,
            fontSize: '17px',
            color: '#ffffff',
            textDecorationLine: 'underline',
            fontFamily: 'Poppins',
          }}>
          {t(Localization('models-corporate', 'new-model'))}
        </Typography>
      </Stack>
      <Stack>
        <Stepper
          connector={
            <KeyboardArrowRightIcon sx={{opacity: '100%', color: 'white'}} />
          }
          nonLinear
          activeStep={activeStep}
          sx={{
            width: '100%',
            justifyContent: 'center',
            ...style.muiActive,
            ...style.muiComplete,
            ...style.muiStepIcon,
            // ...style.muiArrowNext,
          }}>
          {steps.map((label, index) => (
            <Step key={label} completed={completed[index]}>
              <StepLabel>
                <NGText
                  text={t(
                    Localization(
                      'models-corporate',
                      label as 'detail' | 'signature-scenario' | 'parameter',
                    ),
                  )}
                  myStyle={{
                    color: 'primary.contrastText',
                    fontSize: pixelToRem(12),
                  }}
                />
              </StepLabel>
            </Step>
          ))}
        </Stepper>
      </Stack>
      <Button
        variant="contained"
        onClick={() => {
          if (activeStep < steps.length - 1) {
            setActiveStep(p => p + 1);
          }
        }}
        endIcon={
          <NGRightArrowIcon
            sx={{
              mt: '6px',
            }}
          />
        }
        sx={{
          textTransform: 'none',
          fontFamily: 'Poppins',
          fontSize: '11px',
          fontWeight: 600,
          mr: '20px',
        }}>
        {t(Localization('models-corporate', 'next'))}
      </Button>
    </Stack>
  );
};

export default TopNav;
