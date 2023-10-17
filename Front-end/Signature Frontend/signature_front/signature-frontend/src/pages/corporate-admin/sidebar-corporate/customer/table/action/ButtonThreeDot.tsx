import {Localization} from '@/i18n/lan';
import {Route} from '@/constant/Route';
import {Divider, MenuItem} from '@mui/material';
import {useTranslation} from 'react-i18next';
import {NGEYE, NGUser} from '@assets/iconExport/Allicon';
import NGText from '@components/ng-text/NGText';
import {NGBinCycle} from '@assets/Icon';
import {useNavigate} from 'react-router-dom';
import {Navigate} from '@/utils/common';

interface ButtonThreeDotParticipantInterface {
  userId: number;
  onClose?: () => void;
  onDelete?: () => void;
}

function ButtonThreeDotParticipant({
  userId,
  onDelete,
}: ButtonThreeDotParticipantInterface) {
  const navigate = useNavigate();
  const {t} = useTranslation();
  return (
    <>
      <MenuItem
        sx={{py: 1}}
        onClick={() => {
          navigate(Navigate(`${Route.corporate.SIGNATURE_PROJECTS}/${userId}`));
        }}
        disableRipple>
        <NGEYE />
        <NGText text={t(Localization('list-action', 'access-to-projects'))} />
      </MenuItem>
      <Divider sx={{px: 1, '&.MuiDivider-root': {my: 0, mx: 0.5}}} />
      <MenuItem sx={{py: 1}} disableRipple>
        <NGUser />
        <NGText
          text={t(Localization('corporate-form-add-user', 'edit-user'))}
        />
      </MenuItem>
      <Divider sx={{px: 1, '&.MuiDivider-root': {my: 0, mx: 0.5}}} />
      <MenuItem
        sx={{py: 1, color: 'error.main'}}
        disableRipple
        onClick={onDelete}>
        <NGBinCycle />
        <NGText
          myStyle={{color: 'inherit'}}
          text={t(Localization('corporate-form-add-user', 'delete-user'))}
        />
      </MenuItem>
    </>
  );
}

export default ButtonThreeDotParticipant;
