import {FONT_TYPE, UNKOWNERROR} from '@/constant/NGContant';
import {Localization} from '@/i18n/lan';
import {useRedirectResetPasswordMutation} from '@/redux/slides/project-management/user';
import {Backdrop, CircularProgress, Stack, Typography} from '@mui/material';
import InvitationLayout from '@pages/participant/invitation/InvitationLayout';
import React from 'react';
import {useTranslation} from 'react-i18next';
import {useParams} from 'react-router-dom';

interface TypeResetPasswordActivate {
  setResetToken: any;
  setIsError: any;
}

const ResetPasswordActivate = ({
  setResetToken,
  setIsError,
}: TypeResetPasswordActivate) => {
  const {t} = useTranslation();
  const {token} = useParams();
  const [resetPasswordActivate, {isLoading, error, data}] =
    useRedirectResetPasswordMutation();
  const generateError = (s: 400 | 498, m?: string): string => {
    const status = {
      400: t(Localization('status', 'no-longer')),
      498: t(Localization('status', 'expired')),
    };
    return status[400] ?? UNKOWNERROR;
  };

  const handleResetPasswordActivate = async (): Promise<void> => {
    await resetPasswordActivate({
      token: token!,
    }).unwrap();
    await setIsError(false);
  };

  React.useEffect(() => {
    setIsError(true);
    handleResetPasswordActivate().then();
  }, []);
  React.useEffect(() => {
    if (data) {
      setResetToken(data.resetToken);
    }
  }, [data]);
  return (
    <InvitationLayout>
      <Stack alignItems="center" height="100vh" justifyContent="center">
        {error && (
          <Typography
            sx={{
              borderLeft: '1px solid #00000060',
              px: '20px',
              fontSize: 18,
              fontFamily: FONT_TYPE.POPPINS,
            }}>
            {(error as any).data
              ? generateError(
                  (error as any).data.error.statusCode as 400 | 498,
                  (error as any).data.error.message,
                )
              : generateError((error as any).status)}
          </Typography>
        )}
        <Backdrop
          sx={{color: '#fff', zIndex: theme => theme.zIndex.drawer + 1}}
          open={isLoading}>
          <CircularProgress color="inherit" />
        </Backdrop>
      </Stack>
    </InvitationLayout>
  );
};

export default ResetPasswordActivate;
