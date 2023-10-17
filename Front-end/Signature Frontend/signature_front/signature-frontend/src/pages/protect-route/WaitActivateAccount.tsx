import {Localization} from '@/i18n/lan';
import {useAuthActivateMutation} from '@/redux/slides/project-management/user';
import {router} from '@/router';
import {Center} from '@/theme';
import {Navigate} from '@/utils/common';
import {NGButton} from '@components/ng-button/NGButton';
import NGText from '@components/ng-text/NGText';
import {Route} from '@constant/Route';
import {FigmaBody} from '@constant/style/themFigma/Body';
import InvitationLayout from '@pages/participant/invitation/InvitationLayout';
import {useTranslation} from 'react-i18next';

function WaitActivateAccount({resetToken}: {resetToken: string}) {
  const {t} = useTranslation();
  const [authActivate] = useAuthActivateMutation();
  const handleAuthActivate = async (): Promise<void> => {
    try {
      await authActivate({
        resetToken,
      }).unwrap();
      await router.navigate(Navigate(Route.LOGIN));
    } catch (e) {
      /** empty */
    }
  };

  return (
    <InvitationLayout>
      <Center height={'100vh'} spacing={5}>
        <NGText
          text={t(Localization('form', 'click-activate'))}
          myStyle={{...FigmaBody.BodyMediumBold}}
        />
        <NGButton
          title={t(Localization('form', 'activate'))}
          onClick={handleAuthActivate}
        />
      </Center>
    </InvitationLayout>
  );
}

export default WaitActivateAccount;
