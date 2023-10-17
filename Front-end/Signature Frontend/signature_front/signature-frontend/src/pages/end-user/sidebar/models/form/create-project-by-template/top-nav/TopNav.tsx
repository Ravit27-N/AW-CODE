import {NGLeftArrowIcon, NGRightArrowIcon} from '@/assets/Icon';
import NGText from '@/components/ng-text/NGText';
import {Route} from '@/constant/Route';
import {Localization} from '@/i18n/lan';
import {useAppDispatch} from '@/redux/config/hooks';
import {
  clearApproval,
  clearReceptient,
  clearSignatory,
  resetToInitial,
} from '@/redux/slides/authentication/authenticationSlide';
import {router} from '@/router';
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
import {ISubmitIndex} from '../CreateProject';

const steps = ['preparation', 'shipping'];

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
  setSubmitIndex: React.Dispatch<React.SetStateAction<ISubmitIndex>>;
};

const TopNav = (props: ITopNav) => {
  const {setActiveStep, activeStep, setTrigger, setSubmitIndex} = props;
  const dispatch = useAppDispatch();
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
      sx={{
        position: 'relative',
      }}
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
                dispatch(resetToInitial());
                dispatch(clearSignatory());
                dispatch(clearApproval());
                dispatch(clearReceptient());
                setTrigger(false);
                router.navigate(Route.MODEL);
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

      <Stack
        sx={{
          position: 'absolute',
          transform: 'translate(-50%, -50%)',
          top: '50%',
          left: '50%',
        }}>
        <Stepper
          connector={
            <KeyboardArrowRightIcon sx={{opacity: '100%', color: 'white'}} />
          }
          nonLinear
          activeStep={activeStep}
          sx={{
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
                      label as 'preparation' | 'shipping',
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
          if (activeStep === 0) {
            setSubmitIndex(prev => ({
              ...prev,
              preparation: true,
              shipping: false,
            }));
          } else if (activeStep === 1) {
            setSubmitIndex(prev => ({
              ...prev,
              preparation: false,
              shipping: true,
            }));
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
