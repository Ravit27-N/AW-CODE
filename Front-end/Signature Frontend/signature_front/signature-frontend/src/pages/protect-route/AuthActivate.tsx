import {FONT_TYPE, UNKOWNERROR} from '@/constant/NGContant';
import {Backdrop, CircularProgress, Stack, Typography} from '@mui/material';
import React from 'react';
import {useParams} from 'react-router-dom';
import InvitationLayout from '@pages/participant/invitation/InvitationLayout';
import {useUserInfoQuery} from '@/redux/slides/project-management/user';
import {Localization} from '@/i18n/lan';
import {useTranslation} from 'react-i18next';
import WaitActivateAccount from '@pages/protect-route/WaitActivateAccount';

const AuthActivate = () => {
  const {t} = useTranslation();
  const param = useParams() as {uuid: string};
  const getUserInfo = useUserInfoQuery({token: param.uuid});
  const generateError = (s: 400 | 498, m?: string): string => {
    const status = {
      400: t(Localization('status', 'no-longer')),
      498: t(Localization('status', 'expired')),
    };
    return status[s] ?? UNKOWNERROR;
  };

  return (
    <InvitationLayout>
      <Stack alignItems="center" height="100vh" justifyContent="center">
        {getUserInfo.isSuccess ? (
          <WaitActivateAccount
            resetToken={getUserInfo.currentData.resetToken}
          />
        ) : (
          getUserInfo.error && (
            <Typography
              sx={{
                borderLeft: '1px solid #00000060',
                px: '20px',
                fontSize: 18,
                fontFamily: FONT_TYPE.POPPINS,
              }}>
              {(getUserInfo.error as any).data
                ? generateError(
                    (getUserInfo.error as any).data.error.statusCode as
                      | 400
                      | 498,
                    (getUserInfo.error as any).data.error.message,
                  )
                : generateError((getUserInfo.error as any).status)}
            </Typography>
          )
        )}

        <Backdrop
          sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
          open={getUserInfo.isLoading}>
          <CircularProgress color="inherit" />
        </Backdrop>
      </Stack>
    </InvitationLayout>
  );
};

export default AuthActivate;
